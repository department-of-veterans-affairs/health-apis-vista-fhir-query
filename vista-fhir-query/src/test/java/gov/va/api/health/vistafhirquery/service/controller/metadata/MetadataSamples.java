package gov.va.api.health.vistafhirquery.service.controller.metadata;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.r4.api.resources.CapabilityStatement;
import gov.va.api.health.vistafhirquery.service.config.LinkProperties;
import java.util.Properties;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.info.BuildProperties;

public class MetadataSamples {
  public static BuildProperties buildProperties() {
    var properties = new Properties();
    properties.setProperty("group", "gov.va.api.health");
    properties.setProperty("artifact", "vista-fhir-query");
    properties.setProperty("version", "1.2.3");
    properties.setProperty("time", "2005-01-21T07:57:00Z");
    return new BuildProperties(properties);
  }

  @SneakyThrows
  public static String json(Object o) {
    return JacksonConfig.createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(o);
  }

  public static LinkProperties linkProperties() {
    return LinkProperties.builder()
        .publicUrl("http://fake.com")
        .publicR4BasePath("r4")
        .defaultPageSize(10)
        .maxPageSize(100)
        .build();
  }

  @NoArgsConstructor(staticName = "create")
  public static class R4 {
    @SneakyThrows
    public CapabilityStatement capabilityStatement() {
      return JacksonConfig.createMapper()
          .readValue(
              getClass().getResourceAsStream("/r4-metadata.json"), CapabilityStatement.class);
    }

    public MetadataProperties conformanceProperties() {
      return MetadataProperties.builder()
          .version("1")
          .r4(
              MetadataProperties.VersionSpecificProperties.builder()
                  .id("fhir-r4-conformance")
                  .name("Fhir R4")
                  .resourceDocumentation("Implemented per specification.")
                  .build())
          .publisher("Unit Test")
          .statementType(MetadataProperties.StatementType.CLINICIAN)
          .contact(
              MetadataProperties.ContactProperties.builder()
                  .name("David")
                  .email("david@ew.com")
                  .build())
          .publicationDate("2018-09-27T19:30:00-05:00")
          .description("Conformance Description.")
          .softwareName("Junit")
          .productionUse(true)
          .security(
              MetadataProperties.SecurityProperties.builder()
                  .tokenEndpoint("http://fake.com/token")
                  .authorizeEndpoint("http://fake.com/authorize")
                  .managementEndpoint("http://fake.com/manage")
                  .revocationEndpoint("http://fake.com/revoke")
                  .description("Security Description.")
                  .build())
          .build();
    }
  }
}
