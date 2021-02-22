package gov.va.api.health.vistafhirquery.service.controller.observation;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
public class ObservationConditions {
  List<String> codes;

  /** Lazy Intitializer. */
  public List<String> codes() {
    if (codes == null) {
      codes = List.of();
    }
    return codes;
  }

  /** Test a Vista Measurements VUID and check it matches one of the accepted codes. */
  public boolean hasAcceptedCode(String vuid) {
    if (codes().isEmpty()) {
      return true;
    }
    if (vuid == null) {
      return false;
    }
    return codes().contains(vuid);
  }
}
