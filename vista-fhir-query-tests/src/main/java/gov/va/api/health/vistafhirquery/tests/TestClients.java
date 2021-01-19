package gov.va.api.health.vistafhirquery.tests;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.sentinel.BasicTestClient;
import gov.va.api.health.sentinel.TestClient;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class TestClients {

  /** Can be used when testing things that don't map to fhir objects e.g. Ping. */
  public static TestClient internal() {
    return BasicTestClient.builder()
        .contentType("application/json")
        .service(SystemDefinitions.systemDefinition().internal())
        .mapper(JacksonConfig::createMapper)
        .build();
  }
}
