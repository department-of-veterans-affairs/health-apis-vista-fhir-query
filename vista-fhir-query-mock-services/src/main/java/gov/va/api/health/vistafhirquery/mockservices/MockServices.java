package gov.va.api.health.vistafhirquery.mockservices;

import static com.google.common.base.Preconditions.checkState;
import static gov.va.api.health.vistafhirquery.mockservices.MockServiceRequests.contentTypeApplicationJson;
import static gov.va.api.health.vistafhirquery.mockservices.MockServiceRequests.json;
import static gov.va.api.health.vistafhirquery.mockservices.MockServiceRequests.rpcQueryWithExpectedRpcDetails;
import static java.util.stream.Collectors.joining;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import com.google.common.io.Resources;
import gov.va.api.lighthouse.charon.api.RpcDetails;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.mockserver.client.MockServerClient;
import org.mockserver.mockserver.MockServer;
import org.mockserver.model.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MockServices {
  private static final int PORT = 8096;

  private final List<String> supportedQueries = new ArrayList<>();

  private final List<MockService> supportedMocks = List.of(VistaObservationMocks.using(PORT));

  private MockServer mockServer;

  @SneakyThrows
  private String contentOfFile(String resource) {
    log.info("Respond with: {}", resource);
    return Resources.toString(getClass().getResource(resource), StandardCharsets.UTF_8);
  }

  private void help(MockServerClient mock) {
    log.info("Support Query [GET]: {}:{}{}", "http://localhost", PORT, "/help");
    mock.when(request().withPath("/help"))
        .respond(
            response()
                .withStatusCode(200)
                .withHeader(new Header("Content-Type", "text/plain"))
                .withBody(supportedQueries.stream().sorted().collect(joining("\n"))));
    log.info("List of supported queries available at http://localhost:{}/help", PORT);
  }

  private void ping(MockServerClient mock) {
    var body = RpcDetails.builder().name("XOBV TEST PING").context("XOBV VISTALINK TESTER").build();
    supportedQueries.add(
        "[POST] http://localhost:" + PORT + "/rpc with RPC Details like " + json(body));
    mock.when(rpcQueryWithExpectedRpcDetails(PORT, body))
        .respond(
            response()
                .withStatusCode(200)
                .withHeader(contentTypeApplicationJson())
                .withBody(contentOfFile("/vistalinkapi-ping-response-success.json")));
  }

  /** Populate Mock Server Endpoints. */
  public void start() {
    checkState(mockServer == null, "Mock Services have already been started.");
    log.info("Starting mock services on port {}", PORT);
    mockServer = new MockServer(PORT);
    MockServerClient mock = new MockServerClient("localhost", PORT);
    ping(mock);
    supportedMocks.stream()
        .flatMap(m -> m.supportedRequests().stream())
        .forEach(svc -> svc.accept(mock));
    supportedMocks.stream()
        .flatMap(m -> m.supportedQueries().stream())
        .forEach(supportedQueries::add);
    help(mock);
  }
}
