package gov.va.api.health.vistafhirquery.service.controller;

import java.util.List;

public class R4Controllers {
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
}
