package gov.va.api.health.vistafhirquery.service.controller;

import gov.va.api.health.fhir.api.FhirDateTimeParameter;
import java.time.Instant;
import lombok.Getter;

/** Deals with these certain date searches defined by fhir : EQ, SA, GT, EB, LT, GE, LE. */
public class DateSearchBoundaries {
  private final FhirDateTimeParameter date1;

  private final FhirDateTimeParameter date2;

  @Getter private Instant start;

  @Getter private Instant stop;

  /** Compute start and stop search boundaries for the given date(s). */
  public DateSearchBoundaries(FhirDateTimeParameter d1, FhirDateTimeParameter d2) {
    date1 = d1;
    date2 = d2;
    if (date1 != null) {
      initializeStartStop();
    }
  }

  /**
   * Takes in an array of ISO 8601 date strings and creates a DateSearchBoundaries with up to the
   * first two.
   */
  public static DateSearchBoundaries of(String[] dates) {
    FhirDateTimeParameter d1 =
        (dates == null || dates.length < 1) ? null : new FhirDateTimeParameter(dates[0]);
    FhirDateTimeParameter d2 =
        (dates == null || dates.length < 2) ? null : new FhirDateTimeParameter(dates[1]);
    return new DateSearchBoundaries(d1, d2);
  }

  private void createBounds(boolean isValid, Instant maybeStart, Instant maybeStop) {
    if (isValid) {
      start = maybeStart;
      stop = maybeStop;
    } else {
      invalidDateCombination();
    }
  }

  private void equalToDate1() {
    if (date2 == null) {
      start = date1.lowerBound();
      stop = date1.upperBound();
      return;
    }
    switch (date2.prefix()) {
      case EQ:
        createBounds(date1.equals(date2), date1.lowerBound(), date1.upperBound());
        break;
      case SA:
        // fall-through
      case GT:
        createBounds(
            date1.lowerBound().isAfter(date2.upperBound()), date1.lowerBound(), date1.upperBound());
        break;
      case EB:
        // fall-through
      case LT:
        createBounds(
            date1.upperBound().isBefore(date2.upperBound()),
            date1.lowerBound(),
            date1.upperBound());
        break;
      case GE:
        createBounds(
            !date1.lowerBound().isBefore(date2.lowerBound()),
            date1.lowerBound(),
            date1.upperBound());
        break;
      case LE:
        createBounds(
            !date1.lowerBound().isAfter(date2.upperBound()),
            date1.lowerBound(),
            date1.upperBound());
        break;
      case AP:
        throw new UnsupportedOperationException("AP search prefix not implemented");
      default:
        throw new IllegalStateException("FhirDateTimeParameter doesnt support this prefix.");
    }
  }

  private void greaterThanDate1() {
    if (date2 == null) {
      start = date1.upperBound();
      return;
    }
    switch (date2.prefix()) {
      case EQ:
        createBounds(
            date1.upperBound().isBefore(date2.lowerBound()),
            date2.lowerBound(),
            date2.upperBound());
        break;
      case SA:
        // fall-through
      case GT:
        createBounds(true, maxInstant(date1.upperBound(), date2.upperBound()), null);
        break;
      case EB:
        // fall-through
      case LT:
        createBounds(
            date1.upperBound().isBefore(date2.lowerBound()),
            date1.upperBound(),
            date2.lowerBound());
        break;
      case GE:
        createBounds(true, maxInstant(date1.upperBound(), date2.lowerBound()), null);
        break;
      case LE:
        createBounds(
            date1.upperBound().isBefore(date2.upperBound()),
            date1.upperBound(),
            date2.upperBound());
        break;
      case AP:
        throw new UnsupportedOperationException("AP search prefix not implemented");
      default:
        throw new IllegalStateException("FhirDateTimeParameter doesnt support this prefix.");
    }
  }

