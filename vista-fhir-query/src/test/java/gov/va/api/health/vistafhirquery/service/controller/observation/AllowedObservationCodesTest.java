package gov.va.api.health.vistafhirquery.service.controller.observation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class AllowedObservationCodesTest {

  static Stream<Arguments> hasAcceptedCode() {
    // Map<String, String> allowedCodes, String code, boolean expected
    return Stream.of(
        Arguments.of(Map.of(), "a", false),
        Arguments.of(Map.of("a", "A"), "a", true),
        Arguments.of(Map.of("a", "A"), null, false),
        Arguments.of(Map.of("a", "A"), "b", false),
        Arguments.of(Map.of("a", "A", "b", "B", "c", "C"), "a", true),
        Arguments.of(Map.of("a", "A", "b", "B", "c", "C"), "b", true),
        Arguments.of(Map.of("a", "A", "b", "B", "c", "C"), "c", true),
        Arguments.of(Map.of("a", "A", "b", "B", "c", "C"), "d", false));
  }

  @ParameterizedTest
  @MethodSource("hasAcceptedCode")
  void hasAcceptedLoincCode(Map<String, String> allowedCodes, String loinc, boolean expected) {
    if (loinc != null) {
      loinc = loinc.toUpperCase();
    }
    assertThat(AllowedObservationCodes.allowOnly(allowedCodes).isAllowedLoincCode(loinc))
        .isEqualTo(expected);
  }

  @ParameterizedTest
  @MethodSource("hasAcceptedCode")
  void hasAcceptedVuidCode(Map<String, String> allowedCodes, String vuid, boolean expected) {
    assertThat(AllowedObservationCodes.allowOnly(allowedCodes).isAllowedVuidCode(vuid))
        .isEqualTo(expected);
  }
}
