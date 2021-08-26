package gov.va.api.health.vistafhirquery.tests.r4;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.r4.api.resources.Endpoint;
import gov.va.api.health.vistafhirquery.tests.SystemDefinitions;
import gov.va.api.health.vistafhirquery.tests.TestClients;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Slf4j
public class EndpointIT {

  private final String urlWithPath = SystemDefinitions.systemDefinition().r4().urlWithApiPath();

  @ParameterizedTest
  @ValueSource(strings = {"Endpoint", "Endpoint?status=active"})
  void search(String query) {
    log.info("Verify /r4/{} is Bundle (200)", query);
    var bundle =
        TestClients.r4().get(urlWithPath + query).expect(200).expectValid(Endpoint.Bundle.class);
    assertThat(bundle.total()).isGreaterThan(0);
  }

  @Test
  void searchWithBadStatus() {
    log.info("Verify /r4/Endpoint?status=INVALID is Bundle (200)");
    var bundle =
        TestClients.r4()
            .get(urlWithPath + "Endpoint?status=INVALID")
            .expect(200)
            .expectValid(Endpoint.Bundle.class);
    assertThat(bundle.total()).isEqualTo(0);
  }
}
