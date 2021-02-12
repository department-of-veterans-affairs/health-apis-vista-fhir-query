package gov.va.api.health.vistafhirquery.service.controller.observation;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.vistalink.models.vprgetpatientdata.Labs;
import gov.va.api.lighthouse.vistalink.models.vprgetpatientdata.Vitals;
import gov.va.api.lighthouse.vistalink.models.vprgetpatientdata.VprGetPatientData;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class R4ObservationTransformerTest {
  @Test
  void combined() {
    var vprResp =
        VprGetPatientData.Response.Results.builder()
            .labs(Labs.builder().labResults(ObservationLabSamples.Vista.create().labs()).build())
            .vitals(
                Vitals.builder().vitalResults(ObservationSamples.Vista.create().vitals()).build())
            .build();
    var entry = Map.entry("673", vprResp);
    assertThat(
            R4ObservationTransformer.builder()
                .patientIcn("p1")
                .resultsEntry(entry)
                .build()
                .toFhir())
        .containsExactly(
            ObservationSamples.Fhir.create().bloodPressure(),
            ObservationSamples.Fhir.create().weight(),
            ObservationLabSamples.Fhir.create().observation());
  }

  @Test
  void labToFhir() {
    assertThat(
            R4ObservationTransformer.builder()
                .patientIcn("p1")
                .resultsEntry(ObservationLabSamples.Vista.create().resultsByStation())
                .build()
                .toFhir())
        .containsExactly(ObservationLabSamples.Fhir.create().observation());
  }

  @Test
  public void vitalToFhir() {
    assertThat(
            R4ObservationTransformer.builder()
                .patientIcn("p1")
                .resultsEntry(ObservationSamples.Vista.create().resultsByStation())
                .build()
                .toFhir()
                .collect(Collectors.toList()))
        .isEqualTo(ObservationSamples.Fhir.create().observations());
  }
}
