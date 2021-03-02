package gov.va.api.health.vistafhirquery.service.controller;

import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.toOptionalString;

import gov.va.api.health.fhir.api.FhirDateTimeParameter;
import java.time.Instant;
import java.util.Optional;
import lombok.Getter;

public class DateSearchBoundaries {
  private final FhirDateTimeParameter date1;

  private final FhirDateTimeParameter date2;

  @Getter Optional<String> start;

  @Getter Optional<String> stop;

  /** Compute start and stop search boundaries for the given date(s). */
  public DateSearchBoundaries(FhirDateTimeParameter d1, FhirDateTimeParameter d2) {
    date1 = d1;
    date2 = d2;
    if (date1 != null) {
      computeStartStop();
    }
    start = start == null ? Optional.empty() : start;
    stop = stop == null ? Optional.empty() : stop;
  }

  private void computeStartStop() {
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

  private void equalToDate1() {
    if (date2 == null) {
      start = toOptionalString(date1.lowerBound());
      stop = toOptionalString(date1.upperBound());
    } else {
      switch (date2.prefix()) {
        case EQ:
          equalToDate1EqualToDate2();
          break;
        case SA:
          // fall-through
        case GT:
          equalToDate1GreaterThanDate2();
          break;
        case EB:
          // fall-through
        case LT:
          equalToDate1LessThanDate2();
          break;
        case GE:
          equalToDate1GreaterThanOrEqualToDate2();
          break;
        case LE:
          equalToDate1LessThanOrEqualToDate2();
          break;
        case AP:
          throw new UnsupportedOperationException("AP search prefix not implemented");
        default:
          throw new IllegalStateException("FhirDateTimeParameter doesnt support this prefix.");
      }
    }
  }

  private void equalToDate1EqualToDate2() {
    if (date1.equals(date2)) {
      start = toOptionalString(date1.lowerBound());
      stop = toOptionalString(date1.upperBound());
    } else {
      invalidDateCombination();
    }
  }

  private void equalToDate1GreaterThanDate2() {
    if (date1.lowerBound().isAfter(date2.upperBound())) {
      start = toOptionalString(date1.lowerBound());
      stop = toOptionalString(date1.upperBound());
    } else {
      invalidDateCombination();
    }
  }

  private void equalToDate1GreaterThanOrEqualToDate2() {
    if (!date1.lowerBound().isBefore(date2.lowerBound())) {
      start = toOptionalString(date1.lowerBound());
      stop = toOptionalString(date1.upperBound());
    } else {
      invalidDateCombination();
    }
  }

  private void equalToDate1LessThanDate2() {
    if (date1.upperBound().isBefore(date2.upperBound())) {
      start = toOptionalString(date1.lowerBound());
      stop = toOptionalString(date1.upperBound());
    } else {
      invalidDateCombination();
    }
  }

  private void equalToDate1LessThanOrEqualToDate2() {
    if (!date1.lowerBound().isAfter(date2.upperBound())) {
      start = toOptionalString(date1.lowerBound());
      stop = toOptionalString(date1.upperBound());
    } else {
      invalidDateCombination();
    }
  }

  private void greaterThanDate1() {
    if (date2 == null) {
      start = toOptionalString(date1.upperBound());
    } else {
      switch (date2.prefix()) {
        case EQ:
          greaterThanDate1EqualToDate2();
          break;
        case SA:
          // fall-through
        case GT:
          greaterThanDate1GreaterThanDate2();
          break;
        case EB:
          // fall-through
        case LT:
          greaterThanDate1LessThanDate2();
          break;
        case GE:
          greaterThanDate1GreaterThanOrEqualToDate2();
          break;
        case LE:
          greaterThanDate1LessThanOrEqualToDate2();
          break;
        case AP:
          throw new UnsupportedOperationException("AP search prefix not implemented");
        default:
          throw new IllegalStateException("FhirDateTimeParameter doesnt support this prefix.");
      }
    }
  }

  private void greaterThanDate1EqualToDate2() {
    if (date1.upperBound().isBefore(date2.lowerBound())) {
      start = toOptionalString(date2.lowerBound());
      stop = toOptionalString(date2.upperBound());
    } else {
      invalidDateCombination();
    }
  }

  private void greaterThanDate1GreaterThanDate2() {
    start = toOptionalString(maxInstant(date1.upperBound(), date2.upperBound()));
  }

  private void greaterThanDate1GreaterThanOrEqualToDate2() {
    start = toOptionalString(maxInstant(date1.upperBound(), date2.lowerBound()));
  }

  private void greaterThanDate1LessThanDate2() {
    if (date1.upperBound().isBefore(date2.lowerBound())) {
      start = toOptionalString(date1.upperBound());
      stop = toOptionalString(date2.lowerBound());
    } else {
      invalidDateCombination();
    }
  }

  private void greaterThanDate1LessThanOrEqualToDate2() {
    if (date1.upperBound().isBefore(date2.upperBound())) {
      start = toOptionalString(date1.upperBound());
      stop = toOptionalString(date2.upperBound());
    } else {
      invalidDateCombination();
    }
  }

  private void greaterThanOrEqualToDate1() {
    if (date2 == null) {
      start = toOptionalString(date1.lowerBound());
    } else {
      switch (date2.prefix()) {
        case EQ:
          greaterThanOrEqualToDate1EqualToDate2();
          break;
        case SA:
          // fall-through
        case GT:
          greaterThanOrEqualToDate1GreaterThanDate2();
          break;
        case EB:
          // fall-through
        case LT:
          greaterThanOrEqualToDate1LessThanDate2();
          break;
        case GE:
          greaterThanOrEqualToDate1GreaterThanOrEqualToDate2();
          break;
        case LE:
          greaterThanOrEqualToDate1LessThanOrEqualToDate2();
          break;
        case AP:
          throw new UnsupportedOperationException("AP search prefix not implemented");
        default:
          throw new IllegalStateException("FhirDateTimeParameter doesnt support this prefix.");
      }
    }
  }

  private void greaterThanOrEqualToDate1EqualToDate2() {
    if (!date1.lowerBound().isAfter(date2.lowerBound())) {
      start = toOptionalString(date2.lowerBound());
      stop = toOptionalString(date2.upperBound());
    } else {
      invalidDateCombination();
    }
  }

  private void greaterThanOrEqualToDate1GreaterThanDate2() {
    start = toOptionalString(maxInstant(date1.lowerBound(), date2.upperBound()));
  }

  private void greaterThanOrEqualToDate1GreaterThanOrEqualToDate2() {
    start = toOptionalString(maxInstant(date1.lowerBound(), date2.lowerBound()));
  }

  private void greaterThanOrEqualToDate1LessThanDate2() {
    if (date1.lowerBound().isBefore(date2.lowerBound())) {
      start = toOptionalString(date1.lowerBound());
      stop = toOptionalString(date2.lowerBound());
    } else {
      invalidDateCombination();
    }
  }

  private void greaterThanOrEqualToDate1LessThanOrEqualToDate2() {
    if (!date1.lowerBound().isAfter(date2.lowerBound())) {
      start = toOptionalString(date1.lowerBound());
      stop = toOptionalString(date2.upperBound());
    } else {
      invalidDateCombination();
    }
  }

  // TODO: Web exception handler goodness with bad request (400)
  private void invalidDateCombination() {
    throw new ResourceExceptions.BadSearchParameters(
        "Bad date search combination : date=" + date1.toString() + "&" + date2.toString());
  }

  private void lessThanDate1() {
    if (date2 == null) {
      stop = toOptionalString(date1.lowerBound());
    } else {
      switch (date2.prefix()) {
        case EQ:
          lessThanDate1EqualToDate2();
          break;
        case SA:
          // fall-through
        case GT:
          lessThanDate1GreaterThanDate2();
          break;
        case EB:
          // fall-through
        case LT:
          lessThanDate1LessThanDate2();
          break;
        case GE:
          lessThanDate1GreaterThanOrEqualToDate2();
          break;
        case LE:
          lessThanDate1LessThanOrEqualToDate2();
          break;
        case AP:
          throw new UnsupportedOperationException("AP search prefix not implemented");
        default:
          throw new IllegalStateException("FhirDateTimeParameter doesnt support this prefix.");
      }
    }
  }

  private void lessThanDate1EqualToDate2() {
    if (date1.lowerBound().isAfter(date2.upperBound())) {
      start = toOptionalString(date2.lowerBound());
      stop = toOptionalString(date2.upperBound());
    } else {
      invalidDateCombination();
    }
  }

  private void lessThanDate1GreaterThanDate2() {
    if (date1.lowerBound().isAfter(date2.upperBound())) {
      start = toOptionalString(date2.upperBound());
      stop = toOptionalString(date1.lowerBound());
    } else {
      invalidDateCombination();
    }
  }

  private void lessThanDate1GreaterThanOrEqualToDate2() {
    if (date1.lowerBound().isAfter(date2.lowerBound())) {
      start = toOptionalString(date2.lowerBound());
      stop = toOptionalString(date1.lowerBound());
    } else {
      invalidDateCombination();
    }
  }

  private void lessThanDate1LessThanDate2() {
    stop = toOptionalString(minInstant(date1.lowerBound(), date2.lowerBound()));
  }

  private void lessThanDate1LessThanOrEqualToDate2() {
    stop = toOptionalString(minInstant(date1.lowerBound(), date2.upperBound()));
  }

  private void lessThanOrEqualToDate1() {
    if (date2 == null) {
      stop = toOptionalString(date1.upperBound());
    } else {
      switch (date2.prefix()) {
        case EQ:
          lessThanOrEqualToDate1EqualToDate2();
          break;
        case SA:
          // fall-through
        case GT:
          lessThanOrEqualToDate1GreaterThanDate2();
          break;
        case EB:
          // fall-through
        case LT:
          lessThanOrEqualToDate1LessThanDate2();
          break;
        case GE:
          lessThanOrEqualToDate1GreaterThanOrEqualToDate2();
          break;
        case LE:
          lessThanOrEqualToDate1LessThanOrEqualToDate2();
          break;
        case AP:
          throw new UnsupportedOperationException("AP search prefix not implemented");
        default:
          throw new IllegalStateException("FhirDateTimeParameter doesnt support this prefix.");
      }
    }
  }

  private void lessThanOrEqualToDate1EqualToDate2() {
    if (!date1.upperBound().isBefore(date2.lowerBound())) {
      start = toOptionalString(date2.lowerBound());
      stop = toOptionalString(date2.upperBound());
    } else {
      invalidDateCombination();
    }
  }

  private void lessThanOrEqualToDate1GreaterThanDate2() {
    if (date1.upperBound().isAfter(date2.upperBound())) {
      start = toOptionalString(date2.upperBound());
      stop = toOptionalString(date1.upperBound());
    } else {
      invalidDateCombination();
    }
  }

  private void lessThanOrEqualToDate1GreaterThanOrEqualToDate2() {
    if (!date1.upperBound().isBefore(date2.lowerBound())) {
      start = toOptionalString(date2.lowerBound());
      stop = toOptionalString(date1.upperBound());
    } else {
      invalidDateCombination();
    }
  }

  private void lessThanOrEqualToDate1LessThanDate2() {
    stop = toOptionalString(minInstant(date1.upperBound(), date2.lowerBound()));
  }

  private void lessThanOrEqualToDate1LessThanOrEqualToDate2() {
    stop = toOptionalString(minInstant(date1.upperBound(), date2.upperBound()));
  }

  private Instant maxInstant(Instant a, Instant b) {
    return a.isAfter(b) ? a : b;
  }

  private Instant minInstant(Instant a, Instant b) {
    return a.isBefore(b) ? a : b;
  }
}
