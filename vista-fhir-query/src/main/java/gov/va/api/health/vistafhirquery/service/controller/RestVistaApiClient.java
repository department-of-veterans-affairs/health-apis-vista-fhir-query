package gov.va.api.health.vistafhirquery.service.controller;

import gov.va.api.health.vistafhirquery.service.config.VistaApiConfig;
import gov.va.api.lighthouse.charon.api.RpcPrincipal;
import gov.va.api.lighthouse.charon.api.RpcPrincipalLookup;
import gov.va.api.lighthouse.charon.api.RpcRequest;
import gov.va.api.lighthouse.charon.api.RpcResponse;
import gov.va.api.lighthouse.charon.api.RpcVistaTargets;
import gov.va.api.lighthouse.charon.models.TypeSafeRpcRequest;
import java.net.URI;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/** Rest client for the Vista API. */
@Value
@Builder
@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class RestVistaApiClient implements VistalinkApiClient {
  private RestTemplate restTemplate;

  private VistaApiConfig config;

  private RpcPrincipalLookup rpcPrincipalLookup;

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

  private Map<String, RpcPrincipal> getPrincipals(TypeSafeRpcRequest rpcRequestDetails) {
    Map<String, RpcPrincipal> principals =
        rpcPrincipalLookup.findByName(rpcRequestDetails.asDetails().name());
    // Loma Linda context hack
    RpcPrincipal maybeLomaLinda = principals.get("605");
    if (maybeLomaLinda != null
        && !"unset".equals(config().getLomaLindaHackContext())
        && "VPR GET PATIENT DATA".equals(rpcRequestDetails.asDetails().name())) {
      log.info("Performing Loma Linda context override.");
      principals.put(
          "605",
          maybeLomaLinda
              .contextOverride(config.getLomaLindaHackContext())
              .applicationProxyUser(null));
    }
    return principals;
  }

  /** Make a request using a full RPC Request. */
  @SneakyThrows
  public RpcResponse makeRequest(RpcRequest rpcRequest) {
    RequestEntity<RpcRequest> request = buildRequestEntity(rpcRequest);
    ResponseEntity<RpcResponse> response = restTemplate.exchange(request, RpcResponse.class);
    verifyVistalinkApiResponse(response);
    return response.getBody();
  }

  /** Request an RPC based on a patients ICN. */
  @Override
  public RpcResponse requestForTarget(
      RpcVistaTargets target, TypeSafeRpcRequest rpcRequestDetails) {
    RpcRequest rpcRequest =
        RpcRequest.builder()
            .principal(RpcPrincipal.builder().accessCode("not-used").verifyCode("not-used").build())
            .siteSpecificPrincipals(getPrincipals(rpcRequestDetails))
            .target(target)
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
