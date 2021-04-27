package gov.va.api.health.vistafhirquery.service.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
      "metadata.statement-type=patient",
      "spring.datasource.driver-class-name=org.h2.Driver",
      "spring.datasource.url=jdbc:h2:mem:db;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA app",
      "spring.datasource.username=sa",
      "spring.datasource.password=sa",
      "spring.datasource.initialization-mode=always",
      "spring.jpa.hibernate.ddl-auto=none",
      "vista-fhir-query.public-web-exception-key=set",
      "vista.api.principal-file=src/test/resources/principal-and-site-specific-principals.json"
    })
public class PathRewriteConfigTest {
  @Autowired TestRestTemplate restTemplate;

  @LocalServerPort private int port;

  @Test
  void pathIsRewritten() {
    assertThat(
        restTemplate.getForObject(
            "http://localhost:" + port + "/vista-fhir-query/actuator/health", String.class));
  }
}
