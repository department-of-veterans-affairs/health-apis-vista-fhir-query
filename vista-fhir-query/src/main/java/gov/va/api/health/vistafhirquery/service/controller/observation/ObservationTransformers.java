package gov.va.api.health.vistafhirquery.service.controller.observation;

import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.allBlank;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.isBlank;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.toBigDecimal;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.valueOfValueOnlyXmlAttribute;

import gov.va.api.health.r4.api.datatypes.Quantity;
import gov.va.api.health.r4.api.datatypes.SimpleQuantity;
import gov.va.api.health.r4.api.resources.Observation;
import gov.va.api.lighthouse.vistalink.models.ValueOnlyXmlAttribute;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ObservationTransformers {
  /** Build a list of ReferenceRanges using Vista value attributes for the high and low values. */
  public static List<Observation.ReferenceRange> referenceRange(
      ValueOnlyXmlAttribute maybeHigh, ValueOnlyXmlAttribute maybeLow) {
    String high = valueOfValueOnlyXmlAttribute(maybeHigh);
    String low = valueOfValueOnlyXmlAttribute(maybeLow);
    return referenceRange(high, low);
  }

  /** Build a list of ReferenceRanges using String representations of the high and low values. */
  public static List<Observation.ReferenceRange> referenceRange(String high, String low) {
    if (allBlank(high, low)) {
      return null;
    }
    return List.of(
        Observation.ReferenceRange.builder()
            .high(simpleQuantityFor(high))
            .low(simpleQuantityFor(low))
            .build());
  }

  /** Build an R4 SimpleQuantity given a string value. */
  public static SimpleQuantity simpleQuantityFor(String value) {
    if (isBlank(value)) {
      return null;
    }
    return SimpleQuantity.builder().value(toBigDecimal(value)).build();
  }

  /** Build an R4 Quantity using Vista value attributes. */
  public static Quantity valueQuantity(
      ValueOnlyXmlAttribute maybeQuantityValue, ValueOnlyXmlAttribute maybeUnits) {
    String quantityValue = valueOfValueOnlyXmlAttribute(maybeQuantityValue);
    String units = valueOfValueOnlyXmlAttribute(maybeUnits);
    return valueQuantity(quantityValue, units);
  }

  /** Build an R4 Quantity using string representations of the value and units. */
  public static Quantity valueQuantity(String quantityValue, String units) {
    if (isBlank(quantityValue)) {
      return null;
    }
    return Quantity.builder().value(toBigDecimal(quantityValue)).unit(units).build();
  }
}
