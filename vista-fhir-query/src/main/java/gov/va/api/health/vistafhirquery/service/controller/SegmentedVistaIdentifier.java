package gov.va.api.health.vistafhirquery.service.controller;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.commons.lang3.StringUtils.rightPad;
import static org.apache.commons.lang3.StringUtils.strip;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import gov.va.api.lighthouse.charon.models.vprgetpatientdata.VprGetPatientData;
import gov.va.api.lighthouse.charon.models.vprgetpatientdata.VprGetPatientData.Domains;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

/** SegmentedVistaIdentifier. */
@Value
@Builder
public class SegmentedVistaIdentifier {
  @NonNull PatientIdentifierType patientIdentifierType;

  @NonNull String patientIdentifier;

  @NonNull String vistaSiteId;

  VprGetPatientData.Domains vprRpcDomain;

  @NonNull String vistaRecordId;

  private static BiMap<Character, VprGetPatientData.Domains> domainAbbreviationMappings() {
    var mappings =
        Map.of('L', VprGetPatientData.Domains.labs, 'V', VprGetPatientData.Domains.vitals);
    return HashBiMap.create(mappings);
  }

  private static SegmentedVistaIdentifier fromString(String data) {
    String[] segmentParts = data.split("\\+", -1);
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
    // Coverage ids are iens, so any id that doesn't match a domain mapping (L, V) can be ignored
    // ToDo the above statement may not always hold true as new RPCs/fhir-resources are added.
    var domainType = domainAbbreviationMappings().get(segmentParts[2].charAt(0));
    return SegmentedVistaIdentifier.builder()
        .patientIdentifierType(PatientIdentifierType.fromAbbreviation(segmentParts[0].charAt(0)))
        .patientIdentifier(segmentParts[0].substring(1))
        .vistaSiteId(segmentParts[1])
        .vprRpcDomain(domainType)
        .vistaRecordId(segmentParts[2].substring(1))
        .build();
  }

  /** Parse a VistaIdentifier. */
  public static SegmentedVistaIdentifier unpack(String id) {
    return new Encoder().unpack(id);
  }

  /** Build a VistaIdentifier. */
  public String pack() {
    return new Encoder().pack(this);
  }

  /** Build a VistaIdentifier. */
  @Override
  public String toString() {
    return String.join(
        "+",
        patientIdentifierType().abbreviation() + patientIdentifier(),
        vistaSiteId(),
        domainAbbreviationMappings().inverse().getOrDefault(vprRpcDomain(), '*') + vistaRecordId());
  }

  /** The type of a Vista identifier which can be DFN, local ICN, or National ICN. */
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
    @SuppressWarnings("EnhancedSwitchMigration")
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

  /** Format of the vista identifier. */
  private interface Format {
    String tryPack(SegmentedVistaIdentifier vis);

    SegmentedVistaIdentifier unpack(String data);
  }

  /** Encoder used for the vista identifier. */
  private static class Encoder {
    private final Map<Character, Format> formats;

    Encoder() {
      formats = new LinkedHashMap<>();
      formats.put('L', new FormatCompressedObservationLab());
      /* FormatString is the failsafe format, this should be last. */
      formats.put('s', new FormatString());
    }

    /** Build a VistaIdentifier. */
    public String pack(SegmentedVistaIdentifier vis) {
      for (var entry : formats.entrySet()) {
        String value = entry.getValue().tryPack(vis);
        if (value != null) {
          return entry.getKey() + value;
        }
      }
      throw new IllegalStateException(
          "VistaIdentifierSegment should have been encoded by "
              + FormatString.class
              + ", the format mapping is incorrect.");
    }

    public SegmentedVistaIdentifier unpack(String data) {
      if (isBlank(data)) {
        throw new IllegalArgumentException("blank identifier");
      }
      char formatId = data.charAt(0);
      Format format = formats.get(formatId);
      /* Support old format IDs that have no format prefix, but are still string formatted */
      if (format == null) {
        return formats.get('s').unpack(data);
      }
      return format.unpack(data.substring(1));
    }
  }

  private static class FormatCompressedObservationLab implements Format {
    private static final Pattern SITE = Pattern.compile("[0-9]{3}");

    private static final Pattern RECORD_ID = Pattern.compile("CH;[0-9]{7}\\.[0-9]{1,6};[0-9]+");

    @Override
    public String tryPack(SegmentedVistaIdentifier vis) {
      if (vis.vprRpcDomain() != Domains.labs) {
        return null;
      }
      if (vis.patientIdentifierType() != PatientIdentifierType.NATIONAL_ICN) {
        return null;
      }
      if (!SITE.matcher(vis.vistaSiteId()).matches()) {
        return null;
      }
      var tenSix = TenvSix.parse(vis.patientIdentifier());
      if (tenSix.isEmpty()) {
        return null;
      }
      if (!RECORD_ID.matcher(vis.vistaRecordId()).matches()) {
        return null;
      }
      String ten = leftPad(Long.toString(tenSix.get().ten()), 10, 'x');
      String six = leftPad(Integer.toString(tenSix.get().six()), 6, 'x');
      String site = vis.vistaSiteId();
      String date = vis.vistaRecordId().substring(3, 10);
      int lastSemi = vis.vistaRecordId().lastIndexOf(';');
      String time = rightPad(vis.vistaRecordId().substring(11, lastSemi), 6, 'x');
      String remainder = vis.vistaRecordId().substring(lastSemi + 1);
      // ....10....6....3.......7......6......2........
      return ten + six + site + date + time + remainder;
    }

    @Override
    public SegmentedVistaIdentifier unpack(String data) {
      // 10
      String ten = strip(data.substring(0, 10), "x");
      // 6
      String six = strip(data.substring(10, 16), "x");
      // 3
      String site = strip(data.substring(16, 19), "x");
      // 7
      String date = data.substring(19, 26);
      // 6
      String time = data.substring(26, 32);
      // 2
      String remainder = data.substring(32);
      String icn = "0".equals(six) ? ten : ten + "V" + six;
      return SegmentedVistaIdentifier.builder()
          .patientIdentifierType(PatientIdentifierType.NATIONAL_ICN)
          .patientIdentifier(icn)
          .vistaSiteId(site)
          .vprRpcDomain(Domains.labs)
          .vistaRecordId("CH;" + date + "." + time + ";" + remainder)
          .build();
    }
  }

  private static class FormatString implements Format {
    @Override
    public String tryPack(SegmentedVistaIdentifier vis) {
      return vis.toString();
    }

    @Override
    public SegmentedVistaIdentifier unpack(String data) {
      return fromString(data);
    }
  }

  @Value
  @Builder
  static class TenvSix {
    long ten;

    int six;

    static Optional<TenvSix> parse(String icn) {
      if (isBlank(icn)) {
        return Optional.empty();
      }
      try {
        /* Attempt to find national ICN in 10V6 format. */
        if (icn.length() == 10 + 1 + 6 && icn.charAt(10) == 'V') {
          return Optional.of(
              TenvSix.builder()
                  .ten(Long.parseLong(icn.substring(0, 10)))
                  .six(Integer.parseInt(icn.substring(11)))
                  .build());
        }
        /* Attempt to find all numeric lab-style ID. */
        return Optional.of(TenvSix.builder().ten(Long.parseLong(icn)).six(0).build());
      } catch (NumberFormatException e) {
        return Optional.empty();
      }
    }

    @Override
    public String toString() {
      if (six == 0) {
        return Long.toString(ten);
      }
      return ten + "V" + six;
    }
  }
}
