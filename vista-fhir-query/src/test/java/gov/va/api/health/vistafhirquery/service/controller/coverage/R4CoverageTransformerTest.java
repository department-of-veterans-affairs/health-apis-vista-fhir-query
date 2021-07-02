package gov.va.api.health.vistafhirquery.service.controller.coverage;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayResponse;
import java.time.ZoneId;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class R4CoverageTransformerTest {
  @Test
  void empty() {
    assertThat(
            R4CoverageTransformer.builder()
                .patientIcn("1010101010V666666")
                .vistaZoneId(ZoneId.of("Z"))
                .rpcResults(
                    Map.entry("666", LhsLighthouseRpcGatewayResponse.Results.builder().build()))
                .build()
                .toFhir())
        .isEmpty();
  }

  @Test
  void toFhir() {
    assertThat(
            R4CoverageTransformer.builder()
                .patientIcn("1010101010V666666")
                .rpcResults(
                    Map.entry(
                        "666",
                        CoverageSamples.VistaLhsLighthouseRpcGateway.create()
                            .getsManifestResults()))
                .build()
                .toFhir()
                .findFirst()
                .get())
        .isEqualTo(CoverageSamples.R4.create().coverage());
  }
}
