package gov.va.api.health.vistafhirquery.service.controller.observation;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class ObservationConditions {
  /** [Vuid, Loinc] Mappings for testing allowed codes. */
  Map<String, String> codes;

  /** Lazy Intitializer. */
  public Map<String, String> codes() {
    if (codes == null) {
      codes = Map.of();
    }
    return codes;
  }

  /** Test a LOINC and check it matches one of the accepted codes. */
  public boolean hasAcceptedLoincCode(String loinc) {
    if (codes().isEmpty()) {
      return true;
    }
    if (loinc == null) {
      return false;
    }
    return codes().containsValue(loinc);
  }

  /** Test a Vista VUID and check it matches one of the accepted codes. */
  public boolean hasAcceptedVuidCode(String vuid) {
    if (codes().isEmpty()) {
      return true;
    }
    if (vuid == null) {
      return false;
    }
    return codes().containsKey(vuid);
  }
}
