package gov.va.api.health.vistafhirquery.service.controller.ping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.health.vistafhirquery.service.config.VistaApiConfig;
import gov.va.api.health.vistafhirquery.service.controller.VistalinkApiClient;
import gov.va.api.lighthouse.charon.api.RpcInvocationResult;
import gov.va.api.lighthouse.charon.api.RpcResponse;
import gov.va.api.lighthouse.charon.models.xobvtestping.XobvTestPing;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InternalPingControllerTest {
  @Mock VistalinkApiClient vlClient;

  @Test
  void ping() {
    var samples = new PingSamples();
    when(vlClient.requestForPatient(eq("123"), any(XobvTestPing.Request.class)))
        .thenReturn(samples.rpcResponse());
    assertThat(new InternalPingController(vlClient, mock(VistaApiConfig.class)).ping("123"))
        .isEqualTo(List.of(samples.results()));
  }

  static class PingSamples {

    RpcInvocationResult results() {
      return RpcInvocationResult.builder().vista("test").response("payload").build();
    }

    RpcResponse rpcResponse() {
      return RpcResponse.builder().status(RpcResponse.Status.OK).result(results()).build();
    }
  }
}
