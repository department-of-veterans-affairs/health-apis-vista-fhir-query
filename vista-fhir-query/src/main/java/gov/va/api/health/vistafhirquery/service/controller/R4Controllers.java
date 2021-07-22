package gov.va.api.health.vistafhirquery.service.controller;

import gov.va.api.health.ids.client.IdEncoder;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.WitnessProtection;
import java.util.List;
import java.util.Map;

public class R4Controllers {
  /** Try to parse a Segmented Vista Identifier, else throw NotFound. */
  public static SegmentedVistaIdentifier parseOrDie(
      WitnessProtection witnessProtection,
      String publicId,
      Map<Character, VistaIdentifierFormat> formats) {
    try {
      return SegmentedVistaIdentifier.unpack(witnessProtection.toPrivateId(publicId), formats);
    } catch (IdEncoder.BadId | IllegalArgumentException e) {
      throw ResourceExceptions.NotFound.because("Could not unpack id: " + publicId);
    }
  }

  public static SegmentedVistaIdentifier parseOrDie(
      WitnessProtection witnessProtection, String publicId) {
    return parseOrDie(witnessProtection, publicId, null);
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
