package gov.va.api.health.vistafhirquery.mockservices;

import static gov.va.api.health.vistafhirquery.mockservices.MockServiceRequests.contentTypeApplicationJson;
import static gov.va.api.health.vistafhirquery.mockservices.MockServiceRequests.json;
import static gov.va.api.health.vistafhirquery.mockservices.MockServiceRequests.rpcQueryWithExpectedRpcDetails;
import static gov.va.api.health.vistafhirquery.mockservices.MockServiceRequests.rpcResponseOkWithContent;
import static org.mockserver.model.HttpResponse.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.charon.api.RpcDetails;
import gov.va.api.lighthouse.charon.api.RpcDetails.Parameter;
import gov.va.api.lighthouse.charon.api.RpcRequest;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayGetsManifest;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

/** Mock requests/results from the Lighthouse RPC Gateway. */
@Data
@RequiredArgsConstructor(staticName = "using")
@Slf4j
public class LhsLighthouseRpcGatewayMocks implements MockService {
  private final int port;

  private List<String> supportedQueries = new ArrayList<>();

  private List<Consumer<MockServerClient>> supportedRequests = List.of(this::respondByFile);

  private void addSupportedQuery(RpcDetails body) {
    supportedQueries.add(
        "[POST] http://localhost:" + port() + "/rpc with RPC Details like " + json(body));
  }

  @SuppressWarnings("UnnecessaryParentheses")
  @SneakyThrows
  private HttpResponse chooseResponseBasedOnFile(HttpRequest request) {
    ObjectMapper mapper = JacksonConfig.createMapper();
    RpcRequest rpcRequest = mapper.readValue(request.getBodyAsString(), RpcRequest.class);
    log.info("PROCESSING RPC REQUEST: {}", rpcRequest);
    Parameter ap = rpcRequest.rpc().parameters().get(0);
    // Looking for param^FILE^literal^2.312
    var response =
        ap.array().stream()
            .filter(p -> p.startsWith("param^FILE^literal^"))
            .map(p -> p.replace("param^FILE^literal^", ""))
            // File 2 only works here because the request is for fields of subfile .312
            .map(
                file ->
                    switch (file) {
                      case "2", "2.312" -> "/lhslighthouserpcgateway/"
                          + "response-coverage-search-by-patient.json";
                      case "36" -> "/lhslighthouserpcgateway/response-organization-read.json";
                      default -> null;
                    })
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No response for: " + ap));
    return ok(response);
  }

  private HttpResponse ok(String resultsFile) {
    log.info("Responding with {}", resultsFile);
    return response()
        .withStatusCode(200)
        .withHeader(contentTypeApplicationJson())
        .withBody(rpcResponseOkWithContent(resultsFile));
  }

  void respondByFile(MockServerClient mock) {
    var details =
        RpcDetails.builder()
            .name(LhsLighthouseRpcGatewayGetsManifest.RPC_NAME)
            .context("LHS RPC CONTEXT")
            .build();
    addSupportedQuery(details);
    mock.when(rpcQueryWithExpectedRpcDetails(port(), details))
        .respond(this::chooseResponseBasedOnFile);
  }
}
