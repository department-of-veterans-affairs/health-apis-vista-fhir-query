package gov.va.api.health.vistafhirquery.service.controller.organization;

import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayResponse;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

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

  @Test
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
