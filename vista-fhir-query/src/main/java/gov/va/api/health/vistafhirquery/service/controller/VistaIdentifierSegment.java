package gov.va.api.health.vistafhirquery.service.controller;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class VistaIdentifierSegment {
  @NonNull PatientIdentifierType patientIdentifierType;

  @NonNull String patientIdentifier;

  @NonNull String vistaSiteId;

  @NonNull String vistaRecordId;

  /** Parse a VistaIdentifier. */
  public static VistaIdentifierSegment parse(String id) {
    String[] segmentParts = id.split("\\+", -1);
    if (segmentParts.length != 4) {
      throw new IllegalArgumentException(
          "VistaIdentifierSegments are expected to have 3 parts (e.g. icn+siteId+vistaId).");
    }
    return VistaIdentifierSegment.builder()
        .patientIdentifierType(PatientIdentifierType.valueOf(segmentParts[0]))
        .patientIdentifier(segmentParts[1])
        .vistaSiteId(segmentParts[2])
        .vistaRecordId(segmentParts[3])
        .build();
  }

  /** Build a VistaIdentifier. */
  public String toIdentifierSegment() {
    return String.join(
        "+", patientIdentifierType().name(), patientIdentifier(), vistaSiteId(), vistaRecordId());
  }

  public enum PatientIdentifierType {
    // VistA Patient IEN
    dfn,
    // National ICN
    nicn,
    // Local ICN (VistA ICN)
    vicn
  }
}
