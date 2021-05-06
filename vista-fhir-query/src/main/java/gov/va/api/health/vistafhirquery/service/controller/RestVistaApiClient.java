package gov.va.api.health.vistafhirquery.service.controller;

import gov.va.api.health.vistafhirquery.service.config.VistaApiConfig;
import gov.va.api.lighthouse.charon.api.RpcRequest;
import gov.va.api.lighthouse.charon.api.RpcResponse;
import gov.va.api.lighthouse.charon.api.RpcVistaTargets;
import gov.va.api.lighthouse.charon.models.TypeSafeRpcRequest;
import java.net.URI;
import java.util.List;
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
public class RestVistaApiClient implements VistalinkApiClient {
  private RestTemplate restTemplate;

  private VistaApiConfig config;

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

  @SneakyThrows
  private RpcResponse makeRequest(RpcRequest rpcRequest) {
    RequestEntity<RpcRequest> request = buildRequestEntity(rpcRequest);
    ResponseEntity<RpcResponse> response = restTemplate.exchange(request, RpcResponse.class);
    verifyVistalinkApiResponse(response);
    return response.getBody();
  }

  /** Request an RPC based on a patients ICN. */
  @Override
  public RpcResponse requestForPatient(String patient, TypeSafeRpcRequest rpcRequestDetails) {
    RpcRequest rpcRequest =
        RpcRequest.builder()
            .principal(config().defaultPrincipal())
            .siteSpecificPrincipals(config().siteSpecificPrincipals())
            .target(RpcVistaTargets.builder().forPatient(patient).build())
            .rpc(rpcRequestDetails.asDetails())
            .build();
    return makeRequest(rpcRequest);
  }

  /** Request an RPC at a specific VistA site. */
  @Override
  public RpcResponse requestForVistaSite(String vistaSite, TypeSafeRpcRequest rpcRequestDetails) {
    RpcRequest rpcRequest =
        RpcRequest.builder()
            .principal(config().getSiteSpecificPrincipalOrDefault(vistaSite))
            .target(RpcVistaTargets.builder().include(List.of(vistaSite)).build())
            .rpc(rpcRequestDetails.asDetails())
            .build();
    return makeRequest(rpcRequest);
  }

  private void verifyVistalinkApiResponse(ResponseEntity<RpcResponse> response) {
    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new IllegalStateException("Vistalink API didnt return 2xx HTTP status code.");
    }
  }
}
