package gov.va.api.health.vistafhirquery.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import gov.va.api.health.vistafhirquery.tests.TestIds.IcnAtSites;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TestIdsTest {
  static Stream<Arguments> icnAtSiteCsvOf() {
    return Stream.of(
        arguments((String) null, List.of()),
        arguments("", List.of()),
        arguments(",,,", List.of()),
        arguments(
            "999@123", List.of(IcnAtSites.builder().icn("999").vistas(List.of("123")).build())),
        arguments(
            "999@123,888@456+789,777@789",
            List.of(
                IcnAtSites.builder().icn("999").vistas(List.of("123")).build(),
                IcnAtSites.builder().icn("888").vistas(List.of("456", "789")).build(),
                IcnAtSites.builder().icn("777").vistas(List.of("789")).build())),
        arguments(
            ",,999@123,,888@456,,",
            List.of(
                IcnAtSites.builder().icn("999").vistas(List.of("123")).build(),
                IcnAtSites.builder().icn("888").vistas(List.of("456")).build())));
  }

  static Stream<Arguments> icnAtSiteOf() {
    return Stream.of(
        arguments(
            "1234567890V123456@123",
            IcnAtSites.builder().icn("1234567890V123456").vistas(List.of("123")).build()),
        arguments(
            "1234567890@123+456",
            IcnAtSites.builder().icn("1234567890").vistas(List.of("123", "456")).build()),
        arguments(
            "1234567890@++123++456++",
            IcnAtSites.builder().icn("1234567890").vistas(List.of("123", "456")).build()),
        arguments(
            "1234567890V123456@123+456+789",
            IcnAtSites.builder()
                .icn("1234567890V123456")
                .vistas(List.of("123", "456", "789"))
                .build()));
  }

  static Stream<Arguments> icnAtSiteOfThrowsExceptionForBadString() {
    return Stream.of(
        arguments(""),
        arguments((String) null),
        arguments("1234567890V123456"),
        arguments("1234567890V123456@"),
        arguments("@123"));
  }

  @ParameterizedTest
  @MethodSource
  void icnAtSiteCsvOf(String icnAtSiteCsv, List<IcnAtSites> expected) {
    assertThat(IcnAtSites.csvOf(icnAtSiteCsv)).containsExactlyInAnyOrderElementsOf(expected);
  }

  @ParameterizedTest
  @MethodSource
  void icnAtSiteOf(String icnAtSite, IcnAtSites expected) {
    assertThat(IcnAtSites.of(icnAtSite)).isEqualTo(expected);
  }

  @ParameterizedTest
  @MethodSource
  void icnAtSiteOfThrowsExceptionForBadString(String icnAtSite) {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> IcnAtSites.of(icnAtSite));
  }
}
