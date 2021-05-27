package gov.va.api.health.vistafhirquery.service.config;

import gov.va.api.health.r4.api.resources.Resource;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Properties for defining working links in responses. */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("vista-fhir-query")
@Data
@Accessors(fluent = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class LinkProperties {

  private String publicUrl;
  private String publicR4BasePath;

  /**
   * Mapping for resource name and URL without resource name, e.g. Patient =
   * https://api.va.gov/services/fhir/v0/r4.
   */
  private Map<String, String> customR4UrlAndPath;

  private int defaultPageSize;
  private int maxPageSize;

  public Map<String, String> getCustomR4UrlAndPath() {
    return (customR4UrlAndPath == null) ? Map.of() : customR4UrlAndPath;
  }

  @PostConstruct
  void logConfiguration() {
    log.info("default page size: {}, max page size: {}", getDefaultPageSize(), getMaxPageSize());
    log.info("default R4 links: {}/{}", getPublicUrl(), getPublicR4BasePath());
    getCustomR4UrlAndPath().forEach((r, u) -> log.info("{} links: {}", r, u));
  }

  public Links r4() {
    return new Links(publicUrl, publicR4BasePath, getCustomR4UrlAndPath());
  }

  /** Links. */
  @Accessors(fluent = true)
  public static class Links {

    @Getter private final String baseUrl;
    private final Map<String, String> urlForResource;

    Links(String publicUrl, String publicBasePath, @NonNull Map<String, String> publicR4Link) {
      baseUrl = publicUrl + "/" + publicBasePath;
      urlForResource = publicR4Link;
    }

    public String readUrl(Resource resource) {
      return readUrl(resource.getClass().getSimpleName(), resource.id());
    }

    public String readUrl(String resource, String id) {
      return resourceUrl(resource) + "/" + id;
    }

    public String resourceUrl(String resource) {
      return urlForResource.getOrDefault(resource, baseUrl()) + "/" + resource;
    }
  }
}
