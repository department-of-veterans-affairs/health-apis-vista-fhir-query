package gov.va.api.health.vistafhirquery.service.controller.endpoint;

import static gov.va.api.health.vistafhirquery.service.controller.MockRequests.json;
import static gov.va.api.health.vistafhirquery.service.controller.endpoint.EndpointSamples.R4.link;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.r4.api.bundle.BundleLink;
import gov.va.api.health.r4.api.resources.Endpoint;
import java.util.List;
import org.junit.jupiter.api.Test;

public class R4EndpointControllerTest {

  private R4EndpointController controller() {
    return new R4EndpointController(
        EndpointSamples.linkProperties(), EndpointSamples.rpcPrincipalLookup());
  }

  @Test
  void endpointSearch() {
    var actual = controller().endpointSearch(null);
    var expected =
        EndpointSamples.R4.asBundle(
            "http://fake.com",
            List.of(
                EndpointSamples.R4.create().endpoint("101", Endpoint.EndpointStatus.active),
                EndpointSamples.R4.create().endpoint("103", Endpoint.EndpointStatus.active)),
            2,
            link(BundleLink.LinkRelation.self, "http://fake.com/r4/Endpoint"));
    assertThat(json(actual)).isEqualTo(json(expected));
  }
}
