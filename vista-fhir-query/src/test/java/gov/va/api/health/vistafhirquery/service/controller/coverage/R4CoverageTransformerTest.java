package gov.va.api.health.vistafhirquery.service.controller.coverage;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.r4.api.elements.Reference;
import gov.va.api.health.r4.api.resources.Coverage;
import gov.va.api.lighthouse.charon.models.iblhsamcmsgetins.GetInsRpcResults;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class R4CoverageTransformerTest {
  @Test
  void empty() {
    assertThat(
            R4CoverageTransformer.builder()
                .patientIcn("1010101010V666666")
                .rpcResult(Map.entry("666", GetInsRpcResults.empty()))
                .build()
                .toFhir())
        .isEqualTo(
            Coverage.builder()
                .id("1010101010V666666^666")
                .status(Coverage.Status.active)
                .beneficiary(Reference.builder().reference("Patient/1010101010V666666").build())
                .build());
  }

  @Test
  void toFhir() {
    assertThat(
            R4CoverageTransformer.builder()
                .patientIcn("1010101010V666666")
                .rpcResult(
                    Map.entry("666", CoverageSamples.VistaIblhsGetInsRpc.create().getInsResults()))
                .build()
                .toFhir())
        .isEqualTo(CoverageSamples.R4.create().coverage());
  }
}
