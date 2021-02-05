package gov.va.api.health.vistafhirquery.service.controller;

import java.math.BigDecimal;
import lombok.experimental.UtilityClass;

@UtilityClass
public class R4Transformers {

  /** Creates a BigDecimal from a string if possible, otherwise returns null. */
  public static BigDecimal toBigDecimal(String string) {
    if (string == null) {
      return null;
    }
    if (string.matches("\\d+(\\.\\d+)?")) {
      return new BigDecimal(string);
    }
    return null;
  }
}
