package gov.va.api.health.vistafhirquery.service.controller;

import gov.va.api.health.ids.client.IdEncoder;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.WitnessProtection;
import java.util.List;

public class R4Controllers {
  /** Try to parse a Segmented Vista Identifier, else throw NotFound. */
  public static SegmentedVistaIdentifier parseOrDie(
      WitnessProtection witnessProtection, String publicId) {
    try {
      return SegmentedVistaIdentifier.unpack(witnessProtection.toPrivateId(publicId));
    } catch (IdEncoder.BadId | IllegalArgumentException e) {
      throw ResourceExceptions.NotFound.because("Could not unpack id: " + publicId);
    }
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
}
