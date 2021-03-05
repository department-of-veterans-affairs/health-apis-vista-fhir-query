package gov.va.api.health.vistafhirquery.service.controller.observation;

import static gov.va.api.health.vistafhirquery.service.controller.observation.ValueQuantityMapping.VitalSignsValueQuantityMapping.FhirVitalSignsProfile.BLOOD_PRESSURE;
import static gov.va.api.health.vistafhirquery.service.controller.observation.ValueQuantityMapping.VitalSignsValueQuantityMapping.FhirVitalSignsProfile.BODY_HEIGHT;
import static gov.va.api.health.vistafhirquery.service.controller.observation.ValueQuantityMapping.VitalSignsValueQuantityMapping.FhirVitalSignsProfile.BODY_MASS_INDEX;
import static gov.va.api.health.vistafhirquery.service.controller.observation.ValueQuantityMapping.VitalSignsValueQuantityMapping.FhirVitalSignsProfile.BODY_TEMPERATURE;
import static gov.va.api.health.vistafhirquery.service.controller.observation.ValueQuantityMapping.VitalSignsValueQuantityMapping.FhirVitalSignsProfile.BODY_WEIGHT;
import static gov.va.api.health.vistafhirquery.service.controller.observation.ValueQuantityMapping.VitalSignsValueQuantityMapping.FhirVitalSignsProfile.DIASTOLIC_BP;
import static gov.va.api.health.vistafhirquery.service.controller.observation.ValueQuantityMapping.VitalSignsValueQuantityMapping.FhirVitalSignsProfile.HEAD_CIRCUMFERENCE;
import static gov.va.api.health.vistafhirquery.service.controller.observation.ValueQuantityMapping.VitalSignsValueQuantityMapping.FhirVitalSignsProfile.HEART_RATE;
import static gov.va.api.health.vistafhirquery.service.controller.observation.ValueQuantityMapping.VitalSignsValueQuantityMapping.FhirVitalSignsProfile.OXYGEN_SATURATION;
import static gov.va.api.health.vistafhirquery.service.controller.observation.ValueQuantityMapping.VitalSignsValueQuantityMapping.FhirVitalSignsProfile.RESPIRATORY_RATE;
import static gov.va.api.health.vistafhirquery.service.controller.observation.ValueQuantityMapping.VitalSignsValueQuantityMapping.FhirVitalSignsProfile.SYSTOLIC_BP;
import static gov.va.api.health.vistafhirquery.service.controller.observation.ValueQuantityMapping.VitalSignsValueQuantityMapping.FhirVitalSignsProfile.VITAL_SIGNS_PANEL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import gov.va.api.health.r4.api.datatypes.Quantity;
import java.math.BigDecimal;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

public class ValueQuantityMappingTest {
  static Stream<Arguments> findByLoincCode() {
    return Stream.of(
        arguments(VITAL_SIGNS_PANEL, "85353-1"),
        arguments(RESPIRATORY_RATE, "9279-1"),
        arguments(HEART_RATE, "8867-4"),
        arguments(OXYGEN_SATURATION, "2708-6"),
        arguments(BODY_TEMPERATURE, "8310-5"),
        arguments(BODY_HEIGHT, "8302-2"),
        arguments(HEAD_CIRCUMFERENCE, "9843-4"),
        arguments(BODY_WEIGHT, "29463-7"),
        arguments(BODY_MASS_INDEX, "39156-5"),
        arguments(BLOOD_PRESSURE, "85354-9"),
        arguments(SYSTOLIC_BP, "8480-6"),
        arguments(DIASTOLIC_BP, "8462-4"));
  }

  private static Quantity generalQuantity(String value, String unit) {
    return Quantity.builder().value(new BigDecimal(value)).unit(unit).build();
  }

