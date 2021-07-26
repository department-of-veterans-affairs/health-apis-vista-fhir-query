package gov.va.api.health.vistafhirquery.service.controller.witnessprotection;

import gov.va.api.health.ids.client.IdEncoder;
import gov.va.api.health.vistafhirquery.service.controller.PatientTypeCoordinates;
import gov.va.api.health.vistafhirquery.service.controller.RecordCoordinates;
import gov.va.api.health.vistafhirquery.service.controller.ResourceExceptions;
import java.util.function.Function;

/** Interface for translating publicId to a privateId. */
public interface WitnessProtection {
  /** Try to parse patient type coordinates given a public id. */
  default PatientTypeCoordinates toPatientTypeCoordinates(String publicId) {
    return decodePrivateId(publicId, PatientTypeCoordinates::fromString);
  }

  default RecordCoordinates toRecordCoordinates(String publicId) {
    return decodePrivateId(publicId, RecordCoordinates::fromString);
  }

  default <T> T decodePrivateId(String publicId, Function<String, T> decoder) {
    try {
      return decoder.apply(toPrivateId(publicId));
    } catch (IdEncoder.BadId | IllegalArgumentException e) {
      throw ResourceExceptions.NotFound.because("Unsupported id %s", publicId);
    }
  }

  String toPrivateId(String publicId);
}
