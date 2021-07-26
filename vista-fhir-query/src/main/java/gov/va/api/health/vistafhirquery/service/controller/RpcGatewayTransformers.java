package gov.va.api.health.vistafhirquery.service.controller;

import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.isBlank;

import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayResponse.Values;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RpcGatewayTransformers {

  /** Return true if the internal value is not available or blank. */
  @Deprecated
  public static boolean isInternalValueBlank(Values values) {
    return isBlank(values) || isBlank(values.in());
  }

  /** Return true if the internal value is available and not blank. */
  @Deprecated
  public static boolean isInternalValueNotBlank(Values values) {
    return !isInternalValueBlank(values);
  }
}
