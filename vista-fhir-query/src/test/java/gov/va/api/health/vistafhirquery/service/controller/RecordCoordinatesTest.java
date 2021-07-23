package gov.va.api.health.vistafhirquery.service.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class RecordCoordinatesTest {
  @Test
  void identifierFromStringSuccess() {
    var sample = "123;456";
    var expected = RecordCoordinates.builder().file("123").ien("456").build();
    assertThat(RecordCoordinates.fromString(sample)).isEqualTo(expected);
  }

  @ParameterizedTest
  @ValueSource(strings = {"123", "123;456;789"})
  void identifierFromStringThrowsIllegalArgumentForBadValues(String badId) {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> RecordCoordinates.fromString(badId));
  }

  @Test
  void identifierToString() {
    var sample = RecordCoordinates.builder().file("123").ien("456").build();
    var expected = "123;456";
    assertThat(sample.toString()).isEqualTo(expected);
  }
}
