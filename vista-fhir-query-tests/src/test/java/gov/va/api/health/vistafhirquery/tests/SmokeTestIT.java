package gov.va.api.health.vistafhirquery.tests;

import static gov.va.api.health.sentinel.EnvironmentAssumptions.assumeEnvironmentIn;
import static gov.va.api.health.sentinel.EnvironmentAssumptions.assumeEnvironmentNotIn;
import static org.hamcrest.CoreMatchers.equalTo;

import gov.va.api.health.fhir.testsupport.ResourceVerifier;
import gov.va.api.health.r4.api.resources.Observation;
import gov.va.api.health.sentinel.Environment;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class SmokeTestIT {
  private final TestIds testIds = VistaFhirQueryResourceVerifier.ids();

  @Delegate private final ResourceVerifier verifier = VistaFhirQueryResourceVerifier.r4();

  @Test
  void healthCheckIsUnprotected() {
    // At least one test must run or Junit is gonna be mad
    assumeEnvironmentIn(Environment.STAGING);
    var requestPath = SystemDefinitions.systemDefinition().internal().apiPath() + "actuator/health";
    log.info("Running health-check for path: {}", requestPath);
    TestClients.internal().get(requestPath).response().then().body("status", equalTo("UP"));
  }

  @Test
  void read() {
    assumeEnvironmentNotIn(Environment.STAGING);
    verify(test(200, Observation.Bundle.class, "Observation?patient={patient}", testIds.patient()));
  }
}
