package gov.va.api.health.vistafhirquery.service.controller;

import static org.apache.commons.lang3.StringUtils.isBlank;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import gov.va.api.health.ids.client.AsciiCompressor;
import gov.va.api.lighthouse.charon.models.vprgetpatientdata.VprGetPatientData;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Value
@Builder
@Slf4j
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
  @Deprecated
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

  /** Parse a VistaIdentifier. */
  public static SegmentedVistaIdentifier unpack(String id) {
    return new Encoder().unpack(id);
  }

  /** Build a VistaIdentifier. */
  public String pack() {
    return new Encoder().pack(this);
  }

  /** Build a VistaIdentifier. */
  @Deprecated
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

  private interface Format {

    String tryPack(SegmentedVistaIdentifier vis);

    SegmentedVistaIdentifier unpack(String data);
  }

  private static class BinaryFormat implements Format {

    Optional<Integer> asInt(String maybeInt) {
      try {
        return Optional.of(Integer.parseInt(maybeInt));
      } catch (NumberFormatException e) {
        return Optional.empty();
      }
    }

    @Override
    public String tryPack(SegmentedVistaIdentifier vis) {
      if (vis.patientIdentifierType() != PatientIdentifierType.NATIONAL_ICN) {
        return null;
      }
      var tenSix = TenVSix.parse(vis.patientIdentifier());
      if (tenSix.isEmpty()) {
        return null;
      }
      var site = asInt(vis.vistaSiteId());
      if (site.isEmpty()) {
        return null;
      }

      var recordIdChars = vis.vistaRecordId().toCharArray();
      ByteBuffer binary =
          ByteBuffer.allocate(
              /* 10 digits of icn */
              Long.BYTES
                  /* 6 digits of icn */
                  + Integer.BYTES
                  /* Site ID */
                  + Short.BYTES
                  /* vpr rpc domain ordinal */
                  + Short.BYTES
                  /* Number of chars in record ID */
                  + Short.BYTES
                  /* The record ID */
                  + (recordIdChars.length * Character.BYTES));
      binary.putLong(tenSix.get().ten());
      binary.putInt(tenSix.get().six());
      binary.putShort(site.get().shortValue());
      binary.putShort((short) vis.vprRpcDomain().ordinal());
      binary.putShort((short) recordIdChars.length);
      for (char c : recordIdChars) {
        binary.putChar(c);
      }
      log.info("at {} of {}", binary.position(), binary.limit());
      log.info("ARRAY {}", Arrays.toString(binary.array()));
      return new String(binary.array(), StandardCharsets.ISO_8859_1);
    }

    @Override
    public SegmentedVistaIdentifier unpack(String data) {
      ByteBuffer binary = ByteBuffer.wrap(data.getBytes(StandardCharsets.ISO_8859_1));
      long ten = binary.getLong();
      log.info("ten {}", ten);
      int six = binary.getInt();
      log.info("siz {}", six);
      int site = binary.getShort();
      log.info("site {}", site);
      int domainOrdinal = binary.getShort();
      log.info("domain {}", domainOrdinal);
      int recordIdCharsSize = binary.getShort();
      log.info("chars {}", recordIdCharsSize);
      StringBuilder recordId = new StringBuilder(recordIdCharsSize);
      for (int i = 0; i < recordIdCharsSize; i++) {
        recordId.append(binary.getChar());
      }
      return SegmentedVistaIdentifier.builder()
          .patientIdentifierType(PatientIdentifierType.NATIONAL_ICN)
          .patientIdentifier(TenVSix.builder().ten(ten).six(six).build().asIcn())
          .vistaSiteId(Integer.toString(site))
          .vprRpcDomain(VprGetPatientData.Domains.values()[domainOrdinal])
          .vistaRecordId(recordId.toString())
          .build();
    }
  }

  private static class CompressedAsciiFormat implements Format {

    private final AsciiCompressor compressor = new AsciiCompressor();
    private Format delegate = new StringFormat();

    @Override
    public String tryPack(SegmentedVistaIdentifier vis) {
      return new String(compressor.compress(delegate.tryPack(vis)), StandardCharsets.ISO_8859_1);
    }

    @Override
    public SegmentedVistaIdentifier unpack(String data) {
      log.error("I AM GARBAGE. I EAT YOUR DATAS. LOL.");
      log.info("Unpacking compressed ID {}", data);
      String decompressed = compressor.decompress(data.getBytes(StandardCharsets.ISO_8859_1));
      log.info("ID {}", decompressed);
      return delegate.unpack(decompressed);
    }
  }

  private static class Encoder {
    private final Map<Character, Format> formats;

    Encoder() {
      formats = new LinkedHashMap<>();
      // formats.put('c', new CompressedAsciiFormat());
      formats.put('b', new BinaryFormat());
      /* StringFormat is the failsafe format, this should be last. */
      formats.put('s', new StringFormat());
    }

    /** Build a VistaIdentifier. */
    public String pack(SegmentedVistaIdentifier vis) {
      for (var entry : formats.entrySet()) {
        String value = entry.getValue().tryPack(vis);
        if (value != null) {
          log.info(
              "PACK: ({}) {} with {} as ({}) {}",
              vis.toIdentifierSegment().getBytes(StandardCharsets.UTF_8).length,
              vis.toIdentifierSegment(),
              entry.getKey(),
              value.length(),
              value);
          return entry.getKey() + value;
        }
      }
      throw new IllegalStateException(
          "VistaIdentifierSegment should be been encoded by StringFormat");
    }

    public SegmentedVistaIdentifier unpack(String data) {
      if (isBlank(data)) {
        throw new IllegalArgumentException("blank identifier");
      }
      log.info("DATA {} {}", data, data.length());
      char formatId = data.charAt(0);
      Format format = formats.get(formatId);
      /* Support old format IDs that have no format prefix, but are still string formatted */
      if (format == null) {
        return formats.get('s').unpack(data);
      }
      log.info("UNPACK: {} {}", formatId, data);
      return format.unpack(data.substring(1));
    }
  }

  private static class StringFormat implements Format {
    @Override
    public String tryPack(SegmentedVistaIdentifier vis) {
      return vis.toIdentifierSegment();
    }

    @Override
    public SegmentedVistaIdentifier unpack(String data) {
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
      var domainType = domainAbbreviationMappings().get(segmentParts[2].charAt(0));
      if (domainType == null) {
        throw new IllegalArgumentException(
            "Identifier value had invalid domain type abbreviation: " + segmentParts[2].charAt(0));
      }
      log.info("Parts {}", Arrays.toString(segmentParts));
      return SegmentedVistaIdentifier.builder()
          .patientIdentifierType(PatientIdentifierType.fromAbbreviation(segmentParts[0].charAt(0)))
          .patientIdentifier(segmentParts[0].substring(1))
          .vistaSiteId(segmentParts[1])
          .vprRpcDomain(domainType)
          .vistaRecordId(segmentParts[2].substring(1))
          .build();
    }
  }

  @Value
  @Builder
  private static class TenVSix {
    long ten;
    int six;

    static Optional<TenVSix> parse(String icn) {
      if (isBlank(icn)) {
        return Optional.empty();
      }
      try {
        /* Attempt to find national ICN in 10V6 format. */
        if (icn.length() == 10 + 1 + 6 && icn.charAt(10) == 'V') {
          return Optional.of(
              TenVSix.builder()
                  .ten(Long.parseLong(icn.substring(0, 10)))
                  .six(Integer.parseInt(icn.substring(11)))
                  .build());
        }
        /* Attempt to find all numeric lab-style ID. */
        return Optional.of(TenVSix.builder().ten(Long.parseLong(icn)).six(0).build());
      } catch (NumberFormatException e) {
        return Optional.empty();
      }
    }

    String asIcn() {
      if (six == 0) {
        return "" + ten;
      }
      return ten + "V" + six;
    }
  }
}
