package gov.va.api.health.vistafhirquery.service.controller;

import com.google.errorprone.annotations.FormatMethod;

public class ResourceExceptions {

  public static final class NotFound extends ResourceException {
    public NotFound(String message) {
      super(message);
    }

    public static void because(String message) {
      throw new NotFound(message);
    }
  }

  public static final class ExpectationFailed extends ResourceException {
    public ExpectationFailed(String message) {
      super(message);
    }

    public static void because(String message) {
      throw new ExpectationFailed(message);
    }

    @FormatMethod
    public static void because(String message, Object... values) {
      because(String.format(message, values));
    }
  }

  static class ResourceException extends RuntimeException {
    ResourceException(String message) {
      super(message);
    }
  }
}
