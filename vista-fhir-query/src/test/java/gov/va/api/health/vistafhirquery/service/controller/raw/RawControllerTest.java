package gov.va.api.health.vistafhirquery.service.controller.raw;

import static gov.va.api.health.vistafhirquery.service.controller.MockRequests.json;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import gov.va.api.health.vistafhirquery.service.controller.MockWitnessProtection;
import gov.va.api.health.vistafhirquery.service.controller.VistalinkApiClient;
import gov.va.api.health.vistafhirquery.service.controller.organization.OrganizationSamples;
import gov.va.api.lighthouse.charon.api.RpcInvocationResult;
import gov.va.api.lighthouse.charon.api.RpcRequest;
import gov.va.api.lighthouse.charon.api.RpcResponse;
import gov.va.api.lighthouse.charon.models.TypeSafeRpcRequest;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayGetsManifest.Request;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayResponse;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RawControllerTest {
  @Mock VistalinkApiClient vistalinkApiClient;

  MockWitnessProtection wp = new MockWitnessProtection();

  RawController _controller() {
    return RawController.builder()
        .vistalinkApiClient(vistalinkApiClient)
        .witnessProtection(wp)
        .build();
  }

  @Test
  void coverageBySiteAndIcnReturnsWhateverRpcResponseIsFound() {
    var rpcResponse =
        RpcResponse.builder()
            .results(
                List.of(
                    RpcInvocationResult.builder()
                        .vista("666")
                        .response("hey itME results")
                        .build()))
            .build();
    when(vistalinkApiClient.requestForVistaSite(eq("666"), any(TypeSafeRpcRequest.class)))
        .thenReturn(rpcResponse);
    when(vistalinkApiClient.makeRequest(any(RpcRequest.class))).thenReturn(rpcResponse);
    var expected =
        RpcResponse.builder()
            .results(
                List.of(
                    RpcInvocationResult.builder()
                        .vista("666")
                        .response("hey itME results")
                        .build()))
            .build();
    assertThat(_controller().coverageBySiteAndIcn("666", "itME", null, null, null))
        .isEqualTo(expected);
    assertThat(_controller().coverageBySiteAndIcn("666", "itME", "123", "456", "WHOdis?"))
        .isEqualTo(expected);
  }

  @Test
  void organizationByIdReturnsGatewayResponse() {
    wp.add("pub1", "site1;36;ien1");

    var results =
        OrganizationSamples.VistaLhsLighthouseRpcGateway.create().getsManifestResults("ien1");

    when(vistalinkApiClient.requestForVistaSite(Mockito.eq("site1"), Mockito.any(Request.class)))
        .thenReturn(
            RpcResponse.builder()
                .results(
                    List.of(
                        RpcInvocationResult.builder()
                            .vista("site1")
                            .response(json(results))
                            .build()))
                .build());

    var actualLhsResponse = _controller().organizationById("pub1");
    assertThat(actualLhsResponse)
        .isEqualTo(
            LhsLighthouseRpcGatewayResponse.builder()
                .resultsByStation(Map.of("site1", results))
                .build());
  }
}
