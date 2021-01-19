package gov.va.api.health.vistafhirquery.service.controller;

import gov.va.api.health.vistafhirquery.service.config.VistalinkApiConfig;
import gov.va.api.lighthouse.vistalink.api.RpcDetails;
import gov.va.api.lighthouse.vistalink.api.RpcPrincipal;
import gov.va.api.lighthouse.vistalink.api.RpcRequest;
import gov.va.api.lighthouse.vistalink.api.RpcResponse;
import gov.va.api.lighthouse.vistalink.api.RpcVistaTargets;
import java.net.URI;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Value
@Builder
@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
public class RestVistalinkApiClient implements VistalinkApiClient {

  private RestTemplate restTemplate;

  private VistalinkApiConfig config;

  @SneakyThrows
  private RequestEntity<RpcRequest> buildRequestEntity(RpcRequest body) {
    var baseUrl = config().getUrl();
    if (baseUrl.endsWith("/")) {
      baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
    }
    return RequestEntity.post(new URI(baseUrl + "/rpc"))
        .contentType(MediaType.APPLICATION_JSON)
        .header("client-key", config().getClientKey())
        .body(body);
  }

  /** Request an RPC based on a patients ICN. */
  public RpcResponse request(String forPatient, RpcDetails rpcDetails) {
    RpcRequest requestBody =
        RpcRequest.builder()
            .principal(
                RpcPrincipal.builder()
                    .accessCode(config().getAccessCode())
                    .verifyCode(config().getVerifyCode())
                    .build())
            .target(RpcVistaTargets.builder().forPatient(forPatient).build())
            .rpc(rpcDetails)
            .build();
    RequestEntity<RpcRequest> request = buildRequestEntity(requestBody);
    ResponseEntity<RpcResponse> response = restTemplate.exchange(request, RpcResponse.class);
    verifyVistalinkApiResponse(response);
    return response.getBody();
  }

  private void verifyVistalinkApiResponse(ResponseEntity<RpcResponse> response) {
    if (!response.getStatusCode().is2xxSuccessful()) {
      // ToDo make this validation/error handling better
      throw new IllegalStateException("Vistalink didnt return 2xx.");
    }
  }
}
