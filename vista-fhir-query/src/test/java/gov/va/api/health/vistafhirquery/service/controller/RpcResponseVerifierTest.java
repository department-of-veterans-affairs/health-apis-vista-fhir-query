package gov.va.api.health.vistafhirquery.service.controller;

import static gov.va.api.health.vistafhirquery.service.controller.RpcResponseVerifier.verifyAndReturnResults;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import gov.va.api.lighthouse.charon.api.RpcInvocationResult;
import gov.va.api.lighthouse.charon.api.RpcResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class RpcResponseVerifierTest {
  @ParameterizedTest
  @EnumSource(
      value = RpcResponse.Status.class,
      names = {"VISTA_RESOLUTION_FAILURE", "FAILED"})
  void verifyAndReturnResultsFailures(RpcResponse.Status status) {
    assertThatExceptionOfType(RpcResponseVerifier.VistalinkApiRequestFailure.class)
        .isThrownBy(() -> verifyAndReturnResults(RpcResponse.builder().status(status).build()));
  }

  @Test
  void verifyAndReturnResultsNoVistasResolved() {
    assertThat(
            verifyAndReturnResults(
                RpcResponse.builder().status(RpcResponse.Status.NO_VISTAS_RESOLVED).build()))
        .isEmpty();
  }

  @Test
  void verifyResultsReturned() {
    assertThat(
            verifyAndReturnResults(
                RpcResponse.builder()
                    .status(RpcResponse.Status.OK)
                    .results(List.of(RpcInvocationResult.builder().vista("888").build()))
                    .build()))
        .containsExactly(RpcInvocationResult.builder().vista("888").build());
  }
}
