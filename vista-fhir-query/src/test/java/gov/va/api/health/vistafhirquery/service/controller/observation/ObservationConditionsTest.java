package gov.va.api.health.vistafhirquery.service.controller.observation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ObservationConditionsTest {

  static Stream<Arguments> hasAcceptedCode() {
    // List<String> allowedCodes, String code, boolean expected
    return Stream.of(
        Arguments.of(null, "a", true),
        Arguments.of(List.of(), "a", true),
        Arguments.of(List.of("a"), "a", true),
        Arguments.of(List.of("a"), null, false),
        Arguments.of(List.of("a"), "b", false),
        Arguments.of(List.of("a", "b", "c"), "a", true),
        Arguments.of(List.of("a", "b", "c"), "b", true),
        Arguments.of(List.of("a", "b", "c"), "c", true),
        Arguments.of(List.of("a", "b", "c"), "d", false));
  }

  @ParameterizedTest
  @MethodSource
  void hasAcceptedCode(List<String> allowedCodes, String code, boolean expected) {
    assertThat(ObservationConditions.of(allowedCodes).hasAcceptedCode(code)).isEqualTo(expected);
  }
}
