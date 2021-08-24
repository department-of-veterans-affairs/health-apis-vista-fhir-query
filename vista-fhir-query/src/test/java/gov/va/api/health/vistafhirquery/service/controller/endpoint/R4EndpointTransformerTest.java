package gov.va.api.health.vistafhirquery.service.controller.endpoint;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.r4.api.resources.Endpoint;
import org.junit.jupiter.api.Test;

public class R4EndpointTransformerTest {

  @Test
  void toFhir() {
    assertThat(
            R4EndpointTransformer.builder()
                .site("101")
                .status(Endpoint.EndpointStatus.active)
                .linkProperties(EndpointSamples.linkProperties())
                .build()
                .toFhir())
        .isEqualTo(EndpointSamples.R4.create().endpoint("101", Endpoint.EndpointStatus.active));
  }
}
