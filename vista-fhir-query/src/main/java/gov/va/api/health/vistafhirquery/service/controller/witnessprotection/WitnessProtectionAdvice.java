package gov.va.api.health.vistafhirquery.service.controller.witnessprotection;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.concat;

import gov.va.api.health.ids.api.IdentityService;
import gov.va.api.health.ids.api.IdentitySubstitution;
import gov.va.api.health.ids.api.ResourceIdentity;
import gov.va.api.health.r4.api.bundle.AbstractBundle;
import gov.va.api.health.r4.api.bundle.AbstractEntry;
import gov.va.api.health.r4.api.resources.Resource;
import gov.va.api.health.vistafhirquery.service.controller.ResourceExceptions.NotFound;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Controller advice is automatically applied to Bundle and Resource responses. It will lookup the
 * appropriate witness protection agent based on the resource type and substitute references to
 * private IDs to public Ids.
 */
@Slf4j
@ControllerAdvice
public class WitnessProtectionAdvice extends IdentitySubstitution<ProtectedReference>
    implements ResponseBodyAdvice<Object>, WitnessProtection {

  private final ProtectedReferenceFactory protectedReferenceFactory;

  private final AlternatePatientIds alternatePatientIds;

  private final Map<Type, WitnessProtectionAgent<?>> agents;

  /** Create a new instance. */
  @Builder
  @Autowired
  public WitnessProtectionAdvice(
      @NonNull ProtectedReferenceFactory protectedReferenceFactory,
      @NonNull AlternatePatientIds alternatePatientIds,
      @NonNull IdentityService identityService,
      List<WitnessProtectionAgent<?>> availableAgents) {
    super(identityService, ProtectedReference::asResourceIdentity, NotFound::new);
    this.protectedReferenceFactory = protectedReferenceFactory;
    this.alternatePatientIds = alternatePatientIds;
    this.agents =
        availableAgents.stream().collect(toMap(WitnessProtectionAdvice::agentType, identity()));
    log.info(
        "Witness protection is available for {}",
        agents.keySet().stream().map(t -> ((Class<?>) t).getSimpleName()).collect(joining(", ")));
  }

  private static Type agentType(WitnessProtectionAgent<?> agent) {
    Type agentInterface =
        Stream.of(agent.getClass().getGenericInterfaces())
            .filter(
                type -> type.getTypeName().startsWith(WitnessProtectionAgent.class.getName() + "<"))
            .findFirst()
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        agent.getClass() + " is not a " + WitnessProtectionAgent.class.getName()));
    return ((ParameterizedType) agentInterface).getActualTypeArguments()[0];
  }

  @SuppressWarnings("NullableProblems")
  @Override
  public Object beforeBodyWrite(
      Object body,
      MethodParameter returnType,
      MediaType selectedContentType,
      Class<? extends HttpMessageConverter<?>> selectedConverterType,
      ServerHttpRequest request,
      ServerHttpResponse response) {
    return protect(body);
  }

  <T> T protect(T body) {
    if (body instanceof AbstractBundle<?>) {
      protectBundle((AbstractBundle<?>) body);
    } else if (body instanceof Resource) {
      protectResource((Resource) body);
    }
    return body;
  }

  private void protectBundle(AbstractBundle<?> bundle) {
    bundle.entry().forEach(this::protectEntry);
  }

  private void protectEntry(AbstractEntry<?> entry) {
    Optional<ProtectedReference> referenceToFullUrl =
        protectedReferenceFactory.forUri(
            entry.fullUrl(), entry::fullUrl, protectedReferenceFactory.replaceIdOnly());
    protectResource(
        entry.resource(),
        referenceToFullUrl.isEmpty() ? List.of() : List.of(referenceToFullUrl.get()));
  }

  private void protectResource(
      @NonNull Resource resource, List<ProtectedReference> additionalReferences) {
    /*
     * We need to work around the compiler safeguards a little here. Since we are in control of the
     * map, we populate the key to be type of the agent. From the map is guaranteed to be type
     * matched. We will have capture compiler errors if we include the generic type <?>. Instead, we
     * will masquerade all agents as Resource typed instead of the more specific subclass.
     */
    @SuppressWarnings("unchecked")
    WitnessProtectionAgent<Resource> agent =
        (WitnessProtectionAgent<Resource>) agents.get(resource.getClass());
    if (agent == null) {
      log.warn("Witness protection agent not found for {}", resource.getClass());
      return;
    }

    Operations<Resource, ProtectedReference> operations =
        Operations.<Resource, ProtectedReference>builder()
            .toReferences(
                rsrc ->
                    concat(additionalReferences.stream(), agent.referencesOf(rsrc))
                        .map(this::restorePublicPatientIds))
            .isReplaceable(reference -> true)
            .resourceNameOf(ProtectedReference::type)
            .privateIdOf(ProtectedReference::id)
            .updatePrivateIdToPublicId(ProtectedReference::updateId)
            .build();
    IdentityMapping identities = register(List.of(resource), operations.toReferences());
    identities.replacePrivateIdsWithPublicIds(List.of(resource), operations);
  }

  private void protectResource(@NonNull Resource resource) {
    protectResource(resource, List.of());
  }

  private ProtectedReference restorePublicPatientIds(ProtectedReference reference) {
    if (!"Patient".equals(reference.type())) {
      return reference;
    }
    String publicId = alternatePatientIds.toPublicId(reference.id());
    ProtectedReference referenceWithPublicId =
        ProtectedReference.builder()
            .type(reference.type())
            .onUpdate(reference.onUpdate())
            .id(publicId)
            .build();
    referenceWithPublicId.onUpdate().accept(publicId);
    return referenceWithPublicId;
  }

  @Override
  public boolean supports(
      MethodParameter returnType,
      @org.springframework.lang.NonNull Class<? extends HttpMessageConverter<?>> converterType) {
    return AbstractBundle.class.isAssignableFrom(returnType.getParameterType())
        || Resource.class.isAssignableFrom(returnType.getParameterType());
  }

  @Override
  public String toPrivateId(String publicId) {
    return identityService.lookup(publicId).stream()
        .map(ResourceIdentity::identifier)
        .findFirst()
        .orElse(publicId);
  }
}
