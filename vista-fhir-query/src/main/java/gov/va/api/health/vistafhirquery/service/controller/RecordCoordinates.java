package gov.va.api.health.vistafhirquery.service.controller;

import static java.lang.String.format;
import static java.lang.String.join;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class RecordCoordinates {
  @NonNull String site;

  @NonNull String file;

  @NonNull String ien;

  /** Try to parse a string value to an identifier. */
  public static RecordCoordinates fromString(String identifier) {
    String[] parts = identifier.split(";", -1);
    if (parts.length != 3) {
      throw new IllegalArgumentException(
          format(
              "Expected %s (%s) to have three ';' separated parts, but found %d.",
              RecordCoordinates.class.getSimpleName(), identifier, parts.length));
    }
    return RecordCoordinates.builder().site(parts[0]).file(parts[1]).ien(parts[2]).build();
  }

  @Override
  public String toString() {
    return join(";", site(), file(), ien());
  }
}
