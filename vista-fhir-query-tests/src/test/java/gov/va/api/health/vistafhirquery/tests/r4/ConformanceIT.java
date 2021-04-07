package gov.va.api.health.vistafhirquery.tests.r4;

import static org.junit.jupiter.params.provider.Arguments.arguments;

import gov.va.api.health.r4.api.information.WellKnown;
import gov.va.api.health.r4.api.resources.CapabilityStatement;
import gov.va.api.health.vistafhirquery.tests.SystemDefinitions;
import gov.va.api.health.vistafhirquery.tests.TestClients;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@Slf4j
public class ConformanceIT {

  static Stream<Arguments> conformance() {
    String apiPath = SystemDefinitions.systemDefinition().internal().apiPath();
    return Stream.of(
        arguments(apiPath + "r4/.well-known/smart-configuration", WellKnown.class),
        arguments(apiPath + "r4/metadata", CapabilityStatement.class));
  }

  @ParameterizedTest
  @MethodSource
  void conformance(String path, Class<?> expectedClassType) {
    log.info("Verify {} is {} ({})", path, expectedClassType.getSimpleName(), 200);
    TestClients.internal().get(path).expect(200).expectValid(expectedClassType);
  }
}
