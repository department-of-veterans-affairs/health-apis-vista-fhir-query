package gov.va.api.health.vistafhirquery.mockservices;

import static gov.va.api.health.vistafhirquery.mockservices.MockServiceRequests.contentTypeApplicationJson;
import static gov.va.api.health.vistafhirquery.mockservices.MockServiceRequests.json;
import static gov.va.api.health.vistafhirquery.mockservices.MockServiceRequests.rpcQueryWithExpectedRpcDetails;
import static org.mockserver.model.HttpResponse.response;

import com.google.common.io.Resources;
import gov.va.api.lighthouse.vistalink.api.RpcDetails;
import gov.va.api.lighthouse.vistalink.models.vprgetpatientdata.VprGetPatientData;
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

  private List<Consumer<MockServerClient>> supportedRequests = List.of(this::observationRead);

  private void addSupportedQuery(RpcDetails body) {
    supportedQueries.add(
        "[POST] http://localhost:" + port() + "/rpc with RPC Details like " + json(body));
  }

  @SneakyThrows
  private String contentOfFile(String resource) {
    log.info("Respond with: {}", resource);
    return Resources.toString(getClass().getResource(resource), StandardCharsets.UTF_8);
  }

  void observationRead(MockServerClient mock) {
    var body =
        VprGetPatientData.Request.builder()
            .dfn(";1011537977V693883")
            .type(Set.of(VprGetPatientData.Domains.vitals))
            .max(Optional.of("1"))
            .id(Optional.of("32463"))
            .build()
            .asDetails();
    addSupportedQuery(body);
    mock.when(rpcQueryWithExpectedRpcDetails(port(), body))
        .respond(
            response()
                .withStatusCode(200)
                .withHeader(contentTypeApplicationJson())
                .withBody(contentOfFile("/vistalinkapi-vprgetpatientdata-readresponse.json")));
  }
}
