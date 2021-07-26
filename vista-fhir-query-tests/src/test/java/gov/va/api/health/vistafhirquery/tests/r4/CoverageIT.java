package gov.va.api.health.vistafhirquery.tests.r4;

import static gov.va.api.health.sentinel.EnvironmentAssumptions.assumeEnvironmentIn;

import gov.va.api.health.fhir.testsupport.ResourceVerifier;
import gov.va.api.health.r4.api.resources.Coverage;
import gov.va.api.health.r4.api.resources.OperationOutcome;
import gov.va.api.health.sentinel.Environment;
import gov.va.api.health.vistafhirquery.tests.TestIds;
import gov.va.api.health.vistafhirquery.tests.VistaFhirQueryResourceVerifier;
import java.util.function.Predicate;
import lombok.experimental.Delegate;
import org.junit.jupiter.api.Test;

public class CoverageIT {
  private final TestIds testIds = VistaFhirQueryResourceVerifier.ids();

  @Delegate private final ResourceVerifier verifier = VistaFhirQueryResourceVerifier.r4();

  private Predicate<Coverage.Bundle> bundleHasResults() {
    return b -> !b.entry().isEmpty();
  }

  @Test
  void read() {
    // ToDo we need static patients doppelganger to have coverages outside the local env
    assumeEnvironmentIn(Environment.LOCAL);
    var path = "Coverage/{coverage}";
    verifyAll(
        test(200, Coverage.class, path, testIds.coverage()),
        test(404, OperationOutcome.class, path, "I3-404"));
  }

  @Test
  void search() {
    // ToDo we need static patients doppelganger to have coverages outside the local env
    assumeEnvironmentIn(Environment.LOCAL);
    verifyAll(
        test(
            200,
            Coverage.Bundle.class,
            bundleHasResults(),
            "Coverage?patient={icn}",
            testIds.patient()));
  }
}
