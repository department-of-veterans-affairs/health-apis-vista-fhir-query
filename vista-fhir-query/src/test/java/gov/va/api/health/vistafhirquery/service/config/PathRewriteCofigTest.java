package gov.va.api.health.vistafhirquery.service.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PathRewriteCofigTest {
  @Autowired TestRestTemplate restTemplate;

  @LocalServerPort private int port;

  @Test
  void pathIsRewritten() {
    assertThat(
        restTemplate.getForObject(
            "http://localhost:" + port + "/vista-fhir-query/actuator/health", String.class));
  }
}
