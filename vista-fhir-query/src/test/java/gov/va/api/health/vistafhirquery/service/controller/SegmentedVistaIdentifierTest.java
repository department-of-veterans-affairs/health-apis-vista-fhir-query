package gov.va.api.health.vistafhirquery.service.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import gov.va.api.lighthouse.charon.models.vprgetpatientdata.VprGetPatientData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

public class SegmentedVistaIdentifierTest {
  @Test
  void invalidPatientIdentifierTypeThrowsIllegalArgument() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> SegmentedVistaIdentifier.PatientIdentifierType.fromAbbreviation('Z'));
  }

  @Test
  void parseIdSuccessfully() {
    assertThat(SegmentedVistaIdentifier.parse("Nicn+siteId+LvistaId"))
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
        .isThrownBy(() -> SegmentedVistaIdentifier.parse(segment));
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
