package gov.va.api.health.vistafhirquery.service.controller;

import static org.apache.commons.lang3.StringUtils.isBlank;

import javax.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;

/** Utility class for request parameters. */
@UtilityClass
public class HttpRequestParameters {

  /**
   * Return the integer value of the request parameter if available and parsable as an int.
   * Otherwise, return the given default value.
   */
  public static int integer(HttpServletRequest request, String parameterName, int defaultValue) {
    String maybeInt = request.getParameter(parameterName);
    if (isBlank(maybeInt)) {
      return defaultValue;
    }
    try {
      return Integer.parseInt(maybeInt);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }
}
