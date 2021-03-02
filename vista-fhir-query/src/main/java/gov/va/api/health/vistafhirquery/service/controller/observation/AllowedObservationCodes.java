package gov.va.api.health.vistafhirquery.service.controller.observation;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

public interface AllowedObservationCodes {

  static AllowedObservationCodes allowAll() {
    return new AllowAllObservationCodes();
  }

  static AllowedObservationCodes allowOnly(Map<String, String> vuidToLoinc) {
    return AllowOnlyTheseObservationCodes.of(vuidToLoinc);
  }

  /** Test a LOINC and check it matches one of the allowed codes. */
  boolean isAllowedLoincCode(String loinc);

  /** Test a Vista VUID and check it matches one of the allowed codes. */
  boolean isAllowedVuidCode(String vuid);

  class AllowAllObservationCodes implements AllowedObservationCodes {
    @Override
    public boolean isAllowedLoincCode(String loinc) {
      return loinc != null;
    }

    @Override
    public boolean isAllowedVuidCode(String vuid) {
      return vuid != null;
    }
  }

  @Value
  @RequiredArgsConstructor
  class AllowOnlyTheseObservationCodes implements AllowedObservationCodes {
    /** [Vuid, Loinc] Mappings for testing allowed codes. */
    @NonNull BiMap<String, String> codes;

    public static AllowOnlyTheseObservationCodes of(Map<String, String> codes) {
      return new AllowOnlyTheseObservationCodes(HashBiMap.create(codes));
    }

    /** Test a LOINC and check it matches one of the accepted codes. */
    @Override
    public boolean isAllowedLoincCode(String loinc) {
      return loinc != null && codes().inverse().containsKey(loinc);
    }

    /** Test a Vista VUID and check it matches one of the accepted codes. */
    @Override
    public boolean isAllowedVuidCode(String vuid) {
      return vuid != null && codes().containsKey(vuid);
    }
  }
}
