package gov.va.api.health.vistafhirquery.mockservices;

import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.joining;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.Header;
import org.mockserver.netty.MockServer;
import org.springframework.stereotype.Component;

/** Defines mock server configuration. */
@Slf4j
@Component
public class MockServices {
  private static final int PORT = 8096;

  private final List<String> supportedQueries = new ArrayList<>();

  private final List<MockService> supportedMocks =
      List.of(LhsLighthouseRpcGatewayMocks.using(PORT), VprGetPatientDataMocks.using(PORT));

  private MockServer mockServer;

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

  /** Populate Mock Server Endpoints. */
  public void start() {
    checkState(mockServer == null, "Mock Services have already been started.");
    log.info("Starting mock services on port {}", PORT);
    mockServer = new MockServer(PORT);
    MockServerClient mock = new MockServerClient("localhost", PORT);
    supportedMocks.stream()
        .flatMap(m -> m.supportedRequests().stream())
        .forEach(svc -> svc.accept(mock));
    supportedMocks.stream()
        .flatMap(m -> m.supportedQueries().stream())
        .forEach(supportedQueries::add);
    help(mock);
  }
}
