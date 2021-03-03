package gov.va.api.health.vistafhirquery.service.controller;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.autoconfig.encryption.BasicEncryption;
import gov.va.api.health.r4.api.elements.Extension;
import gov.va.api.health.r4.api.elements.Narrative;
import gov.va.api.health.r4.api.resources.OperationOutcome;
import java.lang.reflect.UndeclaredThrowableException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Exceptions that escape the rest controllers will be processed by this handler. It will convert
 * exception into different HTTP status codes and produce an error response payload.
 */
@Slf4j
@RestControllerAdvice
@RequestMapping(produces = "application/json")
public final class WebExceptionHandler {
  private final String encryptionKey;

  /** Uses encryption key if one is set. */
  public WebExceptionHandler(
      @Value("${vista-fhir-query.public-web-exception-key}") String encryptionKey) {
    checkState(
        !"unset".equals(encryptionKey), "vista-fhir-query.public-web-exception-key is unset");
    this.encryptionKey = encryptionKey;
  }

  private static List<Throwable> causes(Throwable tr) {
    List<Throwable> results = new ArrayList<>();
    Throwable current = tr;
    while (true) {
      current = current.getCause();
      if (current == null) {
        return results;
      }
      results.add(current);
    }
  }

  private static boolean isJsonError(Throwable tr) {
    Throwable current = tr;
    while (current != null) {
      if (JsonProcessingException.class.isAssignableFrom(current.getClass())) {
        return true;
      }
      current = current.getCause();
    }
    return false;
  }

  /** Reconstruct a sanitized URL based on the request. */
  private static String reconstructUrl(HttpServletRequest request) {
    return request.getRequestURI()
        + (request.getQueryString() == null ? "" : "?" + request.getQueryString())
            .replaceAll("[\r\n]", "");
  }

  static String sanitizedMessage(Throwable tr) {
    if (tr instanceof MismatchedInputException) {
      MismatchedInputException mie = (MismatchedInputException) tr;
      return String.format("path: %s", mie.getPathReference());
    }

    if (tr instanceof JsonEOFException) {
      JsonEOFException eofe = (JsonEOFException) tr;
      if (eofe.getLocation() != null) {
        return String.format(
            "line: %s, column: %s",
            eofe.getLocation().getLineNr(), eofe.getLocation().getColumnNr());
      }
    }

    if (tr instanceof JsonMappingException) {
      JsonMappingException jme = (JsonMappingException) tr;
      return String.format("path: %s", jme.getPathReference());
    }

    if (tr instanceof JsonParseException) {
      JsonParseException jpe = (JsonParseException) tr;
      if (jpe.getLocation() != null) {
        return String.format(
            "line: %s, column: %s", jpe.getLocation().getLineNr(), jpe.getLocation().getColumnNr());
      }
    }

    return tr.getMessage();
  }

  private OperationOutcome asOperationOutcome(
      String code, Throwable tr, HttpServletRequest request, List<String> diagnostics) {
    OperationOutcome.Issue issue =
        OperationOutcome.Issue.builder()
            .severity(OperationOutcome.Issue.IssueSeverity.fatal)
            .code(code)
            .build();
    String diagnostic = diagnostics.stream().collect(joining(", "));
    if (isNotBlank(diagnostic)) {
      issue.diagnostics(diagnostic);
    }
    return OperationOutcome.builder()
        .id(UUID.randomUUID().toString())
        .resourceType("OperationOutcome")
        .extension(extensions(tr, request))
        .text(
            Narrative.builder()
                .status(Narrative.NarrativeStatus.additional)
                .div("<div>Failure: " + request.getRequestURI() + "</div>")
                .build())
        .issue(List.of(issue))
        .build();
  }

  private List<Extension> extensions(Throwable tr, HttpServletRequest request) {
    List<Extension> extensions = new ArrayList<>(5);

    BasicEncryption encrypter = BasicEncryption.forKey(encryptionKey);

    extensions.add(
        Extension.builder().url("timestamp").valueInstant(Instant.now().toString()).build());

    extensions.add(
        Extension.builder().url("type").valueString(tr.getClass().getSimpleName()).build());

    if (isNotBlank(sanitizedMessage(tr))) {
      extensions.add(
          Extension.builder()
              .url("message")
              .valueString(encrypter.encrypt(sanitizedMessage(tr)))
              .build());
    }

    String cause =
        causes(tr).stream()
            .map(t -> t.getClass().getSimpleName() + " " + sanitizedMessage(t))
            .collect(joining(", "));
    if (isNotBlank(cause)) {
      extensions.add(
          Extension.builder().url("cause").valueString(encrypter.encrypt(cause)).build());
    }

    extensions.add(Extension.builder().url("request").valueString(reconstructUrl(request)).build());

    return extensions;
  }

  @ExceptionHandler({
    BindException.class,
    UnsatisfiedServletRequestParameterException.class,
    ResourceExceptions.BadSearchParameters.class
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  OperationOutcome handleBadRequest(Exception e, HttpServletRequest request) {
    return responseFor("structure", e, request, emptyList(), true);
  }

  @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  OperationOutcome handleNotAllowed(Exception e, HttpServletRequest request) {
    return responseFor("not-allowed", e, request, emptyList(), true);
  }

  @ExceptionHandler({HttpClientErrorException.NotFound.class, ResourceExceptions.NotFound.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  OperationOutcome handleNotFound(Exception e, HttpServletRequest request) {
    return responseFor("not-found", e, request, emptyList(), true);
  }

  /**
   * For exceptions relating to unmarshalling json, we want to make sure no PII is being logged.
   * Therefore, when we encounter these exceptions, we will not print the stacktrace to prevent PII
   * showing up in our logs.
   */
  @ExceptionHandler({Exception.class, UndeclaredThrowableException.class})
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public OperationOutcome handleSnafu(Exception e, HttpServletRequest request) {
    if (isJsonError(e)) {
      return responseFor("database", e, request, emptyList(), false);
    }
    return responseFor("exception", e, request, emptyList(), true);
  }

  /**
   * For constraint violation exceptions, we want to add a little more information in the exception
   * to present what exactly is wrong. We will distill which properties are wrong and why, but we
   * will not leak any values.
   */
  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  OperationOutcome handleValidationException(
      ConstraintViolationException e, HttpServletRequest request) {
    List<String> diagnostics =
        e.getConstraintViolations().stream()
            .map(v -> v.getPropertyPath() + " " + v.getMessage())
            .collect(toList());
    return responseFor("structure", e, request, diagnostics, true);
  }

  @SneakyThrows
  private OperationOutcome responseFor(
      String code,
      Throwable tr,
      HttpServletRequest request,
      List<String> diagnostics,
      boolean printStackTrace) {
    OperationOutcome response = asOperationOutcome(code, tr, request, diagnostics);
    if (printStackTrace) {
      log.error("Response {}", JacksonConfig.createMapper().writeValueAsString(response), tr);
    } else {
      log.error("Response {}", JacksonConfig.createMapper().writeValueAsString(response));
    }
    return response;
  }
}
