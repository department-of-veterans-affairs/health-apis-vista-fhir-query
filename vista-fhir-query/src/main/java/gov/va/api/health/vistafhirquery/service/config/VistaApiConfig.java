package gov.va.api.health.vistafhirquery.service.config;

import static org.apache.commons.lang3.StringUtils.trimToNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.charon.api.RpcPrincipal;
import java.io.File;
import java.util.Collections;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Loads configuration of the VistaAPI. */
@Data
@Builder
@Configuration
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = false)
@EnableConfigurationProperties
@ConfigurationProperties("vista.api")
public class VistaApiConfig {
  private String url;

  private String clientKey;

  private String accessCode;

  private String verifyCode;

  private String applicationProxyUser;

  private String applicationProxyUserContext;

  private String principalFile;

  private Map<String, RpcPrincipal> siteSpecificPrincipals;

  /** Build the default RpcPrincipal. */
  public RpcPrincipal defaultPrincipal() {
    return RpcPrincipal.builder()
        .accessCode(getAccessCode())
        .verifyCode(getVerifyCode())
        .applicationProxyUser(getApplicationProxyUser())
        .build();
  }

  public String getApplicationProxyUserContext() {
    return trimToNull(applicationProxyUserContext);
  }

  /**
   * Get the RpcPrincipal for the given site. If it doesn't exist, return the default RpcPrincipal.
   */
  public RpcPrincipal getSiteSpecificPrincipalOrDefault(String site) {
    return getSiteSpecificPrincipals().getOrDefault(site, defaultPrincipal());
  }

  /** Lazy initializer. */
  private Map<String, RpcPrincipal> getSiteSpecificPrincipals() {
    if (siteSpecificPrincipals == null) {
      siteSpecificPrincipals = Collections.emptyMap();
    }
    return siteSpecificPrincipals;
  }

  @PostConstruct
  @SneakyThrows
  void loadSiteSpecificInfoFromFile() {
    ObjectMapper mapper = new ObjectMapper();
    PrincipalRooney rooney = mapper.readValue(new File(getPrincipalFile()), PrincipalRooney.class);
    /*
    Take principal from the new "principal" field, otherwise fall back on the
    old access, verify, and apu fields.
    */
    if (rooney.getPrincipal() != null) {
      setAccessCode(rooney.getPrincipal().accessCode());
      setVerifyCode(rooney.getPrincipal().verifyCode());
      setApplicationProxyUser(rooney.getPrincipal().applicationProxyUser());
    } else {
      setAccessCode(rooney.getAccessCode());
      setVerifyCode(rooney.getVerifyCode());
      setApplicationProxyUser(rooney.getApplicationProxyUser());
    }
    setSiteSpecificPrincipals(rooney.getSiteSpecificPrincipals());
  }

  public Map<String, RpcPrincipal> siteSpecificPrincipals() {
    return getSiteSpecificPrincipals();
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  static class PrincipalRooney {
    String accessCode;

    String verifyCode;

    String applicationProxyUser;

    RpcPrincipal principal;

    Map<String, RpcPrincipal> siteSpecificPrincipals;
  }
}
