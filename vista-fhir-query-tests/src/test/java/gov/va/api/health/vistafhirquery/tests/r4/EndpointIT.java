package gov.va.api.health.vistafhirquery.tests.r4;

import static gov.va.api.health.sentinel.EnvironmentAssumptions.assumeEnvironmentNotIn;

import gov.va.api.health.fhir.testsupport.ResourceVerifier;
import gov.va.api.health.r4.api.resources.Endpoint;
import gov.va.api.health.sentinel.Environment;
import gov.va.api.health.vistafhirquery.tests.VistaFhirQueryResourceVerifier;
import lombok.experimental.Delegate;
import org.junit.jupiter.api.Test;

public class EndpointIT {

  @Delegate private final ResourceVerifier verifier = VistaFhirQueryResourceVerifier.r4();

  @Test
  void search() {
    assumeEnvironmentNotIn(Environment.STAGING, Environment.PROD);
    verifyAll(test(200, Endpoint.Bundle.class, R4TestSupport::atLeastOneEntry, "Endpoint"));
  }
}
