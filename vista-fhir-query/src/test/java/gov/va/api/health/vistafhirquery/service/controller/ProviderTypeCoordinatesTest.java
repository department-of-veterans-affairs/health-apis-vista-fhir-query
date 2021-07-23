package gov.va.api.health.vistafhirquery.service.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ProviderTypeCoordinatesTest {
  @Test
  void identifierFromStringSuccess() {
    var sample = "123+456";
    var expected = ProviderTypeCoordinates.builder().siteId("123").recordId("456").build();
    assertThat(ProviderTypeCoordinates.fromString(sample)).isEqualTo(expected);
  }

  @ParameterizedTest
  @ValueSource(strings = {"123", "p1+123+456"})
  void identifierFromStringThrowsIllegalArgumentForBadValues(String badId) {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> ProviderTypeCoordinates.fromString(badId));
  }

  @Test
  void identifierToString() {
    var sample = ProviderTypeCoordinates.builder().siteId("123").recordId("456").build();
    var expected = "123+456";
    assertThat(sample.toString()).isEqualTo(expected);
  }
}
