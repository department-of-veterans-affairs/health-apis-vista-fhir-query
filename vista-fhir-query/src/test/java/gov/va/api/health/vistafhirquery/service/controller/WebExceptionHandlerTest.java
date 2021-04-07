package gov.va.api.health.vistafhirquery.service.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.google.common.collect.ImmutableMap;
import gov.va.api.health.r4.api.elements.Narrative;
import gov.va.api.health.r4.api.resources.OperationOutcome;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

public class WebExceptionHandlerTest {
  @Test
  void badRequest() {
    OperationOutcome outcome =
        new WebExceptionHandler("")
            .handleBadRequest(
                new UnsatisfiedServletRequestParameterException(
                    new String[] {"hello"}, ImmutableMap.of("foo", new String[] {"bar"})),
                mock(HttpServletRequest.class));
    assertThat(outcome.id(null).extension(null)).isEqualTo(operationOutcome("structure"));
  }

  @Test
  void forbidden() {
    HttpClientErrorException forbidden =
        HttpClientErrorException.Forbidden.create(HttpStatus.FORBIDDEN, null, null, null, null);
    OperationOutcome outcome =
        new WebExceptionHandler("")
            .handleInternalServerError(forbidden, mock(HttpServletRequest.class));
    assertThat(outcome.id(null).extension(null))
        .isEqualTo(operationOutcome("internal-server-error"));
  }

  @Test
  void internalServerError() {
    HttpServerErrorException internalServerError =
        HttpServerErrorException.InternalServerError.create(
            HttpStatus.INTERNAL_SERVER_ERROR, null, null, null, null);
    OperationOutcome outcome =
        new WebExceptionHandler("")
            .handleInternalServerError(internalServerError, mock(HttpServletRequest.class));
    assertThat(outcome.id(null).extension(null))
        .isEqualTo(operationOutcome("internal-server-error"));
  }

  @Test
  void notAllowed() {
    OperationOutcome outcome =
        new WebExceptionHandler("")
            .handleNotAllowed(
                new HttpRequestMethodNotSupportedException("method"),
                mock(HttpServletRequest.class));
    assertThat(outcome.id(null).extension(null)).isEqualTo(operationOutcome("not-allowed"));
  }

  @Test
  void notFound() {
    OperationOutcome outcome =
        new WebExceptionHandler("")
            .handleNotFound(new ResourceExceptions.NotFound("x"), mock(HttpServletRequest.class));
    assertThat(outcome.id(null).extension(null)).isEqualTo(operationOutcome("not-found"));
  }

  private OperationOutcome operationOutcome(String code) {
    return OperationOutcome.builder()
        .resourceType("OperationOutcome")
        .text(
            Narrative.builder()
                .status(Narrative.NarrativeStatus.additional)
                .div("<div>Failure: null</div>")
                .build())
        .issue(
            List.of(
                OperationOutcome.Issue.builder()
                    .severity(OperationOutcome.Issue.IssueSeverity.fatal)
                    .code(code)
                    .build()))
        .build();
  }

  @Test
  void requestTimeout() {
    ResourceAccessException requestTimeout = new ResourceAccessException(null);
    OperationOutcome outcome =
        new WebExceptionHandler("")
            .handleRequestTimeout(requestTimeout, mock(HttpServletRequest.class));
    assertThat(outcome.id(null).extension(null)).isEqualTo(operationOutcome("request-timeout"));
  }

  @Test
  void sanitizedMessage_Exception() {
    assertThat(WebExceptionHandler.sanitizedMessage(new RuntimeException("oh noez")))
        .isEqualTo("oh noez");
  }

  @Test
  void sanitizedMessage_exception() {
    assertThat(WebExceptionHandler.sanitizedMessage(new RuntimeException("oh noez")))
        .isEqualTo("oh noez");
  }

  @Test
  void sanitizedMessage_jsonEOFException() {
    JsonEOFException ex = mock(JsonEOFException.class);
    when(ex.getLocation()).thenReturn(new JsonLocation(null, 0, 0, 0));
    assertThat(WebExceptionHandler.sanitizedMessage(ex)).isEqualTo("line: 0, column: 0");
  }

  @Test
  void sanitizedMessage_jsonMappingException() {
    JsonMappingException ex = mock(JsonMappingException.class);
    when(ex.getPathReference()).thenReturn("x");
    assertThat(WebExceptionHandler.sanitizedMessage(ex)).isEqualTo("path: x");
  }

  @Test
  void sanitizedMessage_jsonParseException() {
    JsonParseException ex = mock(JsonParseException.class);
    when(ex.getLocation()).thenReturn(new JsonLocation(null, 0, 0, 0));
    assertThat(WebExceptionHandler.sanitizedMessage(ex)).isEqualTo("line: 0, column: 0");
  }

  @Test
  void sanitizedMessage_mismatchedInputException() {
    MismatchedInputException ex = mock(MismatchedInputException.class);
    when(ex.getPathReference()).thenReturn("path");
    assertThat(WebExceptionHandler.sanitizedMessage(ex)).isEqualTo("path: path");
  }

  @Test
  void snafu_json() {
    OperationOutcome outcome =
        new WebExceptionHandler("")
            .handleSnafu(
                new JsonParseException(mock(JsonParser.class), "x"),
                mock(HttpServletRequest.class));
    assertThat(outcome.id(null).extension(null)).isEqualTo(operationOutcome("database"));
  }

  @Test
  void unauthorized() {
    HttpClientErrorException unauthorized =
        HttpClientErrorException.Unauthorized.create(
            HttpStatus.UNAUTHORIZED, null, null, null, null);
    OperationOutcome outcome =
        new WebExceptionHandler("")
            .handleUnauthorized(unauthorized, mock(HttpServletRequest.class));
    assertThat(outcome.id(null).extension(null)).isEqualTo(operationOutcome("unauthorized"));
  }

  @Test
  void validationException() {
    Set<ConstraintViolation<Foo>> violations =
        Validation.buildDefaultValidatorFactory().getValidator().validate(Foo.builder().build());
    OperationOutcome outcome =
        new WebExceptionHandler("")
            .handleValidationException(
                new ConstraintViolationException(violations), mock(HttpServletRequest.class));
    assertThat(outcome.id(null).extension(null))
        .isEqualTo(
            OperationOutcome.builder()
                .resourceType("OperationOutcome")
                .text(
                    Narrative.builder()
                        .status(Narrative.NarrativeStatus.additional)
                        .div("<div>Failure: null</div>")
                        .build())
                .issue(
                    List.of(
                        OperationOutcome.Issue.builder()
                            .severity(OperationOutcome.Issue.IssueSeverity.fatal)
                            .code("structure")
                            .diagnostics("bar must not be null")
                            .build()))
                .build());
  }

  @Value
  @Builder
  private static final class Foo {
    @NotNull String bar;
  }
}
