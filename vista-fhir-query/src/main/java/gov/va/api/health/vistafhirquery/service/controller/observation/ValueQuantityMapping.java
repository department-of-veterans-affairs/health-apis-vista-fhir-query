package gov.va.api.health.vistafhirquery.service.controller.observation;

import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.toBigDecimal;

import gov.va.api.health.r4.api.datatypes.Quantity;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

/** KBS approved Mappings of Value Quantities. */
public interface ValueQuantityMapping {

  Quantity toQuantity(String value, String unit);

  /** General Value quantity allows pass through of values and units without special mappings. */
  class GeneralValueQuantityMapping implements ValueQuantityMapping {
    @Override
    public Quantity toQuantity(String value, String unit) {
      if (value == null) {
        return null;
      }
      return Quantity.builder().value(toBigDecimal(value)).unit(unit).build();
    }
  }

  /** Value quantity mappings for Vital signs. */
  @AllArgsConstructor
  class VitalSignsValueQuantityMapping implements ValueQuantityMapping {

    @NonNull FhirVitalSignsProfile profile;

    @Override
    public Quantity toQuantity(String value, String unit) {
      if (value == null) {
        return null;
      }
      var quantity =
          Quantity.builder()
              .value(toBigDecimal(value))
              .unit(unit)
              .system("http://unitsofmeasure.org");
      switch (profile) {
        case RESPIRATORY_RATE:
          // fall-through
        case HEART_RATE:
          return quantity.code("/min").build();
        case OXYGEN_SATURATION:
          return quantity.code("%").build();
        case BODY_TEMPERATURE:
          BodyTemperatureUnits bodyTemp = BodyTemperatureUnits.findByVistaUnit(unit);
          return quantity.code(bodyTemp.code()).build();
        case BODY_HEIGHT:
          // fall-through
        case HEAD_CIRCUMFERENCE:
          BodyLengthUnits length = BodyLengthUnits.findByVistaUnit(unit);
          return quantity.code(length.code()).build();
        case BODY_WEIGHT:
          BodyWeightUnits weight = BodyWeightUnits.findByVistaUnit(unit);
          return quantity.code(weight.code()).build();
        case BODY_MASS_INDEX:
          return quantity.code("kg/m2").build();
        case SYSTOLIC_BP:
          // fall-through
        case DIASTOLIC_BP:
          return quantity.code("mm[Hg]").build();
        case BLOOD_PRESSURE:
          throw new IllegalArgumentException(
              "Blood pressure quantities should be "
                  + "mapped separately as systolic and diastolic.");
        case VITAL_SIGNS_PANEL:
          throw new IllegalArgumentException(
              "Vital-Sign panels should not include "
                  + "quantities themelves, only references to Observations that do.");
        default:
          throw new IllegalStateException("Unknown Vital-Signs profile: " + profile.toString());
      }
    }

    @AllArgsConstructor
    enum BodyLengthUnits {
      cm("cm", "cm"),
      in("[in_i]", "in_i");

      @Getter private final String code;

      @Getter private final String display;

      public static BodyLengthUnits findByVistaUnit(String vistaUnitString) {
        for (BodyLengthUnits units : BodyLengthUnits.values()) {
          if (units.name().equals(vistaUnitString)) {
            return units;
          }
        }
        throw new IllegalArgumentException(
            "Invalid VistA body-length unit representation: " + vistaUnitString);
      }
    }

    @AllArgsConstructor
    enum BodyTemperatureUnits {
      C("Cel", "Cel"),
      F("[degF]", "degF");

      @Getter private final String code;

      @Getter private final String display;

      public static BodyTemperatureUnits findByVistaUnit(String vistaUnitString) {
        for (BodyTemperatureUnits units : BodyTemperatureUnits.values()) {
          if (units.name().equals(vistaUnitString)) {
            return units;
          }
        }
        throw new IllegalArgumentException(
            "Invalid VistA body-temperature unit representation: " + vistaUnitString);
      }
    }

    @AllArgsConstructor
    enum BodyWeightUnits {
      g("g", "g"),
      kg("kg", "kg"),
      lb("[lb_av]", "lb_av");

      @Getter private final String code;

      @Getter private final String display;

      public static BodyWeightUnits findByVistaUnit(String vistaUnitString) {
        for (BodyWeightUnits unit : BodyWeightUnits.values()) {
          if (unit.name().equals(vistaUnitString)) {
            return unit;
          }
        }
        throw new IllegalArgumentException(
            "Invalid VistA body-weight unit representation: " + vistaUnitString);
      }
    }

    @AllArgsConstructor
    enum FhirVitalSignsProfile {
      VITAL_SIGNS_PANEL("85353-1"),
      RESPIRATORY_RATE("9279-1"),
      HEART_RATE("8867-4"),
      OXYGEN_SATURATION("2708-6"),
      BODY_TEMPERATURE("8310-5"),
      BODY_HEIGHT("8302-2"),
      HEAD_CIRCUMFERENCE("9843-4"),
      BODY_WEIGHT("29463-7"),
      BODY_MASS_INDEX("39156-5"),
      BLOOD_PRESSURE("85354-9"),
      SYSTOLIC_BP("8480-6"),
      DIASTOLIC_BP("8462-4");

      @Getter private final String loincCode;

      public static Optional<FhirVitalSignsProfile> findByLoincCode(String loinc) {
        for (FhirVitalSignsProfile profile : FhirVitalSignsProfile.values()) {
          if (profile.loincCode().equals(loinc)) {
            return Optional.of(profile);
          }
        }
        return Optional.empty();
      }
    }
  }
}
