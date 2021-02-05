package gov.va.api.health.vistafhirquery.service.controller;

import gov.va.api.health.r4.api.resources.Resource;
import gov.va.api.lighthouse.vistalink.models.TypeSafeRpcResponse;
import java.util.List;
import java.util.function.Function;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/** Transformation represents the path a RpcResponse takes to become a FHIR representation. */
@Slf4j
@Getter
@Builder
public class R4Transformation<
    RpcResponseT extends TypeSafeRpcResponse, ResourceT extends Resource> {

  /** Transform a RpcResponse object into a FHIR object. */
  private final Function<RpcResponseT, List<ResourceT>> toResource;

  public Function<RpcResponseT, List<ResourceT>> toResource() {
    log.info("ToDo: Implement WitnessProtection");
    return toResource;
  }
}
