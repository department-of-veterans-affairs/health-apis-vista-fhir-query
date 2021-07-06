package gov.va.api.health.vistafhirquery.service.controller;

import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.isBlank;

import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayResponse.Values;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RpcGatewayTransformers {
  /**
   * Return an Integer representation of the internal value if possible, otherwise throw an
   * UnexpectedVistaValue exception.
   */
  public static Integer internalValueAsIntegerOrDie(Values value, String errorMessage) {
    if (isInternalValueBlank(value)) {
      throw new UnexpectedVistaValue(errorMessage, null);
    }
    try {
      return Integer.parseInt(value.in());
    } catch (NumberFormatException e) {
      throw new UnexpectedVistaValue(errorMessage, value.in());
    }
  }

  /** Return true if the internal value is not available or blank. */
  public static boolean isInternalValueBlank(Values values) {
    return isBlank(values) || isBlank(values.in());
  }

  /** Return true if the internal value is available and not blank. */
  public static boolean isInternalValueNotBlank(Values values) {
    return !isInternalValueBlank(values);
  }

  /** Return true for "1", false for "0", otherwise throw an unexpected vista value exception. */
  @SuppressWarnings("UnnecessaryParentheses") // ErrorProne confused by switch
  public static boolean yesNoToBoolean(String zeroOrOne) {
    if (isBlank(zeroOrOne)) {
      throw new UnexpectedVistaValue("Yes/No code", null);
    }
    return switch (zeroOrOne) {
      case "0" -> false;
      case "1" -> true;
      default -> throw new UnexpectedVistaValue("Yes/No code", zeroOrOne);
    };
  }

  /**
   * A illegal argument exception that indicates the value from Vista is unexpected, e.g. receiving
   * a non-numeric value in a field that is 'guaranteed' to be a number.
   */
  public static class UnexpectedVistaValue extends IllegalArgumentException {
    public UnexpectedVistaValue(String message, Object unexpectedValue) {
      super(String.format("%s, got: %s", message, unexpectedValue));
    }
  }
}
