package gov.va.api.health.vistafhirquery.service.controller.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** MetadataProperties. */
@Data
@Builder
@Configuration
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = false)
@EnableConfigurationProperties
@ConfigurationProperties("metadata")
public class MetadataProperties {
  private String version;
  private VersionSpecificProperties r4;
  private String publisher;
  private StatementType statementType;
  private ContactProperties contact;
  private String publicationDate;
  private String description;
  private String softwareName;
  private boolean productionUse;
  private SecurityProperties security;

  enum StatementType {
    CLINICIAN,
    PATIENT
  }

  /** ContactProperties . */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Accessors(fluent = false)
  public static class ContactProperties {
    private String name;
    private String email;
  }

  /** SecurityProperties . */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Accessors(fluent = false)
  public static class SecurityProperties {
    private String tokenEndpoint;
    private String authorizeEndpoint;
    private String managementEndpoint;
    private String revocationEndpoint;
    private String description;
  }

  /** VersionSpecificProperties . */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Accessors(fluent = false)
  public static class VersionSpecificProperties {
    private String id;
    private String name;
    private String resourceDocumentation;
  }
}
