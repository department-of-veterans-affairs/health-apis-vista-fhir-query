package gov.va.api.health.vistafhirquery.service.controller;

import static gov.va.api.health.vistafhirquery.service.controller.RpcGatewayTransformers.internalValueAsIntegerOrDie;
import static gov.va.api.health.vistafhirquery.service.controller.RpcGatewayTransformers.isInternalValueBlank;
import static gov.va.api.health.vistafhirquery.service.controller.RpcGatewayTransformers.isInternalValueNotBlank;
import static gov.va.api.health.vistafhirquery.service.controller.RpcGatewayTransformers.yesNoToBoolean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import gov.va.api.health.vistafhirquery.service.controller.RpcGatewayTransformers.UnexpectedVistaValue;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayResponse.Values;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RpcGatewayTransformersTest {
  public static Stream<Arguments> blankInternalValue() {
    return Stream.of(
        arguments(null, true),
        arguments(Values.of("x", null), true),
        arguments(Values.of("x", ""), true),
        arguments(Values.of("x", " "), true),
        arguments(Values.of("x", "x"), false)
        //
        );
  }

  @ParameterizedTest
  @MethodSource
  void blankInternalValue(Values value, boolean expectedIsBlank) {
    assertThat(isInternalValueBlank(value)).isEqualTo(expectedIsBlank);
    assertThat(isInternalValueNotBlank(value)).isEqualTo(!expectedIsBlank);
  }

  @Test
  void internalValueAsIntegerOrDieReturnsOrThrows() {
    assertThat(internalValueAsIntegerOrDie(Values.of("a", "123"), "x")).isEqualTo(123);
    assertThatExceptionOfType(UnexpectedVistaValue.class)
        .isThrownBy(() -> internalValueAsIntegerOrDie(Values.of("a", ""), "x"));
    assertThatExceptionOfType(UnexpectedVistaValue.class)
        .isThrownBy(() -> internalValueAsIntegerOrDie(Values.of("a", null), "x"));
    assertThatExceptionOfType(UnexpectedVistaValue.class)
        .isThrownBy(() -> internalValueAsIntegerOrDie(null, "x"));
    assertThatExceptionOfType(UnexpectedVistaValue.class)
        .isThrownBy(() -> internalValueAsIntegerOrDie(Values.of("a", "a"), "x"));
  }

  @Test
  void yesNoToBooleanValues() {
    assertThat(yesNoToBoolean("0")).isFalse();
    assertThat(yesNoToBoolean("1")).isTrue();
    assertThatExceptionOfType(UnexpectedVistaValue.class).isThrownBy(() -> yesNoToBoolean("true"));
    assertThatExceptionOfType(UnexpectedVistaValue.class).isThrownBy(() -> yesNoToBoolean(""));
    assertThatExceptionOfType(UnexpectedVistaValue.class).isThrownBy(() -> yesNoToBoolean(null));
  }
}
