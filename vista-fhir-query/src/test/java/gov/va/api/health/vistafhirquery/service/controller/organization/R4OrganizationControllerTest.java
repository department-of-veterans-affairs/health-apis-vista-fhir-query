package gov.va.api.health.vistafhirquery.service.controller.organization;

import static gov.va.api.health.vistafhirquery.service.controller.coverage.CoverageSamples.json;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import gov.va.api.health.vistafhirquery.service.controller.ResourceExceptions.ExpectationFailed;
import gov.va.api.health.vistafhirquery.service.controller.ResourceExceptions.NotFound;
import gov.va.api.health.vistafhirquery.service.controller.VistalinkApiClient;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.WitnessProtection;
import gov.va.api.lighthouse.charon.api.RpcInvocationResult;
import gov.va.api.lighthouse.charon.api.RpcResponse;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayGetsManifest;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class R4OrganizationControllerTest {
  @Mock VistalinkApiClient vlClient;

  @Mock WitnessProtection witnessProtection;

  private R4OrganizationController controller() {
    return R4OrganizationController.builder()
        .vistalinkApiClient(vlClient)
        .witnessProtection(witnessProtection)
        .build();
  }

  @Test
  void readReturnsKnownResource() {
    // OrganizationSamples.VistaLhsLighthouseRpcGateway.create();
    var samples = "x";
    // samples.getsManifestResults("ip1");
    var results = "y";
    when(vlClient.requestForVistaSite(
            eq("123"), any(LhsLighthouseRpcGatewayGetsManifest.Request.class)))
        .thenReturn(
            RpcResponse.builder()
                .status(RpcResponse.Status.OK)
                .results(
                    List.of(
                        RpcInvocationResult.builder().vista("123").response(json(results)).build()))
                .build());
    when(witnessProtection.toPrivateId("pub1")).thenReturn("p1+123+ip1");
    var actual = controller().organizationRead("pub1");
    // OrganizationSamples.R4.create().coverage("123", "ip1", "p1");
    var expected = "z";
    assertThat(json(actual)).isEqualTo(json(expected));
  }

  @Test
  void readThrowsExpectationFailedWhenTooManyResultsAreFound() {
    // samples.getsManifestResults("ip1");
    var result1 = "y";
    // samples.getsManifestResults("ip1");
    var result2 = "y";
    when(witnessProtection.toPrivateId("pub1")).thenReturn("p1+123+ip1");
    when(vlClient.requestForVistaSite(
            eq("123"), any(LhsLighthouseRpcGatewayGetsManifest.Request.class)))
        .thenReturn(
            RpcResponse.builder()
                .status(RpcResponse.Status.OK)
                .results(
                    List.of(
                        RpcInvocationResult.builder().vista("123").response(json(result1)).build(),
                        RpcInvocationResult.builder().vista("123").response(json(result2)).build()))
                .build());
    assertThatExceptionOfType(ExpectationFailed.class)
        .isThrownBy(() -> controller().organizationRead("pub1"));
  }

  @Test
  void readThrowsNotFoundForBadId() {
    when(witnessProtection.toPrivateId("nope1")).thenReturn("nope1");
    assertThatExceptionOfType(NotFound.class)
        .isThrownBy(() -> controller().organizationRead("nope1"));
  }

  @Test
  void readThrowsNotFoundWhenNoResultsAreFound() {
    var results = LhsLighthouseRpcGatewayResponse.Results.builder().build();
    when(witnessProtection.toPrivateId("pub1")).thenReturn("p1+123+ip1");
    when(vlClient.requestForVistaSite(
            eq("123"), any(LhsLighthouseRpcGatewayGetsManifest.Request.class)))
        .thenReturn(
            RpcResponse.builder()
                .status(RpcResponse.Status.OK)
                .results(
                    List.of(
                        RpcInvocationResult.builder().vista("123").response(json(results)).build()))
                .build());
    assertThatExceptionOfType(NotFound.class)
        .isThrownBy(() -> controller().organizationRead("pub1"));
  }
}
