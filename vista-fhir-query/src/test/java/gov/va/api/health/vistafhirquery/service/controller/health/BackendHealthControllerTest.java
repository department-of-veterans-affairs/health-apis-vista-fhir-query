package gov.va.api.health.vistafhirquery.service.controller.health;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class BackendHealthControllerTest {

  @Mock RestTemplate restTemplate;

  private static Stream<Arguments> expectedStatus() {
    return Stream.of(
        arguments(
            List.of(BackendService.mock("http://charon", HttpStatus.OK)),
            Status.UP,
            List.of(BackendStatus.expect("charon", Status.UP))),
        arguments(
            List.of(BackendService.mock("http://charon", HttpStatus.I_AM_A_TEAPOT)),
            Status.DOWN,
            List.of(BackendStatus.expect("charon", Status.DOWN))));
  }

  private BackendHealthController _controller() {
    return new BackendHealthController(restTemplate, "http://charon.app/health");
  }

  @ParameterizedTest
  @MethodSource
  void expectedStatus(
      List<BackendService> backends, Status overallStatus, List<BackendStatus> backendStatus) {
    backends.forEach(
        backend ->
            when(restTemplate.exchange(
                    startsWith(backend.urlStartsWith),
                    eq(HttpMethod.GET),
                    any(HttpEntity.class),
                    eq(String.class)))
                .thenReturn(backend.respond()));
    var response = _controller().collectBackendHealth().getBody();
    assertThat(response.getStatus()).isEqualTo(overallStatus);
    var backendServices =
        ((List<Health>) response.getDetails().get("backendServices"))
            .stream()
                .collect(toMap(health -> health.getStatus().getDescription(), Health::getStatus));
    backendStatus.forEach(
        backend -> {
          assertThat(backendServices.get(backend.name()).getCode())
              .isEqualTo(backend.status().getCode());
        });
  }

  @Value
  @AllArgsConstructor(staticName = "mock")
  private static class BackendService {
    String urlStartsWith;
    HttpStatus response;

    ResponseEntity<String> respond() {
      return new ResponseEntity<String>(response());
    }
  }

  @Value
  @AllArgsConstructor(staticName = "expect")
  private static class BackendStatus {
    String name;
    Status status;
  }
}
