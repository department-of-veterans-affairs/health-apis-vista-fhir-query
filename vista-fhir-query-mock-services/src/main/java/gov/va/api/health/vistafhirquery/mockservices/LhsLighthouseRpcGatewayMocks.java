package gov.va.api.health.vistafhirquery.mockservices;

import static gov.va.api.health.vistafhirquery.mockservices.MockServiceRequests.contentTypeApplicationJson;
import static gov.va.api.health.vistafhirquery.mockservices.MockServiceRequests.json;
import static gov.va.api.health.vistafhirquery.mockservices.MockServiceRequests.rpcQueryWithExpectedRpcDetails;
import static gov.va.api.health.vistafhirquery.mockservices.MockServiceRequests.rpcResponseOkWithContent;
import static org.mockserver.model.HttpResponse.response;

import gov.va.api.lighthouse.charon.api.RpcDetails;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayGetsManifest;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.mockserver.client.MockServerClient;

/** Mock requests/results from the Lighthouse RPC Gateway. */
@Data
@RequiredArgsConstructor(staticName = "using")
public class LhsLighthouseRpcGatewayMocks implements MockService {
  private final int port;

  private List<String> supportedQueries = new ArrayList<>();

  private List<Consumer<MockServerClient>> supportedRequests = List.of(this::readInsuranceTypeFile);

  private void addSupportedQuery(RpcDetails body) {
    supportedQueries.add(
        "[POST] http://localhost:" + port() + "/rpc with RPC Details like " + json(body));
  }

  void readInsuranceTypeFile(MockServerClient mock) {
    var details =
        RpcDetails.builder()
            .name(LhsLighthouseRpcGatewayGetsManifest.RPC_NAME)
            .context("LHS RPC CONTEXT")
            .build();
    addSupportedQuery(details);
    mock.when(rpcQueryWithExpectedRpcDetails(port(), details))
        .respond(
            response()
                .withStatusCode(200)
                .withHeader(contentTypeApplicationJson())
                .withBody(
                    rpcResponseOkWithContent("/charon-lhslighthouserpcgateway-getsresults.json")));
  }
}
