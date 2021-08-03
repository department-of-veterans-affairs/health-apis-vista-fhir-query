package gov.va.api.health.vistafhirquery.tests;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.NonNull;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

/** TestIds. */
@Value
@Builder(toBuilder = true)
public final class TestIds {
  @NonNull String coverage;

  @NonNull String organization;

  @NonNull String observationVitalSign;

  @NonNull String observationLaboratory;

  @NonNull String patient;

  @Default String unknown = "5555555555555";

  @Default @NonNull List<IcnAtSites> patientSites = new ArrayList<>();

  /** Represents an ICN at potentially multiple vista sites. */
  @Value
  @Builder
  public static class IcnAtSites {
    @NonNull String icn;

    @Default @NonNull List<String> vistas = new ArrayList<>();

    private static IllegalArgumentException badFormat(String badIcnAtSite) {
      return new IllegalArgumentException(
          "Expected icn@site[+site][...], e.g. 123456789V123456@673+605, got " + badIcnAtSite);
    }

    /**
     * Parse a CSV of ICN @ Site (see `of` for more details).
     *
     * <p>Example: 999@123,888@456+789,777@789
     */
    public static List<IcnAtSites> csvOf(String icnAtSiteCsv) {
      if (isBlank(icnAtSiteCsv)) {
        return List.of();
      }
      return Arrays.stream(icnAtSiteCsv.split(",+", -1))
          .filter(StringUtils::isNotBlank)
          .map(IcnAtSites::of)
          .collect(toList());
    }

    /**
     * Parse the ICN @ Site string into an object representation. Format is:
     * ${icn}@${site}[+${site}][...]
     *
     * <p>Examples: 999@123, 999@123+456
     *
     * <p>Use `+` to specify multiple sites. Leading, trailing, and repeating `+` are ignored, e.g.
     * 999@++123++456++ is equal to 999@123+456 or ICN 999 at sites 123 and 456
     */
    public static IcnAtSites of(String icnAtSite) {
      if (isBlank(icnAtSite)) {
        throw badFormat(icnAtSite);
      }
      int at = icnAtSite.indexOf('@');
      if (at < 1 || at > icnAtSite.length() - 2) {
        throw badFormat(icnAtSite);
      }
      String icn = icnAtSite.substring(0, at);
      String sites = icnAtSite.substring(at + 1);
      List<String> vistas =
          List.of(sites.replaceFirst("^\\++", "").replaceFirst("\\++$", "").split("\\++", -1));
      if (vistas.isEmpty()) {
        throw badFormat(icnAtSite);
      }
      return IcnAtSites.builder().icn(icn).vistas(vistas).build();
    }
  }
}
