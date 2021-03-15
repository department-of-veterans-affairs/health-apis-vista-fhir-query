package gov.va.api.health.vistafhirquery.service.controller.observation;

import java.util.Collection;
import java.util.Set;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

public interface AllowedObservationCodes {

  static AllowedObservationCodes allowAll() {
    return new AllowAllObservationCodes();
  }

  static AllowedObservationCodes allowOnly(
      Collection<String> allowedVuids, Collection<String> allowedLoincs) {
    return AllowOnlyTheseObservationCodes.of(allowedVuids, allowedLoincs);
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
    @NonNull Set<String> allowedVuids;

    @NonNull Set<String> allowedLoincs;

    public static AllowOnlyTheseObservationCodes of(
        Collection<String> allowedVuids, Collection<String> allowedLoincs) {
      return new AllowOnlyTheseObservationCodes(
          Set.copyOf(allowedVuids), Set.copyOf(allowedLoincs));
    }

    /** Test a LOINC and check it matches one of the accepted codes. */
    @Override
    public boolean isAllowedLoincCode(String loinc) {
      return loinc != null && allowedLoincs().contains(loinc);
    }

    /** Test a Vista VUID and check it matches one of the accepted codes. */
    @Override
    public boolean isAllowedVuidCode(String vuid) {
      return vuid != null && allowedVuids().contains(vuid);
    }
  }
}
