package gov.va.api.health.vistafhirquery.service.controller;

import gov.va.api.health.fhir.api.IsReference;
import gov.va.api.health.r4.api.elements.Reference;
import gov.va.api.lighthouse.charon.models.FilemanDate;
import gov.va.api.lighthouse.charon.models.ValueOnlyXmlAttribute;
import gov.va.api.lighthouse.charon.models.vprgetpatientdata.VprGetPatientData;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

/** Utility class for common transformations. */
@UtilityClass
public class R4Transformers {
  private static final Pattern BIG_DECIMAL_PATTERN = Pattern.compile("\\d+(\\.\\d+)?");

  /**
   * Return false if at least one value in the given list is a non-blank string, or a non-null
   * object.
   */
  public static boolean allBlank(Object... values) {
    for (Object v : values) {
      if (!isBlank(v)) {
        return false;
      }
    }
    return true;
  }

  /** Given a reference, attempt to get the reference Id from the reference field. */
  public static Optional<String> getReferenceId(IsReference maybeReference) {
    if (maybeReference == null || maybeReference.reference() == null) {
      return Optional.empty();
    }
    String reference = maybeReference.reference();
    String[] referenceParts = reference.split("/", -1);
    if (referenceParts.length <= 1) {
      return Optional.empty();
    }
    return Optional.ofNullable(referenceParts[referenceParts.length - 1]);
  }

  /**
   * Return the result of the given extractor function if the given object is present. The object
   * will be passed to the apply method of the extractor function.
   *
   * <p>Consider this example:
   *
   * <pre>
   * {@code ifPresent(patient.getGender(), gender -> Patient.Gender.valueOf(gender.value()))}
   * </pre>
   *
   * This is equivalent to this standard Java code.
   *
   * <pre>{@code
   * Gender gender = patient.getGender();
   * if (gender == null) {
   *   return null;
   * } else {
   *   return Patient.Gender.valueOf(gender.value());
   * }
   * }</pre>
   */
  public static <T, R> R ifPresent(T object, Function<T, R> extract) {
    if (isBlank(object)) {
      return null;
    }
    return extract.apply(object);
  }

  /** Return true if the value is a blank string, or any other object that is null. */
  public static boolean isBlank(Object value) {
    if (value instanceof CharSequence) {
      return StringUtils.isBlank((CharSequence) value);
    }
    if (value instanceof Collection<?>) {
      return ((Collection<?>) value).isEmpty();
    }
    if (value instanceof Optional<?>) {
      return ((Optional<?>) value).isEmpty() || isBlank(((Optional<?>) value).get());
    }
    if (value instanceof Map<?, ?>) {
      return ((Map<?, ?>) value).isEmpty();
    }
    return value == null;
  }

  /** Creates a BigDecimal from a string if possible, otherwise returns null. */
  public static BigDecimal toBigDecimal(String string) {
    if (isBlank(string)) {
      return null;
    }
    if (BIG_DECIMAL_PATTERN.matcher(string).matches()) {
      return new BigDecimal(string);
    }
    return null;
  }

  /** Transform a FileMan date to a human date. */
  public static String toHumanDateTime(ValueOnlyXmlAttribute filemanDateTime) {
    FilemanDate result = FilemanDate.from(filemanDateTime, ZoneId.of("UTC"));
    if (isBlank(result)) {
      return null;
    }
    return result.instant().toString();
  }

  /** Transform an Instant to an Optional String. */
  public static Optional<String> toIso8601(Instant maybeInstant) {
    if (maybeInstant == null) {
      return Optional.empty();
    }
    return Optional.of(maybeInstant.toString());
  }

  /** Take an instant and from a local-fileman-date macro that charon can consume. */
  public static Optional<String> toLocalDateMacroString(Instant maybeInstant) {
    if (maybeInstant == null) {
      return Optional.empty();
    }
    return Optional.of(String.format("${local-fileman-date(%s)}", maybeInstant));
  }

  /** Create a reference sing the resourceType, an id, and a display. */
  public static Reference toReference(
      @NonNull String resourceType, String maybeId, String maybeDisplay) {
    if (allBlank(maybeId, maybeDisplay)) {
      return null;
    }
    return Reference.builder()
        .reference(ifPresent(maybeId, id -> resourceType + "/" + id))
        .display(maybeDisplay)
        .build();
  }

  /** Build an Identifier Segment using patientId, siteId, and the recordId. */
  public static String toResourceId(
      String patientId, String siteId, VprGetPatientData.Domains recordDomain, String recordId) {
    if (isBlank(recordId)) {
      return null;
    }
    return SegmentedVistaIdentifier.builder()
        .patientIdentifierType(SegmentedVistaIdentifier.PatientIdentifierType.NATIONAL_ICN)
        .patientIdentifier(patientId)
        .vistaSiteId(siteId)
        .vprRpcDomain(recordDomain)
        .vistaRecordId(recordId)
        .build()
        .pack();
  }

  public static String toResourceId(String patientId, String siteId, String recordId) {
    return toResourceId(patientId, siteId, null, recordId);
  }

  /** Gets value of a ValueOnlyXmlAttribute if it exists. */
  public static String valueOfValueOnlyXmlAttribute(ValueOnlyXmlAttribute valueOnlyXmlAttribute) {
    if (isBlank(valueOnlyXmlAttribute)) {
      return null;
    } else {
      return valueOnlyXmlAttribute.value();
    }
  }
}
