package gov.va.api.health.vistafhirquery.service.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import gov.va.api.lighthouse.vistalink.api.RpcResponse;
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
        .isThrownBy(
            () ->
                RpcResponseVerifier.verifyAndReturnResults(
                    RpcResponse.builder().status(status).build()));
  }

  @Test
  void verifyAndReturnResultsNoVistasResolved() {
    assertThat(
            RpcResponseVerifier.verifyAndReturnResults(
                RpcResponse.builder().status(RpcResponse.Status.NO_VISTAS_RESOLVED).build()))
        .isEmpty();
  }
}
