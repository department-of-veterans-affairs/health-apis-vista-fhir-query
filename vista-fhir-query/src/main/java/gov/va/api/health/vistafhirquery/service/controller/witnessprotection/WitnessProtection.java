package gov.va.api.health.vistafhirquery.service.controller.witnessprotection;

import gov.va.api.health.ids.client.IdEncoder;
import gov.va.api.health.vistafhirquery.service.controller.PatientTypeCoordinates;
import gov.va.api.health.vistafhirquery.service.controller.RecordCoordinates;
import gov.va.api.health.vistafhirquery.service.controller.ResourceExceptions;
import java.util.function.Function;

/** Interface for translating publicId to a privateId. */
public interface WitnessProtection {
  /**
   * Attempt to decode a private ID using a given transformer. BadId and IllegalArgumentExceptions
   * will result in a NotFound exception.
   */
  default <T> T decodePrivateId(String publicId, Function<String, T> decoder) {
    try {
      return decoder.apply(toPrivateId(publicId));
    } catch (IdEncoder.BadId | IllegalArgumentException e) {
      throw ResourceExceptions.NotFound.because("Unsupported id %s", publicId);
    }
  }

  /** Try to parse patient type coordinates given a public id. */
  default PatientTypeCoordinates toPatientTypeCoordinates(String publicId) {
    return decodePrivateId(publicId, PatientTypeCoordinates::fromString);
  }

  String toPrivateId(String publicId);

  /** Try to parse record coordinates given a public id. */
  default RecordCoordinates toRecordCoordinates(String publicId) {
    return decodePrivateId(publicId, RecordCoordinates::fromString);
  }
}