  static Stream<Arguments> toQuantity() {
    // ValueQuantityMapping mapping, String value, String units, Quantity expected
    return Stream.of(
        arguments(new ValueQuantityMapping.GeneralValueQuantityMapping(), null, null, null),
        arguments(new ValueQuantityMapping.GeneralValueQuantityMapping(), null, "a", null),
        arguments(
            new ValueQuantityMapping.GeneralValueQuantityMapping(),
            "1",
            null,
            generalQuantity("1", null)),
        arguments(
            new ValueQuantityMapping.GeneralValueQuantityMapping(),
            "1",
            "a",
            generalQuantity("1", "a")),
        arguments(
            new ValueQuantityMapping.VitalSignsValueQuantityMapping(RESPIRATORY_RATE),
            "1",
            "a",
            vitalsQuantity("1", "a", "/min")),
        arguments(
            new ValueQuantityMapping.VitalSignsValueQuantityMapping(HEART_RATE),
            "1",
            "a",
            vitalsQuantity("1", "a", "/min")),
        arguments(
            new ValueQuantityMapping.VitalSignsValueQuantityMapping(OXYGEN_SATURATION),
            "1",
            "a",
            vitalsQuantity("1", "a", "%")),
        arguments(
            new ValueQuantityMapping.VitalSignsValueQuantityMapping(BODY_TEMPERATURE),
            "1",
            "F",
            vitalsQuantity("1", "F", "[degF]")),
        arguments(
            new ValueQuantityMapping.VitalSignsValueQuantityMapping(BODY_HEIGHT),
            "1",
            "in",
            vitalsQuantity("1", "in", "[in_i]")),
        arguments(
            new ValueQuantityMapping.VitalSignsValueQuantityMapping(HEAD_CIRCUMFERENCE),
            "1",
            "in",
            vitalsQuantity("1", "in", "[in_i]")),
        arguments(
            new ValueQuantityMapping.VitalSignsValueQuantityMapping(BODY_WEIGHT),
            "1",
            "lb",
            vitalsQuantity("1", "lb", "[lb_av]")),
        arguments(
            new ValueQuantityMapping.VitalSignsValueQuantityMapping(BODY_MASS_INDEX),
            "1",
            "a",
            vitalsQuantity("1", "a", "kg/m2")),
        arguments(
            new ValueQuantityMapping.VitalSignsValueQuantityMapping(SYSTOLIC_BP),
            "1",
            "a",
            vitalsQuantity("1", "a", "mm[Hg]")),
        arguments(
            new ValueQuantityMapping.VitalSignsValueQuantityMapping(DIASTOLIC_BP),
            "1",
            "a",
            vitalsQuantity("1", "a", "mm[Hg]")),
        arguments(
            new ValueQuantityMapping.VitalSignsValueQuantityMapping(RESPIRATORY_RATE),
            "1",
            null,
            vitalsQuantity("1", null, "/min")),
        arguments(
            new ValueQuantityMapping.VitalSignsValueQuantityMapping(RESPIRATORY_RATE),
            null,
            "a",
            null),
        arguments(
            new ValueQuantityMapping.VitalSignsValueQuantityMapping(RESPIRATORY_RATE),
            null,
            null,
            null));
  }

  private static Quantity vitalsQuantity(String value, String unit, String code) {
    return Quantity.builder()
        .system("http://unitsofmeasure.org")
        .code(code)
        .value(new BigDecimal(value))
        .unit(unit)
        .build();
  }

  @ParameterizedTest
  @MethodSource
  void findByLoincCode(
      ValueQuantityMapping.VitalSignsValueQuantityMapping.FhirVitalSignsProfile profile,
      String loinc) {
    assertThat(
            ValueQuantityMapping.VitalSignsValueQuantityMapping.FhirVitalSignsProfile
                .findByLoincCode(loinc)
                .orElse(null))
        .isEqualTo(profile);
  }

  @ParameterizedTest
  @EnumSource(
      value = ValueQuantityMapping.VitalSignsValueQuantityMapping.FhirVitalSignsProfile.class,
      names = {"BODY_TEMPERATURE", "BODY_HEIGHT", "BODY_WEIGHT"})
  void illegalArgumentEnumUnits(
      ValueQuantityMapping.VitalSignsValueQuantityMapping.FhirVitalSignsProfile profile) {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () ->
                new ValueQuantityMapping.VitalSignsValueQuantityMapping(profile)
                    .toQuantity("1", "NAH"));
  }

  @ParameterizedTest
  @EnumSource(
      value = ValueQuantityMapping.VitalSignsValueQuantityMapping.FhirVitalSignsProfile.class,
      names = {"BLOOD_PRESSURE", "VITAL_SIGNS_PANEL"})
  void illegalArgumentProfileCantCreateQuantity(
      ValueQuantityMapping.VitalSignsValueQuantityMapping.FhirVitalSignsProfile profile) {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () ->
                new ValueQuantityMapping.VitalSignsValueQuantityMapping(profile)
                    .toQuantity("1", "lbs"));
  }

  @ParameterizedTest
  @MethodSource
  void toQuantity(ValueQuantityMapping mapping, String value, String units, Quantity expected) {
    assertThat(mapping.toQuantity(value, units)).isEqualTo(expected);
  }
}
