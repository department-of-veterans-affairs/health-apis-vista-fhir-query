package gov.va.api.health.vistafhirquery.service.controller;

import gov.va.api.health.vistafhirquery.service.config.VistalinkApiConfig;
import gov.va.api.lighthouse.vistalink.api.RpcDetails;
import gov.va.api.lighthouse.vistalink.api.RpcPrincipal;
import gov.va.api.lighthouse.vistalink.api.RpcRequest;
import gov.va.api.lighthouse.vistalink.api.RpcResponse;
import gov.va.api.lighthouse.vistalink.api.RpcVistaTargets;
import java.net.URI;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Value
@Builder
public class VistalinkApiRequestor {
  @NonNull String patient;

  @NonNull RestTemplate restTemplate;

  @NonNull VistalinkApiConfig config;

  static VistalinkApiRequestorBuilder forPatient(String patientIcn) {
    return VistalinkApiRequestor.builder().patient(patientIcn);
  }

  @SneakyThrows
  private RequestEntity<RpcRequest> buildRequest(RpcRequest body) {
    var baseUrl = config().getUrl();
    if (baseUrl.endsWith("/")) {
      baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
    }
    return RequestEntity.post(new URI(baseUrl + "/rpc"))
        .contentType(MediaType.APPLICATION_JSON)
        .header("client-key", config().getClientKey())
        .body(body);
  }

  /** Make request to the Vistalink API with the given RPC Details. */
  public RpcResponse request(RpcDetails rpcDetails) {
    RpcRequest requestBody =
        RpcRequest.builder()
            .principal(
                RpcPrincipal.builder()
                    .accessCode(config().getAccessCode())
                    .verifyCode(config().getVerifyCode())
                    .build())
            .target(RpcVistaTargets.builder().forPatient(patient()).build())
            .rpc(rpcDetails)
            .build();
    RequestEntity<RpcRequest> request = buildRequest(requestBody);
    ResponseEntity<RpcResponse> response = restTemplate.exchange(request, RpcResponse.class);
    verifyResponse(response);
    return response.getBody();
  }

  private void verifyResponse(ResponseEntity<RpcResponse> response) {
    if (!response.getStatusCode().is2xxSuccessful()) {
      // ToDo make this validation/error handling better
      throw new IllegalStateException("Vistalink didnt return 2xx.");
    }
  }
}
