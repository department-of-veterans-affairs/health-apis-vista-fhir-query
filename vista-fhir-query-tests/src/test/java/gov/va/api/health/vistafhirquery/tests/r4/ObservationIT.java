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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@Slf4j
public class ObservationIT {
  SystemDefinition def = SystemDefinitions.systemDefinition();

  ServiceDefinition r4 = def.r4();

  static Stream<Arguments> read() {
    var testIds = SystemDefinitions.systemDefinition().publicIds();
    return Stream.of(
        arguments(testIds.observationVitalSign(), 200),
        arguments(testIds.observationLaboratory(), 200),
        arguments("I2-404", 404));
  }

  static Stream<Arguments> search() {
    var testIds = SystemDefinitions.systemDefinition().publicIds();
    return Stream.of(
        arguments("?patient=" + testIds.patient(), 200),
        arguments("?patient=" + testIds.patient() + "&date=ge2010&date=lt2012", 200),
        arguments("?patient=" + testIds.patient() + "&date=ge2012&date=lt2010", 400),
        arguments("?patient=" + testIds.patient() + "&code=8310-5", 200),
        arguments("?patient=" + testIds.patient() + "&category=laboratory", 200),
        arguments(
            "?patient="
                + testIds.patient()
                + "&category=vital-signs"
                + "&code=8310-5"
                + "&date=ge2010"
                + "&date=lt2012",
            200));
  }

  @ParameterizedTest
  @MethodSource
  void read(String id, int expectedStatus) {
    assumeEnvironmentNotIn(Environment.STAGING);
    var apiPath = r4.apiPath();
    log.info("Verify {}Observation/{} has status ({})", apiPath, id, expectedStatus);
    TestClients.r4().get(apiPath + "Observation/{observation}", id).expect(expectedStatus);
  }

  @ParameterizedTest
  @MethodSource
  void search(String query, int expectedStatus) {
    assumeEnvironmentNotIn(Environment.STAGING);
    var apiPath = r4.apiPath();
    var request = apiPath + "Observation" + query;
    log.info("Verify {} has status (200)", request);
    TestClients.r4().get(request).expect(expectedStatus);
  }
}
