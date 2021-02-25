package gov.va.api.health.vistafhirquery.service.controller.witnessprotection;

import static org.apache.commons.lang3.StringUtils.isBlank;

import gov.va.api.health.ids.api.ResourceIdentity;
import gov.va.api.health.r4.api.elements.Reference;
import gov.va.api.health.r4.api.resources.Resource;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class ProtectedReference {

  public static final String VISTA_SYSTEM = "VISTA";

  private final String type;

  private final String id;

  @ToString.Exclude private final Consumer<String> onUpdate;

  /**
   * Create a new instance from an R4 reference if possible. See {@link #forUri(String, Consumer)}.
   */
  public static Optional<ProtectedReference> forReference(Reference reference) {
    return forUri(reference.reference(), reference::reference);
  }

  /**
   * Create a new instance based on the resource's simple classname, e.g. Observation. The onUpdate
   * consumer will be invoked to apply the new public ID as part of witness protection.
   */
  public static ProtectedReference forResource(Resource resource, Consumer<String> onUpdate) {
    return ProtectedReference.builder()
        .type(resource.getClass().getSimpleName())
        .id(resource.id())
        .onUpdate(onUpdate)
        .build();
  }

  /**
   * Create a new instance based on a URI where the 2nd to last path element is the type and last
   * path element is the private ID, e.g. http://anything.com/some/stuff/Observation/12345. The
   * onUpdate consumer will be invoked to apply the new public ID as part of witness protection.
   *
   * <p>If the URI cannot be parsed into resource/id, then an empty reference is returned.
   */
  public static Optional<ProtectedReference> forUri(String uri, Consumer<String> onUpdate) {
    if (isBlank(uri)) {
      return Optional.empty();
    }
    String[] parts = uri.split("/", -1);
    if (parts.length < 2) {
      return Optional.empty();
    }
    String resource = parts[parts.length - 2];
    String id = parts[parts.length - 1];
    if (isBlank(resource) || isBlank(id)) {
      return Optional.empty();
    }
    return Optional.of(
        ProtectedReference.builder()
            .type(resource)
            .id(id)
            .onUpdate(
                s -> {
                  parts[parts.length - 1] = s;
                  String newUri = String.join("/", parts);
                  onUpdate.accept(newUri);
                })
            .build());
  }

  /** Convert this object into a resource identity. This method always returns a non-empty value. */
  public Optional<ResourceIdentity> asResourceIdentity() {
    return Optional.of(
        ResourceIdentity.builder().system(VISTA_SYSTEM).resource(type).identifier(id).build());
  }

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  public void updateId(Optional<String> id) {
    onUpdate.accept(id.orElse(null));
  }
}
