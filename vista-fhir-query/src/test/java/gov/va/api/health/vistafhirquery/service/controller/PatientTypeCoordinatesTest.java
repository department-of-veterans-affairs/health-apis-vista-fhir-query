package gov.va.api.health.vistafhirquery.service.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class PatientTypeCoordinatesTest {
  @Test
  void identifierFromStringSuccess() {
    var sample = "p1+123+456";
    var expected = PatientTypeCoordinates.builder().icn("p1").siteId("123").recordId("456").build();
    assertThat(PatientTypeCoordinates.fromString(sample)).isEqualTo(expected);
  }

  @ParameterizedTest
  @ValueSource(strings = {"p1", "123+456", "p1+123+456+789"})
  void identifierFromStringThrowsIllegalArgumentForBadValues(String badId) {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> PatientTypeCoordinates.fromString(badId));
  }

  @Test
  void identifierToString() {
    var sample = PatientTypeCoordinates.builder().icn("p1").siteId("123").recordId("456").build();
    var expected = "p1+123+456";
    assertThat(sample.toString()).isEqualTo(expected);
  }
}
