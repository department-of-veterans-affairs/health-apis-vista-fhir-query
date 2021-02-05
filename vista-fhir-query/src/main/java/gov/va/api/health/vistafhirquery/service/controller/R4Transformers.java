package gov.va.api.health.vistafhirquery.service.controller;

import gov.va.api.lighthouse.vistalink.models.ValueOnlyXmlAttribute;
import java.math.BigDecimal;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;

@UtilityClass
public class R4Transformers {
  private static final Pattern BIG_DECIMAL_PATTERN = Pattern.compile("\\d+(\\.\\d+)?");

  /** Creates a BigDecimal from a string if possible, otherwise returns null. */
  public static BigDecimal toBigDecimal(String string) {
    if (string == null) {
      return null;
    }
    if (BIG_DECIMAL_PATTERN.matcher(string).matches()) {
      return new BigDecimal(string);
    }
    return null;
  }

  /** Gets value of a ValueOnlyXmlAttribute if it exists. */
  public static String valueOfValueOnlyXmlAttribute(ValueOnlyXmlAttribute valueOnlyXmlAttribute) {
    if (valueOnlyXmlAttribute == null) {
      return null;
    } else {
      return valueOnlyXmlAttribute.value();
    }
  }
}
