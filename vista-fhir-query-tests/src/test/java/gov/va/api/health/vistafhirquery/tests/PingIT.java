package gov.va.api.health.vistafhirquery.tests;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class PingIT {

  Map<String, String> headers() {
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");
    SystemDefinitions.systemDefinition()
        .clientKey()
        .ifPresent(key -> headers.put("client-key", key));
    return headers;
  }

  @Test
  void ping() {
    var patientIcn = SystemDefinitions.systemDefinition().publicIds().patient();
    var apiPath = SystemDefinitions.systemDefinition().internal().apiPath();
    log.info("Verify {}internal/ping/{} has status (200)", apiPath, patientIcn);
    TestClients.internal().get(headers(), apiPath + "internal/ping/{icn}", patientIcn).expect(200);
  }
}
