package gov.va.api.health.vistafhirquery.tests;

import static gov.va.api.health.sentinel.EnvironmentAssumptions.assumeEnvironmentIn;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.sentinel.Environment;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

@Slf4j
public class RawIT {

  private static List<String> goodRequest() {
    TestIds testIds = VistaFhirQueryResourceVerifier.ids();
    return List.of(
        "/internal/raw/Coverage?icn=" + testIds.patient() + "&site=673",
        "/internal/raw/Organization?id=" + testIds.organization());
  }

  @ParameterizedTest
  @ValueSource(strings = {"/internal/raw/Organization", "/internal/raw/Coverage"})
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
  @MethodSource
  void goodRequest(String requestUrl) {
    assumeEnvironmentIn(Environment.LOCAL);
    log.info("Verify raw response for {} is [200]");
    var response =
        RestAssured.given()
            .baseUri("http://localhost")
            .port(8095)
            .relaxedHTTPSValidation()
            .headers(Map.of("client-key", "~shanktopus~"))
            .contentType("application/json")
            .accept("application/json")
            .request(Method.GET, requestUrl);
    assertThat(response.getStatusCode()).isEqualTo(200);
  }
}
