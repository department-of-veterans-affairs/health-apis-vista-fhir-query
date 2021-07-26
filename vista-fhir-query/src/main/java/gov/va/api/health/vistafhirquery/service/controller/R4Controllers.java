package gov.va.api.health.vistafhirquery.service.controller;

import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayResponse;
import java.util.List;

public class R4Controllers {
  /** If there are any errors that can be collected from the response, throw a fatal error. */
  public static void dieOnError(LhsLighthouseRpcGatewayResponse response) {
    var errors = response.collectErrors();
    if (errors.isEmpty()) {
      return;
    }
    throw new FatalServerError(response.toString());
  }

  /** Verifies that a list of resources has only one result and returns that result. */
  public static <R> R verifyAndGetResult(List<R> resources, String publicId) {
    if (resources == null) {
      throw ResourceExceptions.NotFound.because(publicId);
    }
    if (resources.size() > 1) {
      throw ResourceExceptions.ExpectationFailed.because(
          "Too many results returned. Expected 1 but found %d.", resources.size());
    }
    return resources.stream()
        .findFirst()
        .orElseThrow(() -> ResourceExceptions.NotFound.because(publicId));
  }

  /** Indicates a critical failure in server that the user cannot solve. */
  public static class FatalServerError extends RuntimeException {
    public FatalServerError(String message) {
      super(message);
    }
  }
}
