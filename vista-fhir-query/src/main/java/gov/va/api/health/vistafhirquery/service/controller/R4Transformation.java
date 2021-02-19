package gov.va.api.health.vistafhirquery.service.controller;

import gov.va.api.health.r4.api.resources.Resource;
import gov.va.api.lighthouse.vistalink.models.TypeSafeRpcResponse;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.Builder;
import lombok.Getter;

/** Transformation represents the path a RpcResponse takes to become a FHIR representation. */
@Getter
@Builder
public class R4Transformation<
    RpcResponseT extends TypeSafeRpcResponse, ResourceT extends Resource> {

  /** Transform a RpcResponse object into a FHIR object. */
  private final Function<RpcResponseT, List<ResourceT>> toResource;

  private final Predicate<ResourceT> filters;

  public boolean hasFilter() {
    return filters() != null;
  }

  public static class R4TransformationBuilder<
      RpcResponseT extends TypeSafeRpcResponse, ResourceT extends Resource> {

    /**
     * Acts similar to the @Singular annotation. This method receives a Predicate and uses
     * `and(Predicate)` to join them into a super-predicate.
     */
    public R4TransformationBuilder<RpcResponseT, ResourceT> filter(
        Predicate<ResourceT> maybeFilter) {
      if (filters == null) {
        filters = maybeFilter;
      } else if (maybeFilter != null) {
        filters = filters.and(maybeFilter);
      }
      return this;
    }
  }
}
