package gov.va.api.health.vistafhirquery.service.controller.witnessprotection;

import gov.va.api.health.ids.client.IdEncoder;
import gov.va.api.health.vistafhirquery.service.controller.PatientTypeCoordinates;
import gov.va.api.health.vistafhirquery.service.controller.ResourceExceptions;

/** Interface for translating publicId to a privateId. */
public interface WitnessProtection {
  /** Try to parse patient type coordinates given a public id. */
  default PatientTypeCoordinates toPatientTypeCoordinates(String publicId) {
    try {
      var privateId = toPrivateId(publicId);
      return PatientTypeCoordinates.fromString(privateId);
    } catch (IdEncoder.BadId | IllegalArgumentException e) {
      throw ResourceExceptions.NotFound.because(
          "Could not parse public id %s to %s.",
          publicId, PatientTypeCoordinates.class.getSimpleName());
    }
  }

  String toPrivateId(String publicId);
}
