package gov.va.api.health.vistafhirquery.service.controller;

import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.allBlank;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.ifPresent;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.isBlank;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.toBigDecimal;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.toHumanDateTime;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.valueOfValueOnlyXmlAttribute;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.vistalink.models.ValueOnlyXmlAttribute;
import java.math.BigDecimal;
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
    assertThat(toHumanDateTime(ValueOnlyXmlAttribute.of("abc"))).isEqualTo("abc");
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
