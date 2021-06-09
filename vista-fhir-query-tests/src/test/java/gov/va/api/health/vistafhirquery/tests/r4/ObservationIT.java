package gov.va.api.health.vistafhirquery.tests.r4;

import static gov.va.api.health.sentinel.EnvironmentAssumptions.assumeEnvironmentNotIn;

import gov.va.api.health.fhir.testsupport.ResourceVerifier;
import gov.va.api.health.r4.api.resources.Observation;
import gov.va.api.health.r4.api.resources.OperationOutcome;
import gov.va.api.health.sentinel.Environment;
import gov.va.api.health.vistafhirquery.tests.TestIds;
import gov.va.api.health.vistafhirquery.tests.VistaFhirQueryResourceVerifier;
import java.util.function.Predicate;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class ObservationIT {
  private final TestIds testIds = VistaFhirQueryResourceVerifier.ids();

  @Delegate private final ResourceVerifier verifier = VistaFhirQueryResourceVerifier.r4();

  private Predicate<Observation.Bundle> assertBundleIsNotEmpty() {
    return bundle -> !bundle.entry().isEmpty();
  }

  @Test
  void read() {
    assumeEnvironmentNotIn(Environment.STAGING, Environment.PROD);
    var path = "Observation/{observation}";
    verifyAll(
        test(200, Observation.class, path, testIds.observationVitalSign()),
        test(200, Observation.class, path, testIds.observationLaboratory()),
        test(404, OperationOutcome.class, path, "I2-404"));
  }

  @Test
  void search() {
    assumeEnvironmentNotIn(Environment.STAGING, Environment.PROD);
    verifyAll(
        test(
            200,
            Observation.Bundle.class,
            assertBundleIsNotEmpty(),
            "Observation?_id={id}",
            testIds.observationVitalSign()),
        test(
            200,
            Observation.Bundle.class,
            assertBundleIsNotEmpty(),
            "Observation?identifier={id}",
            testIds.observationLaboratory()),
        test(
            200,
            Observation.Bundle.class,
            assertBundleIsNotEmpty(),
            "Observation?patient={patient}",
            testIds.patient()),
        test(
            200,
            Observation.Bundle.class,
            assertBundleIsNotEmpty(),
            "Observation?patient={patient}&category=laboratory",
            testIds.patient()),
        test(
            200,
            Observation.Bundle.class,
            assertBundleIsNotEmpty(),
            "Observation?patient={patient}&code=8310-5",
            testIds.patient()),
        test(
            200,
            Observation.Bundle.class,
            assertBundleIsNotEmpty(),
            "Observation?patient={patient}&date=ge2010&date=lt2012",
            testIds.patient()),
        test(
            200,
            Observation.Bundle.class,
            assertBundleIsNotEmpty(),
            "Observation?patient={patient}"
                + "&category=vital-signs"
                + "&code=8310-5"
                + "&date=ge2010"
                + "&date=lt2012",
            testIds.patient()),
        test(
            400,
            OperationOutcome.class,
            "Observation?patient={patient}&date=ge2012&date=lt2010",
            testIds.patient()));
  }

  @Test
  void searchNotMe() {
    assumeEnvironmentNotIn(Environment.LOCAL);
    verify(test(403, OperationOutcome.class, "Observation?patient={patient}", testIds.unknown()));
  }
}