  private void greaterThanOrEqualToDate1() {
    if (date2 == null) {
      start = date1.lowerBound();
      return;
    }
    switch (date2.prefix()) {
      case EQ:
        createBounds(
            !date1.lowerBound().isAfter(date2.lowerBound()),
            date2.lowerBound(),
            date2.upperBound());
        break;
      case SA:
        // fall-through
      case GT:
        createBounds(true, maxInstant(date1.lowerBound(), date2.upperBound()), null);
        break;
      case EB:
        // fall-through
      case LT:
        createBounds(
            date1.lowerBound().isBefore(date2.lowerBound()),
            date1.lowerBound(),
            date2.lowerBound());
        break;
      case GE:
        createBounds(true, maxInstant(date1.lowerBound(), date2.lowerBound()), null);
        break;
      case LE:
        createBounds(
            !date1.lowerBound().isAfter(date2.lowerBound()),
            date1.lowerBound(),
            date2.upperBound());
        break;
      case AP:
        throw new UnsupportedOperationException("AP search prefix not implemented");
      default:
        throw new IllegalStateException("FhirDateTimeParameter doesnt support this prefix.");
    }
  }

  private void initializeStartStop() {
    switch (date1.prefix()) {
      case EQ:
        equalToDate1();
        break;
      case SA:
        // fall-through
      case GT:
        greaterThanDate1();
        break;
      case EB:
        // fall-through
      case LT:
        lessThanDate1();
        break;
      case GE:
        greaterThanOrEqualToDate1();
        break;
      case LE:
        lessThanOrEqualToDate1();
        break;
      case AP:
        throw new UnsupportedOperationException("AP search prefix not implemented");
      default:
        throw new IllegalStateException("FhirDateTimeParameter doesnt support this prefix.");
    }
  }

  private void invalidDateCombination() {
    throw ResourceExceptions.BadSearchParameters.because(
        "Bad date search combination : date=" + date1.toString() + "&" + date2.toString());
  }

  private void lessThanDate1() {
    if (date2 == null) {
      stop = date1.lowerBound();
      return;
    }
    switch (date2.prefix()) {
      case EQ:
        createBounds(
            date1.lowerBound().isAfter(date2.upperBound()), date2.lowerBound(), date2.upperBound());
        break;
      case SA:
        // fall-through
      case GT:
        createBounds(
            date1.lowerBound().isAfter(date2.upperBound()), date2.upperBound(), date1.lowerBound());
        break;
      case EB:
        // fall-through
      case LT:
        createBounds(true, null, minInstant(date1.lowerBound(), date2.lowerBound()));
        break;
      case GE:
        createBounds(
            date1.lowerBound().isAfter(date2.lowerBound()), date2.lowerBound(), date1.lowerBound());
        break;
      case LE:
        createBounds(true, null, minInstant(date1.lowerBound(), date2.upperBound()));
        break;
      case AP:
        throw new UnsupportedOperationException("AP search prefix not implemented");
      default:
        throw new IllegalStateException("FhirDateTimeParameter doesnt support this prefix.");
    }
  }

  private void lessThanOrEqualToDate1() {
    if (date2 == null) {
      stop = date1.upperBound();
      return;
    }
    switch (date2.prefix()) {
      case EQ:
        createBounds(
            !date1.upperBound().isBefore(date2.lowerBound()),
            date2.lowerBound(),
            date2.upperBound());
        break;
      case SA:
        // fall-through
      case GT:
        createBounds(
            date1.upperBound().isAfter(date2.upperBound()), date2.upperBound(), date1.upperBound());
        break;
      case EB:
        // fall-through
      case LT:
        createBounds(true, null, minInstant(date1.upperBound(), date2.lowerBound()));
        break;
      case GE:
        createBounds(
            !date1.upperBound().isBefore(date2.lowerBound()),
            date2.lowerBound(),
            date1.upperBound());
        break;
      case LE:
        createBounds(true, null, minInstant(date1.upperBound(), date2.upperBound()));
        break;
      case AP:
        throw new UnsupportedOperationException("AP search prefix not implemented");
      default:
        throw new IllegalStateException("FhirDateTimeParameter doesnt support this prefix.");
    }
  }

  private Instant maxInstant(Instant a, Instant b) {
    return a.isAfter(b) ? a : b;
  }

  private Instant minInstant(Instant a, Instant b) {
    return a.isBefore(b) ? a : b;
  }
}
