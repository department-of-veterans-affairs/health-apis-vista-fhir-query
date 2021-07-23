package gov.va.api.health.vistafhirquery.service.controller;

import static java.lang.String.format;
import static java.lang.String.join;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class ProviderTypeCoordinates {
  @NonNull String siteId;

  @NonNull String recordId;

  /** Try to parse a string value to an identifier. */
  public static ProviderTypeCoordinates fromString(String identifier) {
    String[] parts = identifier.split("\\+", -1);
    if (parts.length != 2) {
      throw new IllegalArgumentException(
          format(
              "Expected %s (%s) to have 2 '+' separated parts, but found %d.",
              ProviderTypeCoordinates.class.getSimpleName(), identifier, parts.length));
    }
    return ProviderTypeCoordinates.builder().siteId(parts[0]).recordId(parts[1]).build();
  }

  @Override
  public String toString() {
    return join("+", siteId(), recordId());
  }
}
