package gov.va.api.health.vistafhirquery.mockservices;

import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.joining;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;

import com.google.common.io.Resources;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.vistalink.api.RpcDetails;
import gov.va.api.lighthouse.vistalink.api.RpcRequest;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.mockserver.client.MockServerClient;
import org.mockserver.matchers.MatchType;
import org.mockserver.mockserver.MockServer;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MockServices {
  private static final int PORT = 8096;

  private final List<String> supportedQueries = new ArrayList<>();

  private final List<Consumer<MockServerClient>> supportedRequests =
      List.of(this::ping, this::help);

  private MockServer mockServer;

  @SneakyThrows
  private HttpRequest addRpcQueryWithExpectedRpcDetails(RpcDetails rpcDetails) {
    var path = "/rpc";
    log.info("Support Query [POST]: http://localhost:{}{}", PORT, path);
    URL url = new URL("http://localhost" + path);
    String body =
        JacksonConfig.createMapper()
            .writeValueAsString(RpcRequest.builder().rpc(rpcDetails).build());
    log.info("With RPC Details like: {}", body);
    supportedQueries.add(
        "[POST] http://localhost:" + PORT + path + " with RPC Details like " + body);
    return request()
        .withMethod("POST")
        .withPath(url.getPath())
        .withHeader(contentTypeApplicationJson())
        .withBody(json(body, StandardCharsets.UTF_8, MatchType.ONLY_MATCHING_FIELDS));
  }

  @SneakyThrows
  private String contentOfFile(String resource) {
    log.info("Respond with: {}", resource);
    return Resources.toString(getClass().getResource(resource), StandardCharsets.UTF_8);
  }

  private Header contentTypeApplicationJson() {
    return new Header("Content-Type", "application/json");
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
    mock.when(
            addRpcQueryWithExpectedRpcDetails(
                RpcDetails.builder()
                    .name("XOBV TEST PING")
                    .context("XOBV VISTALINK TESTER")
                    .build()))
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
    supportedRequests.forEach(svc -> svc.accept(mock));
  }
}
