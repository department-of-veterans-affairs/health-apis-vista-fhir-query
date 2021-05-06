package gov.va.api.health.vistafhirquery.mockservices;

import static gov.va.api.health.vistafhirquery.mockservices.MockServiceRequests.contentTypeApplicationJson;
import static gov.va.api.health.vistafhirquery.mockservices.MockServiceRequests.json;
import static gov.va.api.health.vistafhirquery.mockservices.MockServiceRequests.rpcQueryWithExpectedRpcDetails;
import static org.mockserver.model.HttpResponse.response;

import com.google.common.io.Resources;
import gov.va.api.lighthouse.charon.api.RpcDetails;
import gov.va.api.lighthouse.charon.api.RpcInvocationResult;
import gov.va.api.lighthouse.charon.api.RpcResponse;
import gov.va.api.lighthouse.charon.models.vprgetpatientdata.VprGetPatientData;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.mockserver.client.MockServerClient;

@Data
@Slf4j
@RequiredArgsConstructor(staticName = "using")
public class VistaObservationMocks implements MockService {
  private final int port;

  private List<String> supportedQueries = new ArrayList<>();

  private List<Consumer<MockServerClient>> supportedRequests =
      List.of(this::observationReadVitals, this::observationReadLabs, this::observationSearch);

  private void addSupportedQuery(RpcDetails body) {
    supportedQueries.add(
        "[POST] http://localhost:" + port() + "/rpc with RPC Details like " + json(body));
  }

  @SneakyThrows
  private String contentOfFile(String resource) {
    log.info("Respond with: {}", resource);
    return Resources.toString(getClass().getResource(resource), StandardCharsets.UTF_8);
  }

  void observationReadLabs(MockServerClient mock) {
    var body =
        VprGetPatientData.Request.builder()
            .context(Optional.of("MOCKSERVICES"))
            .dfn(VprGetPatientData.Request.PatientId.forIcn("5000000347"))
            .type(Set.of(VprGetPatientData.Domains.labs))
            .id(Optional.of("CH;6919171.919997;14"))
            .build()
            .asDetails();
    addSupportedQuery(body);
    mock.when(rpcQueryWithExpectedRpcDetails(port(), body))
        .respond(
            response()
                .withStatusCode(200)
                .withHeader(contentTypeApplicationJson())
                .withBody(
                    rpcResponseOkWithContent(
                        "/vistalinkapi-vprgetpatientdata-read-labs-response.xml")));
  }

  void observationReadVitals(MockServerClient mock) {
    var body =
        VprGetPatientData.Request.builder()
            .context(Optional.of("MOCKSERVICES"))
            .dfn(VprGetPatientData.Request.PatientId.forIcn("1011537977V693883"))
            .type(Set.of(VprGetPatientData.Domains.vitals))
            .id(Optional.of("32071"))
            .build()
            .asDetails();
    addSupportedQuery(body);
    mock.when(rpcQueryWithExpectedRpcDetails(port(), body))
        .respond(
            response()
                .withStatusCode(200)
                .withHeader(contentTypeApplicationJson())
                .withBody(
                    rpcResponseOkWithContent(
                        "/vistalinkapi-vprgetpatientdata-read-vitals-response.xml")));
  }

  void observationSearch(MockServerClient mock) {
    supportedQueries.add("[POST] http://localhost:" + port() + "/rpc with _any_ RPC Details");
    mock.when(rpcQueryWithExpectedRpcDetails(port(), null))
        .respond(
            response()
                .withStatusCode(200)
                .withHeader(contentTypeApplicationJson())
                .withBody(
                    rpcResponseOkWithContent(
                        "/vistalinkapi-vprgetpatientdata-searchresponse.xml")));
  }

  private String rpcResponseOkWithContent(String rpcResponseFile) {
    return json(
        RpcResponse.builder()
            .status(RpcResponse.Status.OK)
            .result(
                RpcInvocationResult.builder()
                    .vista("673")
                    .response(contentOfFile(rpcResponseFile))
                    .build()));
  }
}
