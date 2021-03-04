package gov.va.api.health.vistafhirquery.service.controller.observation;

import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.allBlank;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.isBlank;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.toBigDecimal;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.valueOfValueOnlyXmlAttribute;

import gov.va.api.health.r4.api.datatypes.Quantity;
import gov.va.api.lighthouse.vistalink.models.ValueOnlyXmlAttribute;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class ValueQuantityMapping {
  private static final Map<String, Quantity.QuantityBuilder> MAPPINGS = valueQuantityMappings();

  private static Quantity.QuantityBuilder createQuantityBuilder(String loinc, String units) {
    if (isBlank(loinc)) {
      return Quantity.builder();
    }
    var builder = MAPPINGS.getOrDefault(loinc, Quantity.builder());
    if (FhirVitalSignsProfiles.BODY_TEMPERATURE.loincCode().equals(loinc)) {
      var bodyTempUnits = fromVistaBodyTemperatureUnits(units);
      builder.code(bodyTempUnits);
    }
    return builder;
  }

  /** Build an R4 Quantity given a loinc, value, and units. */
  public static Quantity from(String loinc, String value, String units) {
    if (isBlank(value) || allBlank(loinc, value, units)) {
      return null;
    }
    var quantityBuilder = createQuantityBuilder(loinc, units);
    return quantityBuilder.value(toBigDecimal(value)).unit(units).build();
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

  private static String fromVistaBodyTemperatureUnits(String maybeVistaTemperatureUnits) {
    switch (maybeVistaTemperatureUnits) {
      case "C":
        return "Cel";
      case "F":
        return "degF";
      default:
        throw new IllegalArgumentException(
            "Invalid VistA body-temperature unit representation: " + maybeVistaTemperatureUnits);
    }
  }

  private static Map<String, Quantity.QuantityBuilder> valueQuantityMappings() {
    return Map.of(
        FhirVitalSignsProfiles.RESPIRATORY_RATE.loincCode(),
        Quantity.builder().system("http://unitsofmeasure.org").code("/min"));
  }

  @AllArgsConstructor
  private enum FhirVitalSignsProfiles {
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
  }
}
