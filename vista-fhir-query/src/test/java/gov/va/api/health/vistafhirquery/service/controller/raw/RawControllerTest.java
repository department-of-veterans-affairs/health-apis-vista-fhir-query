package gov.va.api.health.vistafhirquery.service.controller.raw;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import gov.va.api.health.vistafhirquery.service.controller.VistalinkApiClient;
import gov.va.api.lighthouse.charon.api.RpcInvocationResult;
import gov.va.api.lighthouse.charon.api.RpcRequest;
import gov.va.api.lighthouse.charon.api.RpcResponse;
import gov.va.api.lighthouse.charon.models.iblhsamcmsgetins.IblhsAmcmsGetIns;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RawControllerTest {
  @Mock VistalinkApiClient vistalinkApiClient;

  RawController _controller() {
    return RawController.builder().vistalinkApiClient(vistalinkApiClient).build();
  }

  @Test
  void organization() {
    when(vistalinkApiClient.requestForVistaSite(eq("666"), any(IblhsAmcmsGetIns.Request.class)))
        .thenReturn(
            RpcResponse.builder()
                .results(
                    List.of(
                        RpcInvocationResult.builder()
                            .vista("666")
                            .response("hey itME results")
                            .build()))
                .build());
    assertThat(_controller().rawResponse("666", "itME", null, null, null))
        .isEqualTo(
            RpcResponse.builder()
                .results(
                    List.of(
                        RpcInvocationResult.builder()
                            .vista("666")
                            .response("hey itME results")
                            .build()))
                .build());

    when(vistalinkApiClient.makeRequest(any(RpcRequest.class)))
        .thenReturn(
            RpcResponse.builder()
                .results(
                    List.of(
                        RpcInvocationResult.builder()
                            .vista("666")
                            .response("hey itME results")
                            .build()))
                .build());
    assertThat(_controller().rawResponse("666", "itME", "123", "456", "WHOdis?"))
        .isEqualTo(
            RpcResponse.builder()
                .results(
                    List.of(
                        RpcInvocationResult.builder()
                            .vista("666")
                            .response("hey itME results")
                            .build()))
                .build());
  }
}
