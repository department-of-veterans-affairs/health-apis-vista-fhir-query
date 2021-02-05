package gov.va.api.health.vistafhirquery.service.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

public class VistaIdentifierSegmentTest {
  @Test
  void parseIdSuccessfully() {
    assertThat(VistaIdentifierSegment.parse("Nicn+siteId+vistaId"))
        .isEqualTo(
            VistaIdentifierSegment.builder()
                .patientIdentifierType(VistaIdentifierSegment.PatientIdentifierType.NATIONAL_ICN)
                .patientIdentifier("icn")
                .vistaSiteId("siteId")
                .vistaRecordId("vistaId")
                .build());
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 4})
  void parseIdWithInvalidSegmentThrows(Integer segmentFieldCount) {
    StringBuilder sb = new StringBuilder("" + 0);
    for (int i = 1; i < segmentFieldCount; i++) {
      sb.append("+");
      sb.append(i);
    }
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> VistaIdentifierSegment.parse(sb.toString()));
  }

  @ParameterizedTest
  @EnumSource(value = VistaIdentifierSegment.PatientIdentifierType.class)
  void patientIdentifierTypeRoundTrip(VistaIdentifierSegment.PatientIdentifierType value) {
    var shortened = value.abbreviation();
    var fullLength = VistaIdentifierSegment.PatientIdentifierType.fromAbbreviation(shortened);
    assertThat(fullLength).isEqualTo(value);
  }

  @Test
  void toIdentiferSegment() {
    assertThat(
            VistaIdentifierSegment.builder()
                .patientIdentifierType(VistaIdentifierSegment.PatientIdentifierType.NATIONAL_ICN)
                .patientIdentifier("icn")
                .vistaSiteId("siteId")
                .vistaRecordId("vistaId")
                .build()
                .toIdentifierSegment())
        .isEqualTo("Nicn+siteId+vistaId");
  }
}
