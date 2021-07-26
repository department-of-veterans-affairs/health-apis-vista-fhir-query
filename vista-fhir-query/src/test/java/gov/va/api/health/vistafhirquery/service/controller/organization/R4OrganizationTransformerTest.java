package gov.va.api.health.vistafhirquery.service.controller.organization;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayResponse;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class R4OrganizationTransformerTest {

  @Test
  void empty() {
    assertThat(
            R4OrganizationTransformer.builder()
                .rpcResults(
                    Map.entry("666", LhsLighthouseRpcGatewayResponse.Results.builder().build()))
                .build()
                .toFhir())
        .isEmpty();
  }

  void toFhir() {
    assertThat(
            R4OrganizationTransformer.builder()
                .rpcResults(
                    Map.entry(
                        "666",
                        OrganizationSamples.VistaLhsLighthouseRpcGateway.create()
                            .getsManifestResults()))
                .build()
                .toFhir()
                .findFirst()
                .get())
        .isEqualTo(OrganizationSamples.R4.create().organization());
  }
}
