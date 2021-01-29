package gov.va.api.health.vistafhirquery.service.controller;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class VistaIdentifierSegment {

  @NonNull String icn;

  @NonNull String vistaSite;

  @NonNull String vistaId;

  /** Parse a VistaIdentifer. */
  public static VistaIdentifierSegment parse(String id) {
    String[] segmentParts = id.split("\\+", -1);
    if (segmentParts.length != 3) {
      throw new IllegalArgumentException(
          "VistaIdentifierSegments are expected to have 3 parts (e.g. icn+siteId+vistaId).");
    }
    return VistaIdentifierSegment.builder()
        .icn(segmentParts[0])
        .vistaSite(segmentParts[1])
        .vistaId(segmentParts[2])
        .build();
  }

  /** Build a VistaIdentifier. */
  public String toIdentifierSegment() {
    return icn() + "+" + vistaSite() + "+" + vistaId();
  }
}
