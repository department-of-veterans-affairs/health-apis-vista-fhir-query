package gov.va.api.health.vistafhirquery.service.controller;

import gov.va.api.lighthouse.vistalink.api.RpcInvocationResult;
import gov.va.api.lighthouse.vistalink.api.RpcResponse;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RpcResponseVerifier {

  /**
   * Verify the Vistalink API results are good to go based on the overall status. This is not the
   * HTTP status code.
   */
  public static List<RpcInvocationResult> verifyAndReturnResults(RpcResponse response) {
    switch (response.status()) {
      case OK:
        return response.results();
      case NO_VISTAS_RESOLVED:
        return List.of();
      case VISTA_RESOLUTION_FAILURE:
        // Fall-Through
      case FAILED:
        throw new VistalinkApiRequestFailure(
            "Vistalink API RpcResponse Status: " + response.status().name());
      default:
        throw new IllegalStateException(
            "Invalid Vistalink API RpcResponse Status: " + response.status().name());
    }
  }

  public static class VistalinkApiRequestFailure extends RuntimeException {
    public VistalinkApiRequestFailure(String message) {
      super(message);
    }
  }
}
