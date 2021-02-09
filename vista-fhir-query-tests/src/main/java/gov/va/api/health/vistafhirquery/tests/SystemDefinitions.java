package gov.va.api.health.vistafhirquery.tests;

import gov.va.api.health.sentinel.Environment;
import gov.va.api.health.sentinel.SentinelProperties;
import gov.va.api.health.sentinel.ServiceDefinition;
import java.util.Optional;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class SystemDefinitions {
  private static Optional<String> clientKey() {
    return Optional.ofNullable(System.getProperty("client-key"));
  }

  private static SystemDefinition local() {
    String url = "http://localhost";
    return SystemDefinition.builder()
        .internal(serviceDefinition("internal", url, 8095, null, "/"))
        .publicIds(localIds())
        .clientKey(Optional.of(System.getProperty("client-key", "~shanktopus~")))
        .build();
  }

  private static TestIds localIds() {
    return TestIds.builder()
        .patient("1011537977V693883")
        .observation("N1011537977V693883+ms+32463")
        .build();
  }

  private static SystemDefinition qa() {
    String url = "https://blue.qa.lighthouse.va.gov";
    return SystemDefinition.builder()
        .internal(serviceDefinition("internal", url, 443, null, "/vista-fhir-query/"))
        .publicIds(qaIds())
        .clientKey(clientKey())
        .build();
  }

  private static TestIds qaIds() {
    return TestIds.builder()
        .patient("1011537977V693883")
        .observation("N5000000349+358+32463")
        .build();
  }

  private static ServiceDefinition serviceDefinition(
      String name, String url, int port, String accessToken, String apiPath) {
    return SentinelProperties.forName(name)
        .accessToken(() -> Optional.ofNullable(accessToken))
        .defaultUrl(url)
        .defaultPort(port)
        .defaultApiPath(apiPath)
        .defaultUrl(url)
        .build()
        .serviceDefinition();
  }

  private static SystemDefinition staging() {
    String url = "https://blue.qa.lighthouse.va.gov";
    return SystemDefinition.builder()
        .internal(serviceDefinition("internal", url, 443, null, "/vista-fhir-query/"))
        .publicIds(stagingIds())
        .clientKey(clientKey())
        .build();
  }

  private static TestIds stagingIds() {
    return TestIds.builder()
        .patient("1011537977V693883")
        .observation("N5000000349+358+32463")
        .build();
  }

  /** Return the applicable system definition for the current environment. */
  public static SystemDefinition systemDefinition() {
    switch (Environment.get()) {
      case LOCAL:
        return local();
      case QA:
        return qa();
      case STAGING:
        return staging();
      default:
        throw new IllegalArgumentException("Unknown sentinel environment: " + Environment.get());
    }
  }
}
