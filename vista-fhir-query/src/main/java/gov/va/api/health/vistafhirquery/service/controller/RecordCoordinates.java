package gov.va.api.health.vistafhirquery.service.controller;

import static java.lang.String.format;
import static java.lang.String.join;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class RecordCoordinates {
  @NonNull String file;

  @NonNull String ien;

  /** Try to parse a string value to an identifier. */
  public static RecordCoordinates fromString(String identifier) {
    String[] parts = identifier.split(";", -1);
    if (parts.length != 2) {
      throw new IllegalArgumentException(
          format(
              "Expected %s (%s) to have 2 ';' separated parts, but found %d.",
              RecordCoordinates.class.getSimpleName(), identifier, parts.length));
    }
    return RecordCoordinates.builder().file(parts[0]).ien(parts[1]).build();
  }

  @Override
  public String toString() {
    return join(";", file(), ien());
  }
}
