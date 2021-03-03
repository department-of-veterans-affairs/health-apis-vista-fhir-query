package gov.va.api.health.vistafhirquery.tests.r4;

import static gov.va.api.health.sentinel.EnvironmentAssumptions.assumeEnvironmentNotIn;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import gov.va.api.health.sentinel.Environment;
import gov.va.api.health.sentinel.ServiceDefinition;
import gov.va.api.health.vistafhirquery.tests.SystemDefinition;
import gov.va.api.health.vistafhirquery.tests.SystemDefinitions;
import gov.va.api.health.vistafhirquery.tests.TestClients;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@Slf4j
public class ObservationIT {
  /* TODO: Resource Validation/Verification. */
  SystemDefinition def = SystemDefinitions.systemDefinition();

  ServiceDefinition r4 = def.r4();

  static Stream<Arguments> search() {
    var testIds = SystemDefinitions.systemDefinition().publicIds();
    return Stream.of(
        arguments("?patient=" + testIds.patient()),
        arguments("?patient=" + testIds.patient() + "&code=8310-5"));
  }

  @Test
  void read() {
    assumeEnvironmentNotIn(Environment.STAGING);
    var readId = def.publicIds().observation();
    var apiPath = r4.apiPath();
    log.info("Verify {}Observation/{} has status (200)", apiPath, readId);
    TestClients.r4().get(apiPath + "Observation/{observation}", readId).expect(200);
  }

  @ParameterizedTest
  @MethodSource
  void search(String query) {
    assumeEnvironmentNotIn(Environment.STAGING);
    var apiPath = r4.apiPath();
    var request = apiPath + "Observation" + query;
    log.info("Verify {} has status (200)", request);
    TestClients.r4().get(request).expect(200);
  }
}
