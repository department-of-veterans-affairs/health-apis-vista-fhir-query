package gov.va.api.health.vistafhirquery.service.controller;

import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.allBlank;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.ifPresent;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.isBlank;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.toBigDecimal;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.toHumanDateTime;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.toIso8601;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.toNewYorkFilemanDateString;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.valueOfValueOnlyXmlAttribute;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import gov.va.api.lighthouse.vistalink.models.FilemanDate;
import gov.va.api.lighthouse.vistalink.models.ValueOnlyXmlAttribute;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class R4TransformersTest {
  static Stream<Arguments> blankAll() {
    return Stream.of(
        Arguments.of(List.of("a", 1, List.of()), false),
        Arguments.of(List.of(Optional.empty(), List.of(), "", Map.of(), " "), true),
        Arguments.of(List.of("a", 1, 1.2, true), false),
        Arguments.of(List.of(Map.of(), "a"), false),
        Arguments.of(List.of("", Map.of(), 1), false));
  }

  static Stream<Arguments> blankOne() {
    return Stream.of(
        Arguments.of(null, true),
        Arguments.of("", true),
        Arguments.of("abc", false),
        Arguments.of(List.of(), true),
        Arguments.of(List.of(""), false),
        Arguments.of(Optional.empty(), true),
        Arguments.of(Optional.of("abc"), false),
        Arguments.of(Map.of(), true),
        Arguments.of(Map.of("abc", "123"), false),
        Arguments.of(1, false),
        Arguments.of(true, false),
        Arguments.of(1.23, false),
        Arguments.of(new BigDecimal("1.1"), false));
  }

  @Test
  void bigDecimal() {
    assertThat(toBigDecimal(null)).isNull();
    assertThat(toBigDecimal("")).isNull();
    assertThat(toBigDecimal("hello")).isNull();
    assertThat(toBigDecimal("0.")).isNull();
    assertThat(toBigDecimal(".0")).isNull();
    assertThat(toBigDecimal(".")).isNull();
    assertThat(toBigDecimal("0.0.0")).isNull();
    assertThat(toBigDecimal("1")).isEqualTo(new BigDecimal("1"));
    assertThat(toBigDecimal("1.1")).isEqualTo(new BigDecimal("1.1"));
  }

  @ParameterizedTest
  @MethodSource
  void blankAll(List<Object> objects, boolean expected) {
    assertThat(allBlank(objects.toArray())).isEqualTo(expected);
  }

  @ParameterizedTest
  @MethodSource
  void blankOne(Object value, boolean expected) {
    assertThat(isBlank(value)).isEqualTo(expected);
  }

  @Test
  void humanDateTime() {
    assertThat(toHumanDateTime(null)).isNull();
    assertThat(toHumanDateTime(ValueOnlyXmlAttribute.builder().build())).isNull();
    assertThat(toHumanDateTime(ValueOnlyXmlAttribute.of("2970919")))
        .isEqualTo("1997-09-19T00:00:00Z");
    assertThat(toHumanDateTime(ValueOnlyXmlAttribute.of("2970919.08")))
        .isEqualTo("1997-09-19T08:00:00Z");
    assertThat(toHumanDateTime(ValueOnlyXmlAttribute.of("2970919.0827")))
        .isEqualTo("1997-09-19T08:27:00Z");
    assertThat(toHumanDateTime(ValueOnlyXmlAttribute.of("2970919.082701")))
        .isEqualTo("1997-09-19T08:27:01Z");
    assertThatExceptionOfType(FilemanDate.BadFilemanDate.class)
        .isThrownBy(() -> toHumanDateTime(ValueOnlyXmlAttribute.of("29")));
    assertThatExceptionOfType(FilemanDate.BadFilemanDate.class)
        .isThrownBy(() -> toHumanDateTime(ValueOnlyXmlAttribute.of("abc")));
  }

  @Test
  void optionalFilemanDateString() {
    assertThat(toNewYorkFilemanDateString(null)).isEqualTo(Optional.empty());
    assertThat(toNewYorkFilemanDateString(Instant.ofEpochMilli(2000)))
        .isEqualTo(Optional.of("2691231.190002"));
    assertThat(toNewYorkFilemanDateString(Instant.parse("2006-01-01T00:00:00Z")))
        .isEqualTo(Optional.of("3051231.19"));
  }

  @Test
  void optionalIso8601String() {
    assertThat(toIso8601(null)).isEqualTo(Optional.empty());
    assertThat(toIso8601(Instant.ofEpochMilli(2000)))
        .isEqualTo(Optional.of(Instant.ofEpochMilli(2000).toString()));
    assertThat(toIso8601(Instant.parse("2006-01-01T00:00:00Z")))
        .isEqualTo(Optional.of("2006-01-01T00:00:00Z"));
  }

  @Test
  void present() {
    Function<Object, String> extract = (o) -> "x" + o;
    assertThat(ifPresent(null, extract)).isNull();
    assertThat(ifPresent("abc", extract)).isEqualTo("xabc");
  }

  @Test
  void valueOfValueOnlyXmlAttr() {
    assertThat(valueOfValueOnlyXmlAttribute(null)).isNull();
    assertThat(valueOfValueOnlyXmlAttribute(ValueOnlyXmlAttribute.builder().build())).isNull();
    assertThat(valueOfValueOnlyXmlAttribute(ValueOnlyXmlAttribute.builder().value("").build()))
        .isEqualTo("");
    assertThat(valueOfValueOnlyXmlAttribute(ValueOnlyXmlAttribute.builder().value("value").build()))
        .isEqualTo("value");
  }
}
