package gov.va.api.health.vistafhirquery.mockservices;

import java.util.List;
import java.util.function.Consumer;
import org.mockserver.client.MockServerClient;

/** MockService. */
public interface MockService {
  int port();

  List<String> supportedQueries();

  List<Consumer<MockServerClient>> supportedRequests();
}
