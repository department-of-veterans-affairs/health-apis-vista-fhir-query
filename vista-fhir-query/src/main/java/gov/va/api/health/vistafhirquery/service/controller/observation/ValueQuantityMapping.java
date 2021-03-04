package gov.va.api.health.vistafhirquery.service.controller.observation;

import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.toBigDecimal;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.valueOfValueOnlyXmlAttribute;

import gov.va.api.health.r4.api.datatypes.Quantity;
import gov.va.api.lighthouse.vistalink.models.ValueOnlyXmlAttribute;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class ValueQuantityMapping {

  private static Quantity createQuantity(
      Optional<FhirVitalSignsProfile> maybeProfileType, String value, String units) {
    var quantity = Quantity.builder().value(toBigDecimal(value)).unit(units);
    if (maybeProfileType.isEmpty()) {
      return quantity.build();
    }
    // ToDo: Keep the vista units or map the code to units?
    quantity.system("http://unitsofmeasure.org");
    switch (maybeProfileType.get()) {
      case RESPIRATORY_RATE:
        // fall-through
      case HEART_RATE:
        return quantity.code("/min").build();
      case OXYGEN_SATURATION:
        return quantity.code("%").build();
      case BODY_TEMPERATURE:
        BodyTemperatureUnits bodyTemp = BodyTemperatureUnits.findByVistaUnit(units);
        return quantity.code(bodyTemp.code()).unit(bodyTemp.display()).build();
      case BODY_HEIGHT:
        // fall-through
      case HEAD_CIRCUMFERENCE:
        BodyLengthUnits length = BodyLengthUnits.findByVistaUnit(units);
        return quantity.code(length.code()).unit(length.display()).build();
      case BODY_WEIGHT:
        BodyWeightUnits weight = BodyWeightUnits.findByVistaUnit(units);
        return quantity.code(weight.code()).unit(weight.display()).build();
      case BODY_MASS_INDEX:
        return quantity.code("kg/m2").build();
      case SYSTOLIC_BP:
        // fall-through
      case DIASTOLIC_BP:
        return quantity.code("mm[Hg]").build();
      case BLOOD_PRESSURE:
        // ToDo throw (blood pressure quantities should be mapped in parts sys&dia)
      case VITAL_SIGNS_PANEL:
        // ToDo throw (panel quantities should be mapped in parts then added to components)
      default:
        // ToDo throw, we should have returned/thrown by now
        return quantity.build();
    }
  }

  /** Build an R4 Quantity given a loinc, value, and units. */
  public static Quantity from(String loinc, String value, String units) {
    if (value == null) {
      return null;
    }
    var maybeVitalSignsProfile = FhirVitalSignsProfile.findByLoincCode(loinc);
    return createQuantity(maybeVitalSignsProfile, value, units);
  }

  public static Quantity from(String value, String units) {
    return from(null, value, units);
  }

  /** Build an R4 Quantity from a vista xml response value and units. */
  public static Quantity from(ValueOnlyXmlAttribute value, ValueOnlyXmlAttribute units) {
    String quantityValue = valueOfValueOnlyXmlAttribute(value);
    String unitValue = valueOfValueOnlyXmlAttribute(units);
    return from(quantityValue, unitValue);
  }

  @AllArgsConstructor
  enum BodyLengthUnits {
    cm("cm", "cm"),
    in("[in_i]", "in_i");

    @Getter private final String code;

    @Getter private final String display;

    public static BodyLengthUnits findByVistaUnit(String vistaUnitString) {
      switch (vistaUnitString) {
        case "cm":
          return cm;
        case "in":
          return in;
        default:
          throw new IllegalArgumentException(
              "Invalid VistA body-length unit representation: " + vistaUnitString);
      }
    }
  }

  @AllArgsConstructor
  enum BodyTemperatureUnits {
    C("Cel", "Cel"),
    F("[degF]", "degF");

    @Getter private final String code;

    @Getter private final String display;

    public static BodyTemperatureUnits findByVistaUnit(String vistaUnitString) {
      switch (vistaUnitString) {
        case "C":
          return C;
        case "F":
          return F;
        default:
          throw new IllegalArgumentException(
              "Invalid VistA body-temperature unit representation: " + vistaUnitString);
      }
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
      switch (vistaUnitString) {
        case "g":
          // Can vista return 'g' as a unit?
          // ToDo throw?
        case "kg":
          return kg;
        case "lb":
          return lb;
        default:
          throw new IllegalArgumentException(
              "Invalid VistA body-weight unit representation: " + vistaUnitString);
      }
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
      if (loinc == null) {
        return Optional.empty();
      }
      for (FhirVitalSignsProfile profile : FhirVitalSignsProfile.values()) {
        if (profile.loincCode().equals(loinc)) {
          return Optional.of(profile);
        }
      }
      return Optional.empty();
    }
  }
}
