package gov.va.api.health.vistafhirquery.tests.r4;

import gov.va.api.health.r4.api.information.WellKnown;
import gov.va.api.health.r4.api.resources.CapabilityStatement;
import gov.va.api.health.vistafhirquery.tests.TestClients;
import org.junit.jupiter.api.Test;

public class ConformanceIT {

  @Test
  void metadata() {
    TestClients.internal()
        .get("/r4/.well-known/smart-configuration")
        .expect(200)
        .expectValid(WellKnown.class);
  }

  @Test
  void smartConfiguration() {
    TestClients.internal().get("/r4/metadata").expect(200).expectValid(CapabilityStatement.class);
  }
}
