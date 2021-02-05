package gov.va.api.health.vistafhirquery.service.controller;

import lombok.Builder;
import lombok.Getter;
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
    if (segmentParts[0].length() < 2) {
      throw new IllegalArgumentException(
          "The first section of a VistaIdentifierSegment must contain "
              + "a type and an identifier value.");
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
    return String.join(
        "+",
        patientIdentifierType().abbreviation() + patientIdentifier(),
        vistaSiteId(),
        vistaRecordId());
  }

  @RequiredArgsConstructor
  public enum PatientIdentifierType {
    /** A Patients DFN in VistA. */
    VISTA_PATIENT_FILE_ID('D'),
    /** A Patients ICN assigned by MPI and existing nationally. */
    NATIONAL_ICN('N'),
    /** An ICN assigned at a local VistA site. */
    LOCAL_VISTA_ICN('L');

    @Getter private final char abbreviation;

    /** Get an Enum value from an abbreviation. */
    public static PatientIdentifierType fromAbbreviation(char abbreviation) {
      switch (abbreviation) {
        case 'D':
          return VISTA_PATIENT_FILE_ID;
        case 'N':
          return NATIONAL_ICN;
        case 'L':
          return LOCAL_VISTA_ICN;
        default:
          throw new IllegalArgumentException(
              "PatientIdentifierType abbreviation in segment is invalid: " + abbreviation);
      }
    }
  }
}
