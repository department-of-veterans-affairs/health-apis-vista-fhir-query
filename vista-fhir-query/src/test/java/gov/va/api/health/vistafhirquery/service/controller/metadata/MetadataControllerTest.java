package gov.va.api.health.vistafhirquery.service.controller.metadata;

import static gov.va.api.health.vistafhirquery.service.controller.metadata.MetadataSamples.json;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class MetadataControllerTest {

  @Test
  void r4() {
    var r4 = MetadataSamples.R4.create();
    var controller =
        new R4MetadataController(
            MetadataSamples.linkProperties(),
            r4.conformanceProperties(),
            MetadataSamples.buildProperties());
    assertThat(json(controller.read())).isEqualTo(json(r4.capabilityStatement()));
  }
}
