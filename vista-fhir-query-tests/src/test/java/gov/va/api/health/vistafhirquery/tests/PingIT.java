package gov.va.api.health.vistafhirquery.tests;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class PingIT {

  @Test
  void ping() {
    var patientIcn = SystemDefinitions.systemDefinition().publicIds().patient();
    log.info(
        "Verify /ping/{} has status (200)",
        SystemDefinitions.systemDefinition().publicIds().patient());
    TestClients.internal().get("/ping/{icn}", patientIcn).expect(200);
  }
}
