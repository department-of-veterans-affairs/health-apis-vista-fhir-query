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

  private static SystemDefinition lab() {
    String url = "https://blue.lab.lighthouse.va.gov";
    return SystemDefinition.builder()
        .internal(serviceDefinition("internal", url, 443, null, "/vista-fhir-query/"))
        .r4(serviceDefinition("r4", url, 443, magicAccessToken(), "/vista-fhir-query/r4"))
        .publicIds(syntheticIds())
        .clientKey(clientKey())
        .build();
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
        .observationVitalSign("I3-j5wsEbInV30wlYgZeXfkDfHBlp5ogiUVdztpdekbjwk")
        .observationLaboratory("I3-Fz9SA8tzj9wZ539HlEqFoDkrsVnrJoWOms7WZ4DS26HwWKQg2Kd1BM")
        .build();
  }

  private static TestIds productionIds() {
    return TestIds.builder()
        .patient("1011537977V693883")
        .observationVitalSign("TBD")
        .observationLaboratory("TBD")
        .build();
  }

  private static SystemDefinition qa() {
    String url = "https://blue.qa.lighthouse.va.gov";
    return SystemDefinition.builder()
        .internal(serviceDefinition("internal", url, 443, null, "/vista-fhir-query/"))
        .r4(serviceDefinition("r4", url, 443, magicAccessToken(), "/vista-fhir-query/r4"))
        .publicIds(syntheticIds())
        .clientKey(clientKey())
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
    String url = "https://blue.staging.lighthouse.va.gov";
    return SystemDefinition.builder()
        .internal(serviceDefinition("internal", url, 443, null, "/vista-fhir-query/"))
        .r4(serviceDefinition("r4", url, 443, magicAccessToken(), "/vista-fhir-query/r4"))
        .publicIds(productionIds())
        .clientKey(clientKey())
        .build();
  }

  private static SystemDefinition stagingLab() {
    String url = "https://blue.staging-lab.lighthouse.va.gov";
    return SystemDefinition.builder()
        .internal(serviceDefinition("internal", url, 443, null, "/vista-fhir-query/"))
        .r4(serviceDefinition("r4", url, 443, magicAccessToken(), "/vista-fhir-query/r4"))
        .publicIds(syntheticIds())
        .clientKey(clientKey())
        .build();
  }

  private static TestIds syntheticIds() {
    return TestIds.builder()
        .patient("1011537977V693883")
        .observationVitalSign("I2-IBV5HN7B4CKF4XNL7GXDCWZNPDVDBRBBQ4OOHUANWIOEQQFGKHOQ0000")
        .observationLaboratory(
            "I2-IBV5HN7B4CKF4XNL7GXDCWZNPBFGQRKQYZGV65WQMWE5Q7UMWSVDFL737DUKXBMMYFXGARBJ4CVZ6000")
        .build();
  }

  /** Return the applicable system definition for the current environment. */
  public static SystemDefinition systemDefinition() {
    switch (Environment.get()) {
      case LAB:
        return lab();
      case LOCAL:
        return local();
      case QA:
        return qa();
      case STAGING:
        return staging();
      case STAGING_LAB:
        return stagingLab();
      default:
        throw new IllegalArgumentException("Unknown sentinel environment: " + Environment.get());
    }
  }
}
