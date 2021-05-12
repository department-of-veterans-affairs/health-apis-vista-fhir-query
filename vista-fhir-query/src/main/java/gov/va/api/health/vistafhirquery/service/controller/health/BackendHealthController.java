package gov.va.api.health.vistafhirquery.service.controller.health;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
@RequestMapping(
    value = "/backend/health",
    produces = {"application/json"})
public class BackendHealthController {
  private final AtomicBoolean hasCachedBackendHealth = new AtomicBoolean(false);

  private final String charonHealthCheckUrl;

  private final RestTemplate restTemplate;

  BackendHealthController(
      @Autowired RestTemplate restTemplate,
      @Value("${backend-health.charon.health-check-url}") String charonHealthCheckUrl) {
    this.restTemplate = restTemplate;
    this.charonHealthCheckUrl = removeTrailingSlash(charonHealthCheckUrl);
  }

  /** Clear cached resources. */
  @Scheduled(cron = "0 */5 * * * *")
  @CacheEvict(value = "backend-health")
  public void clearBackendHealthCache() {
    if (hasCachedBackendHealth.getAndSet(false)) {
      log.info("Clearing cache: backend-health");
    }
  }

  /** Get Backend Health. */
  @GetMapping
  @Cacheable("backend-health")
  public ResponseEntity<Health> collectBackendHealth() {
    Instant now = Instant.now();
    List<Health> backendHealths = List.of(testHealth("charon", charonHealthCheckUrl, now));
    Status status =
        backendHealths.stream().anyMatch(h -> h.getStatus().equals(Status.DOWN))
            ? Status.DOWN
            : Status.UP;
    Health overallHealth =
        Health.status(new Status(status.getCode(), "Vista-Fhir-Query Backend Health Check"))
            .withDetail("time", now)
            .withDetail("backendServices", backendHealths)
            .build();
    hasCachedBackendHealth.set(true);
    if (status.equals(Status.DOWN)) {
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(overallHealth);
    }
    return ResponseEntity.status(HttpStatus.OK).body(overallHealth);
  }

  private String removeTrailingSlash(String url) {
    return url.endsWith("/") ? url.substring(0, url.lastIndexOf("/")) : url;
  }

  @SneakyThrows
  private Health testHealth(String name, String fullUrl, Instant now) {
    HttpStatus httpCode;
    try {
      ResponseEntity<String> response =
          restTemplate.exchange(fullUrl, HttpMethod.GET, HttpEntity.EMPTY, String.class);
      httpCode = response.getStatusCode();
    } catch (RestClientException e) {
      log.error("Failure occurred when getting {} health at {}: {}", name, fullUrl, e.getMessage());
      httpCode = HttpStatus.INTERNAL_SERVER_ERROR;
    }
    return Health.status(new Status(httpCode.is2xxSuccessful() ? "UP" : "DOWN", name))
        .withDetail("name", name)
        .withDetail("httpCode", httpCode.value())
        .withDetail("status", httpCode)
        .withDetail("time", now)
        .build();
  }
}
