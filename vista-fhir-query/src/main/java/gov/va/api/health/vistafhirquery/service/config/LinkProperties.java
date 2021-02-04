package gov.va.api.health.vistafhirquery.service.config;

import gov.va.api.health.r4.api.resources.Resource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("vista-fhir-query")
@Data
@Accessors(fluent = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LinkProperties {

  private String publicUrl;
  private String publicR4BasePath;
  private int defaultPageSize;
  private int maxPageSize;

  public Links<Resource> r4() {
    return new Links<Resource>(publicUrl, publicR4BasePath);
  }

  @Accessors(fluent = true)
  public static class Links<ResourceT> {

    @Getter private final String baseUrl;

    Links(String publicUrl, String publicBasePath) {
      baseUrl = publicUrl + "/" + publicBasePath;
    }

    public String readUrl(Resource resource) {
      return readUrl(resource.getClass().getSimpleName(), resource.id());
    }

    public String readUrl(String resource, String id) {
      return resourceUrl(resource) + "/" + id;
    }

    public String resourceUrl(String resource) {
      return baseUrl() + "/" + resource;
    }
  }
}
