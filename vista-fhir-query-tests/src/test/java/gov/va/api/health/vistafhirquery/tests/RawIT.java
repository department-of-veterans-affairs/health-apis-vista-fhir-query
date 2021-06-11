package gov.va.api.health.vistafhirquery.tests;

import static gov.va.api.health.sentinel.EnvironmentAssumptions.assumeEnvironmentIn;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.sentinel.Environment;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Slf4j
public class RawIT {

  @ParameterizedTest
  @ValueSource(strings = {"/internal/raw/Organization"})
  void clientKeyIsMissing(String path) {
    assumeEnvironmentIn(Environment.LOCAL);
    var response =
        RestAssured.given()
            .baseUri("http://localhost")
            .port(8095)
            .relaxedHTTPSValidation()
            .headers(Map.of("client-key", "nope"))
            .contentType("application/json")
            .accept("application/json")
            .request(Method.GET, path);
    assertThat(response.getStatusCode()).isEqualTo(401);
  }

  @ParameterizedTest
  @ValueSource(strings = {"/internal/raw/Organization"})
  void goodRequest(String path) {
    assumeEnvironmentIn(Environment.LOCAL);
    var response =
        RestAssured.given()
            .baseUri("http://localhost")
            .port(8095)
            .relaxedHTTPSValidation()
            .headers(Map.of("client-key", "~shanktopus~"))
            .contentType("application/json")
            .accept("application/json")
            .request(Method.GET, path + "?icn=9999998&site=673");
    assertThat(response.getStatusCode()).isEqualTo(200);
  }
}
