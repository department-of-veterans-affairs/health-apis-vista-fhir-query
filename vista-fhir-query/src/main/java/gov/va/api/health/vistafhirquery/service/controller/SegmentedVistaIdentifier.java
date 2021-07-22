package gov.va.api.health.vistafhirquery.service.controller;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
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

  @NonNull String vistaRecordId;

  /** Parse a VistaIdentifier. */
  public static SegmentedVistaIdentifier unpack(
      String id, Map<Character, VistaIdentifierFormat> formats) {
    return new Encoder(formats).unpack(id);
  }

  public static SegmentedVistaIdentifier unpack(String id) {
    return unpack(id, null);
  }

  /** Build a VistaIdentifier. */
  public String pack(Map<Character, VistaIdentifierFormat> formats) {
    return new Encoder(formats).pack(this);
  }

  public String pack() {
    return new Encoder(null).pack(this);
  }

  /** Build a VistaIdentifier. */
  @Override
  public String toString() {
    return String.join(
        "+",
        patientIdentifierType().abbreviation() + patientIdentifier(),
        vistaSiteId(),
        vistaRecordId());
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

  /** Encoder used for the vista identifier. */
  private static class Encoder {
    private final Map<Character, VistaIdentifierFormat> formats;

    Encoder(Map<Character, VistaIdentifierFormat> withFormats) {
      formats = new LinkedHashMap<>();
      if (withFormats != null) {
        formats.putAll(withFormats);
      }
      /* FormatString is the failsafe format, this should be last. */
      formats.put('s', new VistaIdentifierFormat.FormatString());
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
              + VistaIdentifierFormat.FormatString.class
              + ", the format mapping is incorrect.");
    }

    public SegmentedVistaIdentifier unpack(String data) {
      if (isBlank(data)) {
        throw new IllegalArgumentException("blank identifier");
      }
      char formatId = data.charAt(0);
      VistaIdentifierFormat format = formats.get(formatId);
      /* Support old format IDs that have no format prefix, but are still string formatted */
      if (format == null) {
        return formats.get('s').unpack(data);
      }
      return format.unpack(data.substring(1));
    }
  }

  @Value
  @Builder
  public static class TenvSix {
    long ten;

    int six;

    /** Parse an icn to a TenvSix. */
    public static Optional<TenvSix> parse(String icn) {
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
