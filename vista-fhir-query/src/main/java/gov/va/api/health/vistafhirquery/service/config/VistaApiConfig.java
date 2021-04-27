package gov.va.api.health.vistafhirquery.service.config;

import static org.apache.commons.lang3.StringUtils.trimToNull;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.charon.api.RpcPrincipal;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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

  private String siteSpecificPrincipalFile;

  private Map<String, SiteSpecificDetails> siteSpecificDetails;

  public String getApplicationProxyUserContext() {
    return trimToNull(applicationProxyUserContext);
  }

  /** The value "default" is validated during @PostConstruct, and will be present. */
  public RpcPrincipal getPrincipalOrDefault(String site) {
    return getSiteSpecificDetails()
        .getOrDefault(site, getSiteSpecificDetails().get("default"))
        .getRpcPrincipal();
  }

  @PostConstruct
  void loadSiteSpecificInfoFromFile() {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root;
    try (FileInputStream fs = new FileInputStream(siteSpecificPrincipalFile)) {
      root = mapper.readTree(fs);
    } catch (FileNotFoundException e) {
      throw new IllegalArgumentException(
          "Failed to find principals file: " + siteSpecificPrincipalFile);
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to parse json file: " + siteSpecificPrincipalFile);
    }
    if (root.isEmpty()) {
      throw new IllegalArgumentException("Principal file is empty: " + siteSpecificPrincipalFile);
    }
    Map<String, SiteSpecificDetails> siteToDetailsMap = new HashMap<>();
    JsonNode siteSpecificPrincipalsNode = root.get("siteSpecificPrincipals");
    if (!siteSpecificPrincipalsNode.isNull()) {
      siteToDetailsMap = mapper.convertValue(siteSpecificPrincipalsNode, new TypeReference<>() {});
    }
    JsonNode defaultPrincipalNode = root.get("principal");
    if (defaultPrincipalNode.isNull()) {
      throw new IllegalArgumentException(
          "Default rpc principal is not defined in: " + siteSpecificPrincipalFile);
    }
    RpcPrincipal defaultPrincipal = mapper.convertValue(root.get("principal"), RpcPrincipal.class);
    siteToDetailsMap.put(
        "default", SiteSpecificDetails.builder().rpcPrincipal(defaultPrincipal).build());
    siteSpecificDetails = Map.copyOf(siteToDetailsMap);
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  static class SiteSpecificDetails {
    @JsonUnwrapped RpcPrincipal rpcPrincipal;
  }
}
