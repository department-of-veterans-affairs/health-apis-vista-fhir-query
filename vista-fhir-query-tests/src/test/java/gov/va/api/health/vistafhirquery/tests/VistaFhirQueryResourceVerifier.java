package gov.va.api.health.vistafhirquery.tests;

import gov.va.api.health.fhir.testsupport.ResourceVerifier;
import lombok.experimental.UtilityClass;

@UtilityClass
public class VistaFhirQueryResourceVerifier {

  public static TestIds ids() {
    return SystemDefinitions.systemDefinition().publicIds();
  }

  public static ResourceVerifier r4() {
    return ResourceVerifier.builder()
        .apiPath(SystemDefinitions.systemDefinition().r4().apiPath())
        .bundleClass(gov.va.api.health.r4.api.bundle.AbstractBundle.class)
        .testClient(TestClients.r4())
        .operationOutcomeClass(gov.va.api.health.r4.api.resources.OperationOutcome.class)
        // Until paging is implemented, only vista is enforcing our maximum amount
        .maxCount(9999)
        .build();
  }
}
