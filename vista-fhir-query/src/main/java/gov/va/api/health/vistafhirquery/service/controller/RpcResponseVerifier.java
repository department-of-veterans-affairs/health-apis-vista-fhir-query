package gov.va.api.health.vistafhirquery.service.controller;

import gov.va.api.lighthouse.charon.api.RpcInvocationResult;
import gov.va.api.lighthouse.charon.api.RpcResponse;
import java.util.List;
import lombok.experimental.UtilityClass;

/** Utility class for verifying vistalink api results and throwing exceptions if not acceptable. */
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

  /** Flavor of runtime exception for a vistalink api request failure. */
  public static class VistalinkApiRequestFailure extends RuntimeException {
    public VistalinkApiRequestFailure(String message) {
      super(message);
    }
  }
}
