package gov.va.api.health.vistafhirquery.service.controller.endpoint;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class R4EndpointTransformerTest {

  @Test
  void toFhir() {
    assertThat(
            R4EndpointTransformer.builder()
                .site("101")
                .linkProperties(EndpointSamples.linkProperties())
                .build()
                .toFhir())
        .isEqualTo(EndpointSamples.R4.create().endpoint("101"));
  }
}
