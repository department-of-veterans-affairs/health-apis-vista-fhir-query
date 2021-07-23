package gov.va.api.health.vistafhirquery.service.controller.observation;

import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.commons.lang3.StringUtils.rightPad;
import static org.apache.commons.lang3.StringUtils.strip;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import gov.va.api.health.vistafhirquery.service.controller.ResourceExceptions;
import gov.va.api.health.vistafhirquery.service.controller.SegmentedVistaIdentifier;
import gov.va.api.health.vistafhirquery.service.controller.VistaIdentifierFormat;
import gov.va.api.lighthouse.charon.models.vprgetpatientdata.VprGetPatientData;
import java.util.Map;
import java.util.regex.Pattern;

public class CompressedObservationLabFormat implements VistaIdentifierFormat {
  private static final Pattern SITE = Pattern.compile("[0-9]{3}");

  private static final Pattern RECORD_ID = Pattern.compile("CH;[0-9]{7}\\.[0-9]{1,6};[0-9]+");

  /** Mappings from VPR RPC domains to abbreviated characters. */
  public static BiMap<Character, VprGetPatientData.Domains> domainAbbreviationMappings() {
    var mappings =
        Map.of('L', VprGetPatientData.Domains.labs, 'V', VprGetPatientData.Domains.vitals);
    return HashBiMap.create(mappings);
  }

  @Override
  public String tryPack(SegmentedVistaIdentifier vis) {
    if (vis.vistaRecordId().length() < 2) {
      throw ResourceExceptions.ExpectationFailed.because(
          "Expected record id "
              + vis.vistaRecordId()
              + " to have "
              + "length > 2 to encode"
              + " "
              + CompressedObservationLabFormat.class.getSimpleName());
    }
    if (domainAbbreviationMappings().get(vis.vistaRecordId().charAt(0))
        != VprGetPatientData.Domains.labs) {
      return null;
    }
    if (vis.patientIdentifierType()
        != SegmentedVistaIdentifier.PatientIdentifierType.NATIONAL_ICN) {
      return null;
    }
    if (!SITE.matcher(vis.vistaSiteId()).matches()) {
      return null;
    }
    var tenSix = SegmentedVistaIdentifier.TenvSix.parse(vis.patientIdentifier());
    if (tenSix.isEmpty()) {
      return null;
    }
    if (!RECORD_ID.matcher(vis.vistaRecordId().substring(1)).matches()) {
      return null;
    }
    String ten = leftPad(Long.toString(tenSix.get().ten()), 10, 'x');
    String six = leftPad(Integer.toString(tenSix.get().six()), 6, 'x');
    String site = vis.vistaSiteId();
    String date = vis.vistaRecordId().substring(4, 11);
    int lastSemi = vis.vistaRecordId().lastIndexOf(';');
    String time = rightPad(vis.vistaRecordId().substring(12, lastSemi), 6, 'x');
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
        .patientIdentifierType(SegmentedVistaIdentifier.PatientIdentifierType.NATIONAL_ICN)
        .patientIdentifier(icn)
        .vistaSiteId(site)
        .vistaRecordId(
            domainAbbreviationMappings().inverse().get(VprGetPatientData.Domains.labs)
                + "CH;"
                + date
                + "."
                + time
                + ";"
                + remainder)
        .build();
  }
}
