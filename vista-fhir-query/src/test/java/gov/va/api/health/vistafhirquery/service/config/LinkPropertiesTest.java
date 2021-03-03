package gov.va.api.health.vistafhirquery.service.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import gov.va.api.health.r4.api.resources.Condition;
import gov.va.api.health.r4.api.resources.Immunization;
import gov.va.api.health.r4.api.resources.Patient;
import gov.va.api.health.r4.api.resources.Resource;
import gov.va.api.health.vistafhirquery.service.config.LinkProperties.Links;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class LinkPropertiesTest {

  static Stream<Arguments> resourceUrl() {
    return Stream.of(
        arguments(Patient.builder().id("p1").build(), "http://custom.com/foo/Patient/p1"),
        arguments(
            Immunization.builder().id("i1").build(), "http://also-custom.com/bar/Immunization/i1"),
        arguments(Condition.builder().id("c1").build(), "http://default.com/r4/Condition/c1"));
  }

  @Test
  void customR4UrlDefaultsToEmpty() {
    assertThat(LinkProperties.builder().build().getCustomR4UrlAndPath()).isEmpty();
  }

  Links links() {
    return LinkProperties.builder()
        .publicUrl("http://default.com")
        .publicR4BasePath("r4")
        .customR4UrlAndPath(
            Map.of(
                "Patient", "http://custom.com/foo",
                "Immunization", "http://also-custom.com/bar"))
        .build()
        .r4();
  }

  @ParameterizedTest
  @MethodSource
  void resourceUrl(Resource resource, String expectedUrl) {
    assertThat(links().readUrl(resource)).isEqualTo(expectedUrl);
  }
}
