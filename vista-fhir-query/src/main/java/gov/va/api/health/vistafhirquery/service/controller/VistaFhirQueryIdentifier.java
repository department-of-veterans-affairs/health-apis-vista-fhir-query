package gov.va.api.health.vistafhirquery.service.controller;

import static java.lang.String.format;
import static java.lang.String.join;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class VistaFhirQueryIdentifier {
  @NonNull String patientIcn;

  @NonNull String vistaSiteNumber;

  @NonNull String vistaRecordIdentifier;

  /** Try to parse a string value to an identifier. */
  public static VistaFhirQueryIdentifier fromString(String identifier) {
    String[] parts = identifier.split("\\+", -1);
    if (parts.length != 3) {
      throw new IllegalArgumentException(
          format(
              "Expected %s to have 3 '+' separated parts, but found %d.",
              identifier, parts.length));
    }
    return VistaFhirQueryIdentifier.builder()
        .patientIcn(parts[0])
        .vistaSiteNumber(parts[1])
        .vistaRecordIdentifier(parts[2])
        .build();
  }

  @Override
  public String toString() {
    return join("+", patientIcn(), vistaSiteNumber(), vistaRecordIdentifier());
  }
}
