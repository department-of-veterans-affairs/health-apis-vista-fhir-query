package gov.va.api.health.vistafhirquery.tests;

import static gov.va.api.health.sentinel.EnvironmentAssumptions.assumeEnvironmentNotIn;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

import gov.va.api.health.r4.api.resources.Observation;
import gov.va.api.health.sentinel.AccessTokens;
import gov.va.api.health.sentinel.Environment;
import gov.va.api.health.sentinel.ExpectedResponse;
import gov.va.api.health.vistafhirquery.tests.TestIds.IcnAtSites;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.specification.RequestSpecification;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@Slf4j
public class VistaConnectivityIT {
  private static String token;

  @BeforeAll
  static void acquireToken() {
    assumeSystemAccessTokenIsPossible();
    token = AccessTokens.get().forSystemScopes("system/Observation.read");
  }

  private static void assumeSystemAccessTokenIsPossible() {
    SystemDefinitions.loadConfigSecretsProperties();
    for (var property :
        List.of(
            "system-oauth-robot.aud",
            "system-oauth-robot.token-url",
            "system-oauth-robot.client-id",
            "system-oauth-robot.client-secret")) {

      String value = System.getProperty(property);
      if (isBlank(value)) {
        log.info("Missing property {}", property);
      }
      assumeThat(value)
          .as("System access token cannot be created: Missing property: %s", property)
          .isNotBlank();
    }
  }

  static Stream<Arguments> connected() {
    return SystemDefinitions.systemDefinition().publicIds().patientSites().stream()
        .map(Arguments::of);
  }

  @ParameterizedTest
  @MethodSource
  void connected(IcnAtSites icnAtSites) {
    assumeEnvironmentNotIn(Environment.STAGING, Environment.PROD);
    var sd = SystemDefinitions.systemDefinition().r4();
    RequestSpecification request =
        RestAssured.given()
            .baseUri(sd.url())
            .port(sd.port())
            .relaxedHTTPSValidation()
            .headers(Map.of("Authorization", "Bearer " + token))
            .contentType("application/json")
            .accept("application/json");
    ExpectedResponse response =
        ExpectedResponse.of(
            request.request(
                Method.GET, sd.apiPath() + "Observation?patient={icn}", icnAtSites.icn()));
    var bundle = response.expect(200).expectValid(Observation.Bundle.class);
    assertThat(bundle.total()).isGreaterThan(0);
  }
}
