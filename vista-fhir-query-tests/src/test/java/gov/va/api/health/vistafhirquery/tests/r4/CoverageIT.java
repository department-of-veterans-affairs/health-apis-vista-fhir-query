package gov.va.api.health.vistafhirquery.tests.r4;

import static gov.va.api.health.sentinel.EnvironmentAssumptions.assumeEnvironmentNotIn;

import gov.va.api.health.fhir.testsupport.ResourceVerifier;
import gov.va.api.health.r4.api.resources.Coverage;
import gov.va.api.health.r4.api.resources.OperationOutcome;
import gov.va.api.health.sentinel.Environment;
import gov.va.api.health.vistafhirquery.tests.TestIds;
import gov.va.api.health.vistafhirquery.tests.VistaFhirQueryResourceVerifier;
import lombok.experimental.Delegate;
import org.junit.jupiter.api.Test;

public class CoverageIT {
  private final TestIds testIds = VistaFhirQueryResourceVerifier.ids();

  @Delegate private final ResourceVerifier verifier = VistaFhirQueryResourceVerifier.r4();

  @Test
  void read() {
    assumeEnvironmentNotIn(Environment.STAGING, Environment.PROD);
    var path = "Coverage/{coverage}";
    verifyAll(
        test(200, Coverage.class, path, testIds.coverage()),
        test(404, OperationOutcome.class, path, "I3-404"));
  }

  @Test
  void search() {
    assumeEnvironmentNotIn(Environment.STAGING, Environment.PROD);
    verifyAll(
        test(
            200,
            Coverage.Bundle.class,
            R4TestSupport::atLeastOneEntry,
            "Coverage?patient={icn}",
            testIds.patient()));
  }
}
