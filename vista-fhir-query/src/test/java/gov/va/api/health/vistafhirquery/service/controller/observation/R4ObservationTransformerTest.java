package gov.va.api.health.vistafhirquery.service.controller.observation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class R4ObservationTransformerTest {

  @Test
  public void ToFhir() {
    assertThat(
            R4ObservationTransformer.builder()
                .resultsEntry(ObservationSamples.Vista.create().resultsByStation())
                .build()
                .toFhir()
                .collect(Collectors.toList()))
        .isEqualTo(ObservationSamples.Fhir.create().observations());
  }
}
