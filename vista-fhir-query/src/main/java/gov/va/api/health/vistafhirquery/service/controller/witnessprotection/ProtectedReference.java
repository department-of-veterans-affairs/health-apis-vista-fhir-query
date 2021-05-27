package gov.va.api.health.vistafhirquery.service.controller.witnessprotection;

import gov.va.api.health.ids.api.ResourceIdentity;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/** Protected reference of a resource. */
@Builder
@Getter
@ToString
public class ProtectedReference {

  public static final String VISTA_SYSTEM = "VISTA";

  private final String type;

  private final String id;

  @ToString.Exclude private final Consumer<String> onUpdate;

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
