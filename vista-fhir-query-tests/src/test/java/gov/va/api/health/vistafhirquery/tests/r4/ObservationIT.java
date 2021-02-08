package gov.va.api.health.vistafhirquery.tests.r4;

import static gov.va.api.health.sentinel.EnvironmentAssumptions.assumeEnvironmentNotIn;

import gov.va.api.health.sentinel.Environment;
import gov.va.api.health.vistafhirquery.tests.SystemDefinitions;
import gov.va.api.health.vistafhirquery.tests.TestClients;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class ObservationIT {

  Map<String, String> headers() {
    log.info("ToDo: Use static token method.");
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");
    SystemDefinitions.systemDefinition()
        .clientKey()
        .ifPresent(key -> headers.put("client-key", key));
    return headers;
  }

  @Test
  void read() {
    assumeEnvironmentNotIn(Environment.STAGING);
    var readId = SystemDefinitions.systemDefinition().publicIds().observation();
    var apiPath = SystemDefinitions.systemDefinition().internal().apiPath();
    log.info("Verify {}r4/Observation/{} has status (200)", apiPath, readId);
    TestClients.internal()
        .get(headers(), apiPath + "r4/Observation/{observation}", readId)
        .expect(200);
  }
}
