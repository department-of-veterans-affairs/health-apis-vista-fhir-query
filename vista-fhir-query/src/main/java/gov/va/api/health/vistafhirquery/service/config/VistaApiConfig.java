package gov.va.api.health.vistafhirquery.service.config;

import static org.apache.commons.lang3.StringUtils.trimToNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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

  private String vprGetPatientDataContext;

  public String getVprGetPatientDataContext() {
    return trimToNull(vprGetPatientDataContext);
  }
}
