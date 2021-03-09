package gov.va.api.health.vistafhirquery.service.controller.observation;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.r4.api.datatypes.Quantity;
import gov.va.api.health.r4.api.datatypes.SimpleQuantity;
import gov.va.api.health.r4.api.resources.Observation;
import gov.va.api.lighthouse.charon.models.ValueOnlyXmlAttribute;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

public class ObservationTransformersTest {

  @Test
  void referenceRange() {
    assertThat(
            ObservationTransformers.referenceRange(
                    ValueOnlyXmlAttribute.of("9"), ValueOnlyXmlAttribute.of("1"))
                .get(0))
        .isEqualTo(
            Observation.ReferenceRange.builder()
                .high(SimpleQuantity.builder().value(new BigDecimal("9")).build())
                .low(SimpleQuantity.builder().value(new BigDecimal("1")).build())
                .build());
    assertThat(
            ObservationTransformers.referenceRange(
                    ValueOnlyXmlAttribute.of("9"), ValueOnlyXmlAttribute.of(null))
                .get(0))
        .isEqualTo(
            Observation.ReferenceRange.builder()
                .high(SimpleQuantity.builder().value(new BigDecimal("9")).build())
                .build());
    assertThat(
            ObservationTransformers.referenceRange(
                    ValueOnlyXmlAttribute.of(null), ValueOnlyXmlAttribute.of("1"))
                .get(0))
        .isEqualTo(
            Observation.ReferenceRange.builder()
                .low(SimpleQuantity.builder().value(new BigDecimal("1")).build())
                .build());
    assertThat(
            ObservationTransformers.referenceRange(
                ValueOnlyXmlAttribute.of(null), ValueOnlyXmlAttribute.of(null)))
        .isNull();
  }

  @Test
  void valueQuantity() {
    assertThat(
            ObservationTransformers.valueQuantity(
                ValueOnlyXmlAttribute.of("1"), ValueOnlyXmlAttribute.of("mL")))
        .isEqualTo(Quantity.builder().value(new BigDecimal("1")).unit("mL").build());
    assertThat(
            ObservationTransformers.valueQuantity(
                ValueOnlyXmlAttribute.of("1"), ValueOnlyXmlAttribute.of(null)))
        .isEqualTo(Quantity.builder().value(new BigDecimal("1")).build());
    assertThat(
            ObservationTransformers.valueQuantity(
                ValueOnlyXmlAttribute.of(null), ValueOnlyXmlAttribute.of("mL")))
        .isNull();
    assertThat(
            ObservationTransformers.valueQuantity(
                ValueOnlyXmlAttribute.of(null), ValueOnlyXmlAttribute.of(null)))
        .isNull();
    assertThat(ObservationTransformers.valueQuantity("2708-6", "1", "%"))
        .isEqualTo(
            Quantity.builder()
                .system("http://unitsofmeasure.org")
                .code("%")
                .value(new BigDecimal(1))
                .unit("%")
                .build());
    assertThat(ObservationTransformers.valueQuantity("UNK", "1", "%"))
        .isEqualTo(Quantity.builder().value(new BigDecimal(1)).unit("%").build());
  }
}
