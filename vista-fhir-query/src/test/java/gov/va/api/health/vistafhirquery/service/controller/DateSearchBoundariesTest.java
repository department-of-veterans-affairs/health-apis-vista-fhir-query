package gov.va.api.health.vistafhirquery.service.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import gov.va.api.health.fhir.api.FhirDateTimeParameter;
import java.time.Instant;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class DateSearchBoundariesTest {
  private static final FhirDateTimeParameter equalTo2005 = new FhirDateTimeParameter("2005");

  private static final FhirDateTimeParameter equalTo2006 = new FhirDateTimeParameter("2006");

  private static final FhirDateTimeParameter equalTo2007 = new FhirDateTimeParameter("2007");

  private static final FhirDateTimeParameter greaterThan2005 = new FhirDateTimeParameter("gt2005");

  private static final FhirDateTimeParameter greaterThan2006 = new FhirDateTimeParameter("gt2006");

  private static final FhirDateTimeParameter greaterThanOrEqualTo2005 =
      new FhirDateTimeParameter("ge2005");

  private static final FhirDateTimeParameter greaterThanOrEqualTo2006 =
      new FhirDateTimeParameter("ge2006");

  private static final FhirDateTimeParameter greaterThanOrEqualTo2007 =
      new FhirDateTimeParameter("ge2007");

  private static final FhirDateTimeParameter lessThan2006 = new FhirDateTimeParameter("lt2006");

  private static final FhirDateTimeParameter lessThan2007 = new FhirDateTimeParameter("lt2007");

  private static final FhirDateTimeParameter lessThanOrEqualTo2006 =
      new FhirDateTimeParameter("le2006");

  private static final FhirDateTimeParameter lessThanOrEqualTo2007 =
      new FhirDateTimeParameter("le2007");

  private static final FhirDateTimeParameter approximately2006 =
      new FhirDateTimeParameter("ap2006");

  private static Stream<Arguments> approximatelyDateCombinations() {
    return // date 1 eq
    Stream.of(
        arguments(approximately2006, null),
        arguments(equalTo2006, approximately2006),
        arguments(greaterThanOrEqualTo2006, approximately2006),
        arguments(greaterThan2006, approximately2006),
        arguments(lessThanOrEqualTo2006, approximately2006),
        arguments(lessThan2006, approximately2006));
  }

  private static Stream<Arguments> invalidDateArguments() {
    return // date 1 eq
    Stream.of(
        arguments(equalTo2006, equalTo2007),
        arguments(equalTo2005, greaterThanOrEqualTo2006),
        arguments(equalTo2006, greaterThan2006),
        arguments(equalTo2007, lessThanOrEqualTo2006),
        arguments(equalTo2007, lessThan2007),
        arguments(greaterThanOrEqualTo2006, equalTo2005),
        arguments(greaterThanOrEqualTo2007, lessThanOrEqualTo2006),
        arguments(greaterThanOrEqualTo2006, lessThan2006),
        arguments(greaterThan2006, equalTo2006),
        arguments(greaterThan2006, lessThanOrEqualTo2006),
        arguments(greaterThan2006, lessThan2006),
        arguments(lessThanOrEqualTo2006, equalTo2007),
        arguments(lessThanOrEqualTo2006, greaterThanOrEqualTo2007),
        arguments(lessThanOrEqualTo2006, greaterThan2006),
        arguments(lessThan2007, equalTo2007),
        arguments(lessThan2006, greaterThanOrEqualTo2006),
        arguments(lessThan2006, greaterThan2006));
  }

  private static Stream<Arguments> validDateArguments() {
    return Stream.of(
        arguments(equalTo2006, null, equalTo2006.lowerBound(), equalTo2006.upperBound()),
        arguments(equalTo2006, equalTo2006, equalTo2006.lowerBound(), equalTo2006.upperBound()),
        arguments(
            equalTo2006,
            greaterThanOrEqualTo2006,
            equalTo2006.lowerBound(),
            equalTo2006.upperBound()),
        arguments(equalTo2006, greaterThan2005, equalTo2006.lowerBound(), equalTo2006.upperBound()),
        arguments(
            equalTo2006, lessThanOrEqualTo2006, equalTo2006.lowerBound(), equalTo2006.upperBound()),
        arguments(equalTo2006, lessThan2007, equalTo2006.lowerBound(), equalTo2006.upperBound()),
        arguments(greaterThanOrEqualTo2006, null, greaterThanOrEqualTo2006.lowerBound(), null),
        arguments(
            greaterThanOrEqualTo2006,
            equalTo2006,
            equalTo2006.lowerBound(),
            equalTo2006.upperBound()),
        arguments(
            greaterThanOrEqualTo2006,
            greaterThanOrEqualTo2005,
            greaterThanOrEqualTo2006.lowerBound(),
            null),
        arguments(greaterThanOrEqualTo2006, greaterThan2006, greaterThan2006.upperBound(), null),
        arguments(
            greaterThanOrEqualTo2006,
            lessThanOrEqualTo2006,
            greaterThanOrEqualTo2006.lowerBound(),
            lessThanOrEqualTo2006.upperBound()),
        arguments(
            greaterThanOrEqualTo2006,
            lessThan2007,
            greaterThanOrEqualTo2006.lowerBound(),
            lessThan2007.lowerBound()),
        arguments(greaterThan2006, null, greaterThan2006.upperBound(), null),
        arguments(greaterThan2005, equalTo2006, equalTo2006.lowerBound(), equalTo2006.upperBound()),
        arguments(greaterThan2005, greaterThanOrEqualTo2005, greaterThan2005.upperBound(), null),
        arguments(greaterThan2005, greaterThan2006, greaterThan2006.upperBound(), null),
        arguments(
            greaterThan2005,
            lessThanOrEqualTo2006,
            greaterThan2005.upperBound(),
            lessThanOrEqualTo2006.upperBound()),
        arguments(
            greaterThan2005, lessThan2006, greaterThan2005.upperBound(), lessThan2006.lowerBound()),
        arguments(lessThanOrEqualTo2006, null, null, lessThanOrEqualTo2006.upperBound()),
        arguments(
            lessThanOrEqualTo2006, equalTo2006, equalTo2006.lowerBound(), equalTo2006.upperBound()),
        arguments(
            lessThanOrEqualTo2006,
            greaterThanOrEqualTo2005,
            greaterThanOrEqualTo2005.lowerBound(),
            lessThanOrEqualTo2006.upperBound()),
        arguments(
            lessThanOrEqualTo2006,
            greaterThan2005,
            greaterThan2005.upperBound(),
            lessThanOrEqualTo2006.upperBound()),
        arguments(
            lessThanOrEqualTo2006, lessThanOrEqualTo2007, null, lessThanOrEqualTo2006.upperBound()),
        arguments(lessThanOrEqualTo2006, lessThan2006, null, lessThan2006.lowerBound()),
        arguments(lessThan2006, null, null, lessThan2006.lowerBound()),
        arguments(lessThan2007, equalTo2006, equalTo2006.lowerBound(), equalTo2006.upperBound()),
        arguments(
            lessThan2006,
            greaterThanOrEqualTo2005,
            greaterThanOrEqualTo2005.lowerBound(),
            lessThan2006.lowerBound()),
        arguments(
            lessThan2006, greaterThan2005, greaterThan2005.upperBound(), lessThan2006.lowerBound()),
        arguments(lessThan2006, lessThanOrEqualTo2006, null, lessThan2006.lowerBound()),
        arguments(lessThan2006, lessThan2007, null, lessThan2006.lowerBound()));
  }

  @ParameterizedTest
  @MethodSource("approximatelyDateCombinations")
  void approximatelyThrowsUnsupportedOperation(
      FhirDateTimeParameter date1, FhirDateTimeParameter date2) {
    assertThatExceptionOfType(UnsupportedOperationException.class)
        .isThrownBy(() -> new DateSearchBoundaries(date1, date2));
  }

  @ParameterizedTest
  @MethodSource("validDateArguments")
  void correctStartAndStopForValidDates(
      FhirDateTimeParameter date1,
      FhirDateTimeParameter date2,
      Instant expectedStart,
      Instant expectedStop) {
    assertThat(new DateSearchBoundaries(date1, date2).start()).isEqualTo(expectedStart);
    assertThat(new DateSearchBoundaries(date1, date2).stop()).isEqualTo(expectedStop);
  }

  @ParameterizedTest
  @MethodSource("invalidDateArguments")
  void invalidDatesThrowBadSearchParameters(
      FhirDateTimeParameter date1, FhirDateTimeParameter date2) {
    assertThatExceptionOfType(ResourceExceptions.BadSearchParameters.class)
        .isThrownBy(() -> new DateSearchBoundaries(date1, date2));
  }
}
