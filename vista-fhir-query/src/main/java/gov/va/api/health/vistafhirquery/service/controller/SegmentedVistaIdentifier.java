package gov.va.api.health.vistafhirquery.service.controller;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import gov.va.api.lighthouse.charon.models.vprgetpatientdata.VprGetPatientData;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@Builder
public class SegmentedVistaIdentifier {
  @NonNull PatientIdentifierType patientIdentifierType;

  @NonNull String patientIdentifier;

  @NonNull String vistaSiteId;

  @NonNull VprGetPatientData.Domains vprRpcDomain;

  @NonNull String vistaRecordId;

  private static BiMap<Character, VprGetPatientData.Domains> domainAbbreviationMappings() {
    var mappings =
        Map.of('L', VprGetPatientData.Domains.labs, 'V', VprGetPatientData.Domains.vitals);
    return HashBiMap.create(mappings);
  }

  /** Parse a VistaIdentifier. */
  public static SegmentedVistaIdentifier parse(String id) {
    String[] segmentParts = id.split("\\+", -1);
    if (segmentParts.length != 3) {
      throw new IllegalArgumentException(
          "SegmentedVistaIdentifier are expected to have 3 parts "
              + "(e.g. patientIdTypeAndId+vistaSiteId+vistaRecordId).");
    }
    if (segmentParts[0].length() < 2 || segmentParts[2].length() < 2) {
      throw new IllegalArgumentException(
          "The first and third sections of a SegmentedVistaIdentifier must contain "
              + "a type and an identifier value.");
    }
    var domainType = domainAbbreviationMappings().get(segmentParts[2].charAt(0));
    if (domainType == null) {
      throw new IllegalArgumentException(
          "Identifier value had invalid domain type abbreviation: " + segmentParts[2].charAt(0));
    }
    return SegmentedVistaIdentifier.builder()
        .patientIdentifierType(PatientIdentifierType.fromAbbreviation(segmentParts[0].charAt(0)))
        .patientIdentifier(segmentParts[0].substring(1))
        .vistaSiteId(segmentParts[1])
        .vprRpcDomain(domainType)
        .vistaRecordId(segmentParts[2].substring(1))
        .build();
  }

  /** Build a VistaIdentifier. */
  public String toIdentifierSegment() {
    return String.join(
        "+",
        patientIdentifierType().abbreviation() + patientIdentifier(),
        vistaSiteId(),
        domainAbbreviationMappings().inverse().get(vprRpcDomain()) + vistaRecordId());
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
