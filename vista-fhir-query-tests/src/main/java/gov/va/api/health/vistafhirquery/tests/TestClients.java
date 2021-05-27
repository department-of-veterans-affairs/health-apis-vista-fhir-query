package gov.va.api.health.vistafhirquery.tests;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.sentinel.BasicTestClient;
import gov.va.api.health.sentinel.FhirTestClient;
import gov.va.api.health.sentinel.TestClient;
import java.util.List;
import lombok.experimental.UtilityClass;

/** Utility class that defines test clients that can be used across all tests. */
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

  /** R4 Vista Fhir Query TestClient. */
  public static TestClient r4() {
    return FhirTestClient.builder()
        .service(SystemDefinitions.systemDefinition().r4())
        .mapper(JacksonConfig::createMapper)
        .contentTypes(List.of("application/json", "application/fhir+json"))
        .errorResponseEqualityCheck(
            new gov.va.api.health.vistafhirquery.tests.r4.OperationOutcomesAreFunctionallyEqual())
        .build();
  }
}
