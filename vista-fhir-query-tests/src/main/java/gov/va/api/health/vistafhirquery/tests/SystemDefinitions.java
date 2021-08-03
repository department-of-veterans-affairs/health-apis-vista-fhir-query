package gov.va.api.health.vistafhirquery.tests;

import static gov.va.api.health.sentinel.SentinelProperties.magicAccessToken;

import gov.va.api.health.sentinel.Environment;
import gov.va.api.health.sentinel.ReducedSpamLogger;
import gov.va.api.health.sentinel.SentinelProperties;
import gov.va.api.health.sentinel.ServiceDefinition;
import gov.va.api.health.vistafhirquery.tests.TestIds.IcnAtSites;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import lombok.Synchronized;
import lombok.experimental.UtilityClass;
import org.slf4j.LoggerFactory;

/** System definitions that can be used by tests. */
@UtilityClass
public final class SystemDefinitions {
  private static final ReducedSpamLogger log =
      ReducedSpamLogger.builder().logger(LoggerFactory.getLogger(SentinelProperties.class)).build();

  private static boolean LOADED_SECRETS = false;

  private static Optional<String> clientKey() {
    return Optional.ofNullable(System.getProperty("client-key"));
  }

  private static TestIds idsForLocalEnvironment() {
    return TestIds.builder()
        .coverage("I3-4z05kjIPYEYQYLQmGck6nb")
        .organization("I3-450NAk1LKUAaaGqyCDA9S9")
        .observationLaboratory("I3-KqbQBRfPz2QzBYOB9MoX6iis0i7kCY2n5Zn5RQssOEMCd96dST7kj4")
        .observationVitalSign("I3-nfcGFM5JEsuzapkk9xecju3QH0MrK6tqZKVY9rd7wSk")
        .patient("1011537977V693883")
        .patientSites(icnAtSites())
        .build();
  }

  private static TestIds idsForProductionEnvironment() {
    return TestIds.builder()
        .coverage("TODO https://vajira.max.gov/browse/API-8891")
        .organization("TODO https://vajira.max.gov/browse/API-8891")
        .observationLaboratory("TODO https://vajira.max.gov/browse/API-8891")
        .observationVitalSign("TODO https://vajira.max.gov/browse/API-8891")
        .patient("1011537977V693883")
        .patientSites(icnAtSites())
        .build();
  }

  private static TestIds idsForSyntheticEnvironment() {
    return TestIds.builder()
        .coverage("TODO https://vajira.max.gov/browse/API-8891")
        .organization("TODO https://vajira.max.gov/browse/API-8891")
        .observationLaboratory("I3-IbkbEJ3pceqVRMjceHtk9zfkaWo5B2hFH018sws2KYPDg98RU2fFQC")
        .observationVitalSign("I3-MzfzyZkSpl9HvWWWuN0JvxF6V2f0fwrUm4Cj381IfxH")
        .patient("1011537977V693883")
        .patientSites(icnAtSites())
        .build();
  }

  private static SystemDefinition lab() {
    String url = "https://blue.lab.lighthouse.va.gov";
    return SystemDefinition.builder()
        .internal(serviceDefinition("internal", url, 443, null, "/vista-fhir-query/"))
        .r4(serviceDefinition("r4", url, 443, magicAccessToken(), "/vista-fhir-query/r4"))
        .publicIds(idsForSyntheticEnvironment())
        .clientKey(clientKey())
        .build();
  }

  /**
   * Thread safe: Load additional system properties from a secrets file, by default:
   * config/secrets.properties, but can be specified using the system property' secrets.properties.
   *
   * <p>If this file does not exist, a warning is logged and nothing is loaded.
   */
  @Synchronized
  public static void loadConfigSecretsProperties() {
    if (LOADED_SECRETS) {
      return;
    }
    String secrets = System.getProperty("secrets.properties", "config/secrets.properties");
    log.error("Attempting to load secrets from {}", secrets);
    Properties properties = new Properties();
    try (var in = new FileInputStream(secrets)) {
      properties.load(in);
      properties
          .stringPropertyNames()
          .forEach(p -> System.setProperty(p, properties.getProperty(p)));
      log.info("Loaded {} secrets", properties.stringPropertyNames().size());
    } catch (IOException e) {
      log.warn("No secrets loaded: {}", e.getMessage());
    }
    LOADED_SECRETS = true;
  }

  private static SystemDefinition local() {
    String url = "http://localhost";
    return SystemDefinition.builder()
        .internal(serviceDefinition("internal", url, 8095, null, "/"))
        .r4(serviceDefinition("r4", url, 8095, null, "/r4"))
        .publicIds(idsForLocalEnvironment())
        .clientKey(Optional.of(System.getProperty("client-key", "~shanktopus~")))
        .build();
  }

  private static SystemDefinition production() {
    String url = "https://blue.production.lighthouse.va.gov";
    return SystemDefinition.builder()
        .internal(serviceDefinition("internal", url, 443, null, "/vista-fhir-query/"))
        .r4(serviceDefinition("r4", url, 443, magicAccessToken(), "/vista-fhir-query/r4"))
        .publicIds(idsForProductionEnvironment())
        .clientKey(clientKey())
        .build();
  }

  private static SystemDefinition qa() {
    String url = "https://blue.qa.lighthouse.va.gov";
    return SystemDefinition.builder()
        .internal(serviceDefinition("internal", url, 443, null, "/vista-fhir-query/"))
        .r4(serviceDefinition("r4", url, 443, magicAccessToken(), "/vista-fhir-query/r4"))
        .publicIds(idsForSyntheticEnvironment())
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
        .publicIds(idsForProductionEnvironment())
        .clientKey(clientKey())
        .build();
  }

  private static SystemDefinition stagingLab() {
    String url = "https://blue.staging-lab.lighthouse.va.gov";
    return SystemDefinition.builder()
        .internal(serviceDefinition("internal", url, 443, null, "/vista-fhir-query/"))
        .r4(serviceDefinition("r4", url, 443, magicAccessToken(), "/vista-fhir-query/r4"))
        .publicIds(idsForSyntheticEnvironment())
        .clientKey(clientKey())
        .build();
  }

  /** Return the applicable system definition for the current environment. */
  public static SystemDefinition systemDefinition() {
    loadConfigSecretsProperties();
    switch (Environment.get()) {
      case PROD:
        return production();
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

  private List<IcnAtSites> icnAtSites() {
    String property = "vista-connectivity.icn-at-sites";
    var csv = System.getProperty(property, "1011537977V693883@673");
    log.infoOnce(
        "Using ICN at Sites {} (Override with -D{}=<icn@site,icn@site,...>)", csv, property);
    return IcnAtSites.csvOf(csv);
  }
}
