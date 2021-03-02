package gov.va.api.health.vistafhirquery.tests;

import static gov.va.api.health.sentinel.SentinelProperties.magicAccessToken;

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
        .r4(serviceDefinition("r4", url, 8095, null, "/r4"))
        .publicIds(localIds())
        .clientKey(Optional.of(System.getProperty("client-key", "~shanktopus~")))
        .build();
  }

  private static TestIds localIds() {
    return TestIds.builder()
        .patient("1011537977V693883")
        .observation(
            "I2-6FYK5ZV4ERKV6Q6R33FN7SQVQGAMBKJVSVI3I6UBSZ4SMKCA3I4YM3QX7EWV6TDY7PNZXQGS5BZHQ000")
        .build();
  }

  private static SystemDefinition qa() {
    String url = "https://blue.qa.lighthouse.va.gov";
    return SystemDefinition.builder()
        .internal(serviceDefinition("internal", url, 443, null, "/vista-fhir-query/"))
        .r4(serviceDefinition("r4", url, 443, magicAccessToken(), "/vista-fhir-query/r4"))
        .publicIds(qaIds())
        .clientKey(clientKey())
        .build();
  }

  private static TestIds qaIds() {
    return TestIds.builder()
        .patient("1011537977V693883")
        .observation("I2-IBV5HN7B4CKF4XNL7GXDCWZNPD2T3WDMVHZZSIIBWTK3PXVFONYQ0000")
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
        .r4(serviceDefinition("r4", url, 443, magicAccessToken(), "/vista-fhir-query/r4"))
        .publicIds(stagingIds())
        .clientKey(clientKey())
        .build();
  }

  private static TestIds stagingIds() {
    return qaIds();
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
