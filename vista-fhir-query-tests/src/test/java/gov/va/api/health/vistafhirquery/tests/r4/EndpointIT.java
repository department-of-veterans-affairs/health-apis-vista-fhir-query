package gov.va.api.health.vistafhirquery.tests.r4;

import static gov.va.api.health.sentinel.EnvironmentAssumptions.assumeEnvironmentNotIn;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.r4.api.resources.Endpoint;
import gov.va.api.health.sentinel.Environment;
import gov.va.api.health.sentinel.ExpectedResponse;
import gov.va.api.health.vistafhirquery.tests.SystemDefinitions;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Slf4j
public class EndpointIT {
  private RequestSpecification request(gov.va.api.health.sentinel.ServiceDefinition sd) {
    RequestSpecification request =
        RestAssured.given()
            .baseUri(sd.url())
            .port(sd.port())
            .relaxedHTTPSValidation()
            .contentType("application/json")
            .accept("application/json");
    return request;
  }

  @ParameterizedTest
  @ValueSource(strings = {"Endpoint", "Endpoint?status=active"})
  void search(String query) {
    assumeEnvironmentNotIn(Environment.STAGING, Environment.PROD);
    var sd = SystemDefinitions.systemDefinition().r4();
    ExpectedResponse bundleResponse =
        ExpectedResponse.of(request(sd).request(Method.GET, sd.apiPath() + query));
    var bundle = bundleResponse.expect(200).expectValid(Endpoint.Bundle.class);
    assertThat(bundle.total()).isGreaterThan(0);
  }

  @Test
  void searchWithBadStatus() {
    assumeEnvironmentNotIn(Environment.STAGING, Environment.PROD);
    var sd = SystemDefinitions.systemDefinition().r4();
    ExpectedResponse bundleResponse =
        ExpectedResponse.of(
            request(sd).request(Method.GET, sd.apiPath() + "Endpoint?status=INVALID"));
    var bundle = bundleResponse.expect(200).expectValid(Endpoint.Bundle.class);
    assertThat(bundle.total()).isEqualTo(0);
  }
}
