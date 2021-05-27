package gov.va.api.health.vistafhirquery.mockservices;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.JsonBody.json;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.charon.api.RpcDetails;
import gov.va.api.lighthouse.charon.api.RpcRequest;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.mockserver.matchers.MatchType;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.JsonBody;

/** MockServiceRequests. */
@Slf4j
@UtilityClass
public class MockServiceRequests {
  public static Header contentTypeApplicationJson() {
    return new Header("Content-Type", "application/json");
  }

  @SneakyThrows
  public static String json(Object o) {
    return JacksonConfig.createMapper().writeValueAsString(o);
  }

  /** Create an HTTP Request for mocking a Vistalink API /rpc endpoint. */
  @SneakyThrows
  public static HttpRequest rpcQueryWithExpectedRpcDetails(int port, RpcDetails rpcDetails) {
    var path = "/rpc";
    log.info("Support Query [POST]: http://localhost:{}{}", port, path);
    URL url = new URL("http://localhost" + path);
    var request =
        request()
            .withMethod("POST")
            .withPath(url.getPath())
            .withHeader(contentTypeApplicationJson());
    if (rpcDetails != null) {
      String body = json(RpcRequest.builder().rpc(rpcDetails).build());
      log.info("With RPC Details like: {}", body);
      request =
          request.withBody(
              JsonBody.json(body, StandardCharsets.UTF_8, MatchType.ONLY_MATCHING_FIELDS));
    }
    return request;
  }
}
