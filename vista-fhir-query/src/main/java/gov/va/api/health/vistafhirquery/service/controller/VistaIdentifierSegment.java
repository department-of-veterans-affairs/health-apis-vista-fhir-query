package gov.va.api.health.vistafhirquery.service.controller;

import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
    if (segmentParts.length != 3) {
      throw new IllegalArgumentException(
          "VistaIdentifierSegments are expected to have 3 parts "
              + "(e.g. patientIdTypeAndId+vistaSiteId+vistaRecordId).");
    }
    return VistaIdentifierSegment.builder()
        .patientIdentifierType(PatientIdentifierType.fromAbbreviation(segmentParts[0].charAt(0)))
        .patientIdentifier(segmentParts[0].substring(1))
        .vistaSiteId(segmentParts[1])
        .vistaRecordId(segmentParts[2])
        .build();
  }

  /** Build a VistaIdentifier. */
  public String toIdentifierSegment() {
    return patientIdentifierType().abbreviation()
        + String.join("+", patientIdentifier(), vistaSiteId(), vistaRecordId());
  }

  @RequiredArgsConstructor
  public enum PatientIdentifierType {
    /** A Patients DFN in VistA. */
    VISTA_PATIENT_FILE_ID('D'),
    /** A Patients ICN assigned by MPI and existing nationally. */
    NATIONAL_ICN('N'),
    /** An ICN assigned at a local VistA site. */
    LOCAL_VISTA_ICN('L');

    private final char abbreviation;

    /** Get an Enum value from an abbreviation. */
    public static PatientIdentifierType fromAbbreviation(char abbreviation) {
      var match =
          Arrays.stream(PatientIdentifierType.values())
              .filter(e -> e.abbreviation() == abbreviation)
              .collect(Collectors.toList());
      if (match.size() != 1) {
        throw new IllegalStateException(
            "PatientIdentifierType abbreviation in segment is invalid: " + abbreviation);
      }
      return match.get(0);
    }

    public char abbreviation() {
      return abbreviation;
    }
  }
}
