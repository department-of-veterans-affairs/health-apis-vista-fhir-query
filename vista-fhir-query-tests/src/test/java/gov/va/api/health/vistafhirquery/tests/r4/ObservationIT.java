package gov.va.api.health.vistafhirquery.tests.r4;

import static gov.va.api.health.sentinel.EnvironmentAssumptions.assumeEnvironmentNotIn;

import gov.va.api.health.sentinel.Environment;
import gov.va.api.health.sentinel.ServiceDefinition;
import gov.va.api.health.vistafhirquery.tests.SystemDefinition;
import gov.va.api.health.vistafhirquery.tests.SystemDefinitions;
import gov.va.api.health.vistafhirquery.tests.TestClients;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class ObservationIT {

  /* TODO: Resource Validation/Verification. */

  SystemDefinition def = SystemDefinitions.systemDefinition();

  ServiceDefinition r4 = def.r4();

  @Test
  void read() {
    assumeEnvironmentNotIn(Environment.STAGING);
    var readId = def.publicIds().observation();
    var apiPath = r4.apiPath();
    log.info("Verify {}Observation/{} has status (200)", apiPath, readId);
    TestClients.r4().get(apiPath + "Observation/{observation}", readId).expect(200);
  }

  @Test
  void search() {
    assumeEnvironmentNotIn(Environment.STAGING);
    var patientId = def.publicIds().patient();
    var apiPath = r4.apiPath();
    log.info("Verify {}Observation?patient={} has status (200)", apiPath, patientId);
    TestClients.r4().get(apiPath + "Observation?patient={patient}", patientId).expect(200);
  }
}
