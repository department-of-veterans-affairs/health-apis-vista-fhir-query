package gov.va.api.health.vistafhirquery.service.config;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Properties for managing alternate ids. */
@SuppressWarnings("DefaultAnnotationParam")
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("alternate-patient-ids")
@Data
@Accessors(fluent = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlternatePatientIdProperties {

  /** If enabled, request will be intercepted to swap configured IDs. */
  private boolean enabled;

  /** Public ID (key) to private Id (value). */
  private Map<String, String> id;

  /**
   * Supported parameters that will be inspected for alternate patient IDs, e.g. patient, _id. Only
   * these parameters will be considered eligible for patient ID swapping.
   */
  private List<String> parameters;
}
