package gov.va.api.health.vistafhirquery.service.controller.wellknown;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.r4.api.information.WellKnown;
import gov.va.api.health.vistafhirquery.service.controller.metadata.MetadataProperties;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class WellKnownControllerTest {
  private MetadataProperties conformanceProperties() {
    return MetadataProperties.builder()
        .security(
            MetadataProperties.SecurityProperties.builder()
                .authorizeEndpoint("http://fake.com/authorize")
                .tokenEndpoint("http://fake.com/token")
                .managementEndpoint("http://fake.com/manage")
                .revocationEndpoint("http://fake.com/revoke")
                .build())
        .build();
  }

  @SneakyThrows
  private String json(Object o) {
    return JacksonConfig.createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(o);
  }

  @Test
  void r4() {
    var sample = new WellKnownController(wellKnownProperties(), conformanceProperties()).r4();
    var actual =
        WellKnown.builder()
            .tokenEndpoint("http://fake.com/token")
            .authorizationEndpoint("http://fake.com/authorize")
            .managementEndpoint("http://fake.com/manage")
            .revocationEndpoint("http://fake.com/revoke")
            .capabilities(
                asList(
                    "context-standalone-patient",
                    "launch-standalone",
                    "permission-offline",
                    "permission-patient"))
            .responseTypeSupported(asList("code", "refresh-token"))
            .scopesSupported(asList("patient/Fake.read", "offline_access"))
            .build();
    assertThat(json(sample)).isEqualTo(json(actual));
  }

  private WellKnownProperties wellKnownProperties() {
    return WellKnownProperties.builder()
        .capabilities(
            asList(
                "context-standalone-patient",
                "launch-standalone",
                "permission-offline",
                "permission-patient"))
        .responseTypeSupported(asList("code", "refresh-token"))
        .scopesSupported(asList("patient/Fake.read", "offline_access"))
        .build();
  }
}
