package gov.va.api.health.vistafhirquery.service.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import gov.va.api.health.ids.api.ResourceIdentity;
import gov.va.api.health.ids.client.IdsClientProperties;
import gov.va.api.health.ids.client.IdsClientProperties.EncodedIdsFormatProperties;
import gov.va.api.health.ids.client.RestIdentityServiceClientConfig;
import gov.va.api.health.vistafhirquery.idsmapping.VistaFhirQueryIdsCodebookSupplier;
import gov.va.api.health.vistafhirquery.service.controller.SegmentedVistaIdentifier.PatientIdentifierType;
import gov.va.api.health.vistafhirquery.service.controller.SegmentedVistaIdentifier.TenvSix;
import gov.va.api.lighthouse.charon.models.vprgetpatientdata.VprGetPatientData;
import gov.va.api.lighthouse.charon.models.vprgetpatientdata.VprGetPatientData.Domains;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

@Slf4j
public class SegmentedVistaIdentifierTest {
  static Stream<Arguments> packWithCompactedObservationLabFormatIsOnlyForLabs() {
    return Stream.of(
        arguments(
            SegmentedVistaIdentifier.builder()
                .patientIdentifierType(PatientIdentifierType.NATIONAL_ICN)
                .patientIdentifier("1011537977V693883")
                .vistaSiteId("673")
                .vprRpcDomain(Domains.labs)
                .vistaRecordId("CH;6929384.839997;14")
                .build(),
            "L1011537977693883673692938483999714"),
        arguments(
            SegmentedVistaIdentifier.builder()
                .patientIdentifierType(PatientIdentifierType.NATIONAL_ICN)
                .patientIdentifier("1011537977")
                .vistaSiteId("673")
                .vprRpcDomain(Domains.labs)
                .vistaRecordId("CH;6929384.839997;14")
                .build(),
            "L1011537977xxxxx0673692938483999714"),
        arguments(
            SegmentedVistaIdentifier.builder()
                .patientIdentifierType(PatientIdentifierType.NATIONAL_ICN)
                .patientIdentifier("1011537977V693883")
                .vistaSiteId("673")
                .vprRpcDomain(Domains.labs)
                .vistaRecordId("CH;6929384.83;14")
                .build(),
            "L1011537977693883673692938483xxxx14"),
        arguments(
            SegmentedVistaIdentifier.builder()
                .patientIdentifierType(PatientIdentifierType.NATIONAL_ICN)
                .patientIdentifier("1011537977V693883")
                .vistaSiteId("673")
                .vprRpcDomain(Domains.vitals)
                .vistaRecordId("CH;6929384.839997;14")
                .build(),
            "sN1011537977V693883+673+VCH;6929384.839997;14"),
        arguments(
            SegmentedVistaIdentifier.builder()
                .patientIdentifierType(PatientIdentifierType.VISTA_PATIENT_FILE_ID)
                .patientIdentifier("1011537977V693883")
                .vistaSiteId("673")
                .vprRpcDomain(Domains.labs)
                .vistaRecordId("CH;6929384.839997;14")
                .build(),
            "sD1011537977V693883+673+LCH;6929384.839997;14"),
        arguments(
            SegmentedVistaIdentifier.builder()
                .patientIdentifierType(PatientIdentifierType.NATIONAL_ICN)
                .patientIdentifier("1011537977V693883")
                .vistaSiteId("673a")
                .vprRpcDomain(Domains.labs)
                .vistaRecordId("CH;6929384.839997;14")
                .build(),
            "sN1011537977V693883+673a+LCH;6929384.839997;14"),
        arguments(
            SegmentedVistaIdentifier.builder()
                .patientIdentifierType(PatientIdentifierType.NATIONAL_ICN)
                .patientIdentifier("1011537977V693883")
                .vistaSiteId("673")
                .vprRpcDomain(Domains.labs)
                .vistaRecordId("XH;6929384.839997;14")
                .build(),
            "sN1011537977V693883+673+LXH;6929384.839997;14"),
        arguments(
            SegmentedVistaIdentifier.builder()
                .patientIdentifierType(PatientIdentifierType.NATIONAL_ICN)
                .patientIdentifier("a1011537977V693883")
                .vistaSiteId("673")
                .vprRpcDomain(Domains.labs)
                .vistaRecordId("CH;6929384.839997;14")
                .build(),
            "sNa1011537977V693883+673+LCH;6929384.839997;14")
        //
        );
  }

  @Test
  void formatOfToString() {
    assertThat(
            SegmentedVistaIdentifier.builder()
                .patientIdentifierType(SegmentedVistaIdentifier.PatientIdentifierType.NATIONAL_ICN)
                .patientIdentifier("icn")
                .vistaSiteId("siteId")
                .vprRpcDomain(VprGetPatientData.Domains.vitals)
                .vistaRecordId("vistaId")
                .build()
                .toString())
        .isEqualTo("Nicn+siteId+VvistaId");
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  @Test
  void invalidPatientIdentifierTypeThrowsIllegalArgument() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> SegmentedVistaIdentifier.PatientIdentifierType.fromAbbreviation('Z'));
  }

  @ParameterizedTest
  @MethodSource
  void packWithCompactedObservationLabFormatIsOnlyForLabs(
      SegmentedVistaIdentifier id, String expected) {
    assertThat(id.pack()).isEqualTo(expected);
  }

  @Test
  void packWithCompactedObservationLabFormatIsSmallEnough() {
    // VISTA Observation N1011537977V693883+673+LCH;6929384.839997;14
    SegmentedVistaIdentifier id =
        SegmentedVistaIdentifier.builder()
            .patientIdentifierType(PatientIdentifierType.NATIONAL_ICN)
            .patientIdentifier("1011537977V693883")
            .vistaSiteId("673")
            .vprRpcDomain(Domains.labs)
            .vistaRecordId("CH;6909685.886779;643214")
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
                    .build())
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
  void tenVSixParsing() {
    assertThat(TenvSix.parse("1122334455V667788").get())
        .isEqualTo(TenvSix.builder().ten(1122334455).six(667788).build());
    assertThat(TenvSix.parse("1122334455").get())
        .isEqualTo(TenvSix.builder().ten(1122334455).six(0).build());
    assertThat(TenvSix.parse("12345").get()).isEqualTo(TenvSix.builder().ten(12345).six(0).build());
    assertThat(TenvSix.parse(null)).isEmpty();
    assertThat(TenvSix.parse("")).isEmpty();
    assertThat(TenvSix.parse(Long.MAX_VALUE + "9")).isEmpty();
    assertThat(TenvSix.parse("1122334455V" + (Integer.MAX_VALUE + 1L))).isEmpty();
    assertThat(TenvSix.parse("1122334455X667788")).isEmpty();
    assertThat(TenvSix.parse("112233445V667788")).isEmpty();
    assertThat(TenvSix.parse("1122334455V67788")).isEmpty();
    assertThat(TenvSix.parse("112233445aV667788")).isEmpty();
    assertThat(TenvSix.parse("1122334455Va67788")).isEmpty();
    assertThat(TenvSix.builder().ten(1122334455).six(667788).build().toString())
        .isEqualTo("1122334455V667788");
    assertThat(TenvSix.builder().ten(1122334455).six(0).build().toString()).isEqualTo("1122334455");
  }
}
