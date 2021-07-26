package gov.va.api.health.vistafhirquery.service.controller.coverage;

import static gov.va.api.health.vistafhirquery.service.controller.coverage.R4CoverageTransformer.stopPolicyFromBillingToBoolean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.InsuranceType;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayResponse;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayResponse.FilemanEntry;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayResponse.UnexpectedVistaValue;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayResponse.Values;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class R4CoverageTransformerTest {
  static Stream<Arguments> relationship() {
    return Stream.of(
        arguments("01", "spouse"),
        arguments("18", "self"),
        arguments("19", "child"),
        arguments("32", "parent"),
        arguments("33", "parent"),
        arguments("41", "injured"),
        arguments("53", "common"),
        arguments("G8", "other"));
  }

  @Test
  void badRelationship() {
    var entry =
        FilemanEntry.builder()
            .fields(Map.of(InsuranceType.PT_RELATIONSHIP_HIPAA, Values.of("ignored", "00")))
            .build();
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> tx().relationship(entry));
  }

  @Test
  void empty() {
    // Empty Results
    assertThat(
            R4CoverageTransformer.builder()
                .patientIcn("1010101010V666666")
                .rpcResults(
                    Map.entry("666", LhsLighthouseRpcGatewayResponse.Results.builder().build()))
                .build()
                .toFhir())
        .isEmpty();
    // Empty Fields
    assertThat(
            R4CoverageTransformer.builder()
                .patientIcn("1010101010V666666")
                .rpcResults(
                    Map.entry(
                        "666",
                        LhsLighthouseRpcGatewayResponse.Results.builder()
                            .results(
                                List.of(
                                    LhsLighthouseRpcGatewayResponse.FilemanEntry.builder()
                                        .file("2.312")
                                        .ien("1,69,")
                                        .fields(Map.of())
                                        .build()))
                            .build()))
                .build()
                .toFhir())
        .isEmpty();
  }

  @ParameterizedTest
  @MethodSource
  void relationship(String vista, String fhir) {
    var entry =
        FilemanEntry.builder()
            .fields(Map.of(InsuranceType.PT_RELATIONSHIP_HIPAA, Values.of("ignored", vista)))
            .build();
    assertThat(tx().relationship(entry).coding().get(0).code()).isEqualTo(fhir);
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

  private R4CoverageTransformer tx() {
    return R4CoverageTransformer.builder()
        .patientIcn("1010101010V666666")
        .rpcResults(Map.entry("888", LhsLighthouseRpcGatewayResponse.Results.builder().build()))
        .build();
  }

  @Test
  void yesNoToBooleanValues() {
    assertThat(stopPolicyFromBillingToBoolean("0")).isFalse();
    assertThat(stopPolicyFromBillingToBoolean("1")).isTrue();
    assertThatExceptionOfType(UnexpectedVistaValue.class)
        .isThrownBy(() -> stopPolicyFromBillingToBoolean("true"));
    assertThatExceptionOfType(UnexpectedVistaValue.class)
        .isThrownBy(() -> stopPolicyFromBillingToBoolean(""));
  }
}
