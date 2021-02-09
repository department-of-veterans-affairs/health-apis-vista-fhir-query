package gov.va.api.health.vistafhirquery.service.controller.observation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class R4ObservationTransformerTest {
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
                .resultsEntry(ObservationSamples.Vista.create().resultsByStation())
                .build()
                .toFhir()
                .collect(Collectors.toList()))
        .isEqualTo(ObservationSamples.Fhir.create().observations());
  }
}
