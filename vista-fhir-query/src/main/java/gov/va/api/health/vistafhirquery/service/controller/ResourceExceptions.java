package gov.va.api.health.vistafhirquery.service.controller;

public class ResourceExceptions {

  public static final class NotFound extends ResourceException {
    public NotFound(String message) {
      super(message);
    }

    public static void because(String message) {
      throw new NotFound(message);
    }
  }

  static class ResourceException extends RuntimeException {
    ResourceException(String message) {
      super(message);
    }
  }
}
