package gov.va.api.health.vistafhirquery.service.controller;

import static java.lang.String.format;
import static java.lang.String.join;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class PatientTypeCoordinates {
  @NonNull String icn;

  @NonNull String siteId;

  @NonNull String recordId;

  /** Try to parse a string value to an identifier. */
  public static PatientTypeCoordinates fromString(String identifier) {
    String[] parts = identifier.split("\\+", -1);
    if (parts.length != 3) {
      throw new IllegalArgumentException(
          format(
              "Expected %s (%s) to have 3 '+' separated parts, but found %d.",
              PatientTypeCoordinates.class.getSimpleName(), identifier, parts.length));
    }
    return PatientTypeCoordinates.builder()
        .icn(parts[0])
        .siteId(parts[1])
        .recordId(parts[2])
        .build();
  }

  @Override
  public String toString() {
    return join("+", icn(), siteId(), recordId());
  }
}
