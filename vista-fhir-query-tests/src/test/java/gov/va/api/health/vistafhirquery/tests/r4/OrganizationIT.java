package gov.va.api.health.vistafhirquery.tests.r4;

import static gov.va.api.health.sentinel.EnvironmentAssumptions.assumeEnvironmentIn;

import gov.va.api.health.fhir.testsupport.ResourceVerifier;
import gov.va.api.health.r4.api.resources.OperationOutcome;
import gov.va.api.health.r4.api.resources.Organization;
import gov.va.api.health.sentinel.Environment;
import gov.va.api.health.vistafhirquery.tests.TestIds;
import gov.va.api.health.vistafhirquery.tests.VistaFhirQueryResourceVerifier;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class OrganizationIT {
  private final TestIds testIds = VistaFhirQueryResourceVerifier.ids();
  @Delegate private final ResourceVerifier verifier = VistaFhirQueryResourceVerifier.r4();

  @Test
  void read() {
    assumeEnvironmentIn(Environment.LOCAL, Environment.QA);
    var path = "Organization/{id}";
    verifyAll(
        test(200, Organization.class, path, testIds.organization()),
        test(404, OperationOutcome.class, path, "I2-404"));
  }
}
