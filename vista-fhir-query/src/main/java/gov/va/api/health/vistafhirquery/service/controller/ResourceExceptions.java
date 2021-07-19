package gov.va.api.health.vistafhirquery.service.controller;

import com.google.errorprone.annotations.FormatMethod;

/** The because methods exist to add readability when throwing exceptions. */
@SuppressWarnings("DoNotCallSuggester")
public class ResourceExceptions {

  /** NotFound . */
  public static final class NotFound extends ResourceException {
    public NotFound(String message) {
      super(message);
    }

    public static NotFound because(String message) {
      return new NotFound(message);
    }
  }

  /** BadSearchParameters . */
  public static final class BadSearchParameters extends ResourceException {
    public BadSearchParameters(String message) {
      super(message);
    }

    public static BadSearchParameters because(String message) {
      return new BadSearchParameters(message);
    }
  }

  /** ExpectationFailed . */
  public static final class ExpectationFailed extends ResourceException {
    public ExpectationFailed(String message) {
      super(message);
    }

    public static ExpectationFailed because(String message) {
      return new ExpectationFailed(message);
    }

    @FormatMethod
    public static ExpectationFailed because(String message, Object... values) {
      return because(String.format(message, values));
    }
  }

  /** ResourceException . */
  static class ResourceException extends RuntimeException {
    ResourceException(String message) {
      super(message);
    }
  }
}
