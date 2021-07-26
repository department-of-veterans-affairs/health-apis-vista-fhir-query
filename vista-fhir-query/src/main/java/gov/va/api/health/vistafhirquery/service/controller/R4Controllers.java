package gov.va.api.health.vistafhirquery.service.controller;

import gov.va.api.health.ids.client.IdEncoder;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.WitnessProtection;
import java.util.List;

public class R4Controllers {

  /** Try to parse an identifier, else throw NotFound. */
  public static PatientTypeCoordinates patientTypeCoordinatesOrDie(
      WitnessProtection witnessProtection, String publicId) {
    try {
      return PatientTypeCoordinates.fromString(witnessProtection.toPrivateId(publicId));
    } catch (IdEncoder.BadId | IllegalArgumentException e) {
      throw ResourceExceptions.NotFound.because("Could not parse id: " + publicId);
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
