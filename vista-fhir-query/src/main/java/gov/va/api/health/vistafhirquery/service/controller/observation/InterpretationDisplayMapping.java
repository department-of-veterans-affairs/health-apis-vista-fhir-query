package gov.va.api.health.vistafhirquery.service.controller.observation;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.StringUtils.upperCase;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/** InterpretationDisplayMapping. */
@Slf4j
public class InterpretationDisplayMapping {
  private static final Map<String, String> MAPPINGS = populateMappings();

  /**
   * Get the display value for a given interpretation code or null if the mapping does not exist.
   */
  public static String forCode(String code) {
    var maybeDisplay = MAPPINGS.get(upperCase(trimToEmpty(code), Locale.US));
    if (maybeDisplay == null) {
      log.error("No display value for interpretation code '{}'.", code);
    }
    return maybeDisplay;
  }

  private static Map<String, String> populateMappings() {
    var mappings = new HashMap<String, String>();
    mappings.put("CAR", "Carrier");
    mappings.put("CARRIER", "Carrier");
    mappings.put("<", "Off scale low");
    mappings.put(">", "Off scale high");
    mappings.put("A", "Abnormal");
    mappings.put("AA", "Critical abnormal");
    mappings.put("AC", "Anti-complementary substances present");
    mappings.put("B", "Better");
    mappings.put("D", "Significant change down");
    mappings.put("DET", "Detected");
    mappings.put("E", "Equivocal");
    mappings.put("EX", "outside threshold");
    mappings.put("EXP", "Expected");
    mappings.put("H", "High");
    mappings.put("H*", "Critical high");
    mappings.put("HH", "Critical high");
    mappings.put("HU", "Significantly high");
    mappings.put("H>", "Significantly high");
    mappings.put("HM", "Hold for Medical Review");
    mappings.put("HX", "above high threshold");
    mappings.put("I", "Intermediate");
    mappings.put("IE", "Insufficient evidence");
    mappings.put("IND", "Indeterminate");
    mappings.put("L", "Low");
    mappings.put("L*", "Critical low");
    mappings.put("LL", "Critical low");
    mappings.put("LU", "Significantly low");
    mappings.put("L<", "Significantly low");
    mappings.put("LX", "below low threshold");
    mappings.put("MS", "moderately susceptible");
    mappings.put("N", "Normal");
    mappings.put("NCL", "No CLSI defined breakpoint");
    mappings.put("ND", "Not detected");
    mappings.put("NEG", "Negative");
    mappings.put("NR", "Non-reactive");
    mappings.put("NS", "Non-susceptible");
    mappings.put("OBX", "Interpretation qualifiers in separate OBX segments");
    mappings.put("POS", "Positive");
    mappings.put("QCF", "Quality control failure");
    mappings.put("R", "Resistant");
    mappings.put("RR", "Reactive");
    mappings.put("S", "Susceptible");
    mappings.put("SDD", "Susceptible-dose dependent");
    mappings.put("SYN-R", "Synergy - resistant");
    mappings.put("SYN-S", "Synergy - susceptible");
    mappings.put("TOX", "Cytotoxic substance present");
    mappings.put("U", "Significant change up");
    mappings.put("UNE", "Unexpected");
    mappings.put("VS", "very susceptible");
    mappings.put("W", "Worse");
    mappings.put("WR", "Weakly reactive");
    // Mappings should be unmodifiable
    return Map.copyOf(mappings);
  }
}
