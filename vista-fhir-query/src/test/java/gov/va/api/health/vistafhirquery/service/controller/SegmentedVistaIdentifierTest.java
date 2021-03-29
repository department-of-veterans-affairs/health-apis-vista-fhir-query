package gov.va.api.health.vistafhirquery.service.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import gov.va.api.health.ids.api.ResourceIdentity;
import gov.va.api.health.ids.client.IdsClientProperties;
import gov.va.api.health.ids.client.IdsClientProperties.EncodedIdsFormatProperties;
import gov.va.api.health.ids.client.RestIdentityServiceClientConfig;
import gov.va.api.health.vistafhirquery.idsmapping.VistaFhirQueryIdsCodebookSupplier;
import gov.va.api.health.vistafhirquery.service.controller.SegmentedVistaIdentifier.PatientIdentifierType;
import gov.va.api.lighthouse.charon.models.vprgetpatientdata.VprGetPatientData;
import gov.va.api.lighthouse.charon.models.vprgetpatientdata.VprGetPatientData.Domains;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

@Slf4j
public class SegmentedVistaIdentifierTest {
  @SuppressWarnings("ResultOfMethodCallIgnored")
  @Test
  void invalidPatientIdentifierTypeThrowsIllegalArgument() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> SegmentedVistaIdentifier.PatientIdentifierType.fromAbbreviation('Z'));
  }

  @Test
  void packWithBinaryFormat() {
    // VISTA Observation N1011537977V693883+673+LCH;6929384.839997;14
    SegmentedVistaIdentifier id =
        SegmentedVistaIdentifier.builder()
            .patientIdentifierType(PatientIdentifierType.NATIONAL_ICN)
            .patientIdentifier("1011537977V693883")
            .vistaSiteId("673")
            .vprRpcDomain(Domains.labs)
            .vistaRecordId("CH;6929384.839997;14xxx")
            .build();
    String packed = id.pack();
    SegmentedVistaIdentifier unpacked = SegmentedVistaIdentifier.unpack(packed);
    assertThat(unpacked).isEqualTo(id);
    var ids =
        new RestIdentityServiceClientConfig(
                null,
                IdsClientProperties.builder()
                    .encodedIds(
                        EncodedIdsFormatProperties.builder()
                            .i3Enabled(true)
                            .encodingKey("some-longish-key-here")
                            .build())
                    .build(),
                null)
            .encodingIdentityServiceClient(new VistaFhirQueryIdsCodebookSupplier().get());
    String i3 =
        ids.register(
                List.of(
                    ResourceIdentity.builder()
                        .system("VISTA")
                        .resource("Observation")
                        .identifier(packed)
                        .build()))
            .get(0)
            .uuid();
    assertThat(i3.length()).as(packed).isLessThanOrEqualTo(64);
    log.info(
        "({}) {} -> ({}) {}",
        id.toIdentifierSegment().length(),
        id.toIdentifierSegment(),
        i3.length(),
        i3);
  }

  @Test
  void packWithStringFormat() {
    assertThat(
            SegmentedVistaIdentifier.builder()
                .patientIdentifierType(SegmentedVistaIdentifier.PatientIdentifierType.NATIONAL_ICN)
                .patientIdentifier("icn")
                .vistaSiteId("siteId")
                .vprRpcDomain(VprGetPatientData.Domains.vitals)
                .vistaRecordId("vistaId")
                .build()
                .pack())
        .isEqualTo("sNicn+siteId+VvistaId");
  }

  @Test
  void parseIdSuccessfully() {
    assertThat(SegmentedVistaIdentifier.unpack("Nicn+siteId+LvistaId"))
        .isEqualTo(
            SegmentedVistaIdentifier.builder()
                .patientIdentifierType(SegmentedVistaIdentifier.PatientIdentifierType.NATIONAL_ICN)
                .patientIdentifier("icn")
                .vistaSiteId("siteId")
                .vprRpcDomain(VprGetPatientData.Domains.labs)
                .vistaRecordId("vistaId")
                .build());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"x+123+Vabc", "+123+abc", "123", "123+abc", "D123+abc+V456+def", "D123+abc+x"})
  void parseInvalidSegmentThrowsIllegalArgument(String segment) {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> SegmentedVistaIdentifier.unpack(segment));
  }

  @ParameterizedTest
  @EnumSource(value = SegmentedVistaIdentifier.PatientIdentifierType.class)
  void patientIdentifierTypeRoundTrip(SegmentedVistaIdentifier.PatientIdentifierType value) {
    var shortened = value.abbreviation();
    var fullLength = SegmentedVistaIdentifier.PatientIdentifierType.fromAbbreviation(shortened);
    assertThat(fullLength).isEqualTo(value);
  }

  @Test
  void toIdentiferSegment() {
    assertThat(
            SegmentedVistaIdentifier.builder()
                .patientIdentifierType(SegmentedVistaIdentifier.PatientIdentifierType.NATIONAL_ICN)
                .patientIdentifier("icn")
                .vistaSiteId("siteId")
                .vprRpcDomain(VprGetPatientData.Domains.vitals)
                .vistaRecordId("vistaId")
                .build()
                .toIdentifierSegment())
        .isEqualTo("Nicn+siteId+VvistaId");
  }
}
