package gov.va.api.health.vistafhirquery.service.controller.witnessprotection;

import static org.apache.commons.lang3.StringUtils.isBlank;

import gov.va.api.health.r4.api.elements.Reference;
import gov.va.api.health.r4.api.resources.Resource;
import gov.va.api.health.vistafhirquery.service.config.LinkProperties;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** ProtectedReferenceFactory. */
@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ProtectedReferenceFactory {

  private final LinkProperties linkProperties;

  /**
   * Create a new instance from an R4 reference if possible. See {@link #forUri(String,
   * Consumer,NewReferenceValueStrategy)}.
   */
  public Optional<ProtectedReference> forReference(Reference reference) {
    if (reference == null) {
      return Optional.empty();
    }
    return forUri(reference.reference(), reference::reference, replaceWithFullUrl());
  }

  /**
   * Create a new instance based on the resource's simple classname, e.g. Observation. The onUpdate
   * consumer will be invoked to apply the new public ID as part of witness protection.
   */
  public ProtectedReference forResource(
      @NonNull Resource resource, @NonNull Consumer<String> onUpdate) {
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
  public Optional<ProtectedReference> forUri(
      String uri, Consumer<String> onUpdate, NewReferenceValueStrategy newValue) {
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
                  var update =
                      ReferenceUpdate.builder()
                          .uriParts(parts)
                          .resourceType(resource)
                          .oldResourceId(id)
                          .newResourceId(s)
                          .build();
                  onUpdate.accept(newValue.apply(update));
                })
            .build());
  }

  /**
   * Return a strategy that will replace the ID only in the reference URI, leaving all other parts
   * of the untouched. E.g., /whatever/Foo/1234 becomes /whatever/Foo/ABCD.
   */
  public NewReferenceValueStrategy replaceIdOnly() {
    return update -> {
      String[] parts = update.uriParts();
      parts[parts.length - 1] = update.newResourceId();
      return String.join("/", parts);
    };
  }

  /**
   * Return a strategy that will replace the entire reference value with the full URL using
   * configured paging links. E.g., /whatever/Foo/1234 becomes
   * https://awesome.com/services/fhir/v0/r4/Foo/ABCD.
   */
  public NewReferenceValueStrategy replaceWithFullUrl() {
    return update -> linkProperties.r4().readUrl(update.resourceType(), update.newResourceId());
  }

  /** NewReferenceValueStrategy. */
  public interface NewReferenceValueStrategy extends Function<ReferenceUpdate, String> {}

  /** ReferenceUpdate. */
  @Builder
  public static class ReferenceUpdate {
    private final String[] uriParts;
    @Getter private final String resourceType;
    @Getter private final String oldResourceId;
    @Getter private final String newResourceId;

    public String[] uriParts() {
      return Arrays.copyOf(uriParts, uriParts.length);
    }
  }
}
