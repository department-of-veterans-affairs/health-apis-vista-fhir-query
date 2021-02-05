package gov.va.api.health.vistafhirquery.service.controller.observation;

import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.toBigDecimal;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.valueOfValueOnlyXmlAttribute;
import static org.springframework.util.CollectionUtils.isEmpty;

import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import gov.va.api.health.r4.api.datatypes.Quantity;
import gov.va.api.health.r4.api.datatypes.SimpleQuantity;
import gov.va.api.health.r4.api.elements.Reference;
import gov.va.api.health.r4.api.resources.Observation;
import gov.va.api.lighthouse.vistalink.models.CodeAndNameXmlAttribute;
import gov.va.api.lighthouse.vistalink.models.ValueOnlyXmlAttribute;
import gov.va.api.lighthouse.vistalink.models.vprgetpatientdata.BloodPressure;
import gov.va.api.lighthouse.vistalink.models.vprgetpatientdata.Vitals;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
public class VistaVitalToR4ObservationTransformer {
  @NonNull private final Vitals.Vital vistaVital;

  static List<CodeableConcept> category() {
    return List.of(
        CodeableConcept.builder()
            .text("Vital Signs")
            .coding(
                List.of(
                    Coding.builder()
                        .code("vital-signs")
                        .display("Vital Signs")
                        .system("http://terminology.hl7.org/CodeSystem/observation-category")
                        .build()))
            .build());
  }

  static CodeableConcept code(Vitals.Measurement measurement) {
    log.info(
        "TODO: Update Observation.code codings use proper"
            + " LOINC code values that are matched with CDW mappings");
    return CodeableConcept.builder()
        .coding(
            List.of(
                Coding.builder()
                    .system("http://loinc.org")
                    .code(measurement.vuid())
                    .display(measurement.name())
                    .build()))
        .build();
  }

  /**
   * FHIR expects blood pressure to be split into two components within a single vital-signs
   * response, with the values provided in the format of `systolic/diastolic` so when split systolic
   * is represented by the 0 index while diastolic is represented by the 1 index.
   */
  static List<Observation.Component> component(Vitals.Measurement measurement) {
    Optional<BloodPressure> bp = measurement.asBloodPressure();
    if (!bp.isPresent()) {
      return null;
    }
    BloodPressure.BloodPressureMeasurement systolicMeasurement = bp.get().systolic();
    BloodPressure.BloodPressureMeasurement diastolicMeasurement = bp.get().diastolic();
    Observation.Component systolic =
        Observation.Component.builder()
            .referenceRange(referenceRange(systolicMeasurement.high(), systolicMeasurement.low()))
            .valueQuantity(valueQuantity(systolicMeasurement.value(), measurement.units()))
            .build();
    Observation.Component diastolic =
        Observation.Component.builder()
            .referenceRange(referenceRange(diastolicMeasurement.high(), diastolicMeasurement.low()))
            .valueQuantity(valueQuantity(diastolicMeasurement.value(), measurement.units()))
            .build();
    return List.of(systolic, diastolic);
  }

  static List<Reference> performer(CodeAndNameXmlAttribute facility) {
    if (facility == null) {
      return null;
    } else {
      return List.of(
          Reference.builder().reference(facility.code()).display(facility.name()).build());
    }
  }

  static List<Observation.ReferenceRange> referenceRange(String high, String low) {
    if (high == null && low == null) {
      return null;
    }
    return List.of(
        Observation.ReferenceRange.builder()
            .high(SimpleQuantity.builder().value(toBigDecimal(high)).build())
            .low(SimpleQuantity.builder().value(toBigDecimal(low)).build())
            .build());
  }

  static Observation.ObservationStatus status(List<ValueOnlyXmlAttribute> removed) {
    if (isEmpty(removed)) {
      return Observation.ObservationStatus._final;
    }
    return Observation.ObservationStatus.entered_in_error;
  }

  static Quantity valueQuantity(String value, String units) {
    return Quantity.builder().value(toBigDecimal(value)).unit(units).build();
  }

  Observation observationFromMeasurement(Vitals.Measurement measurement) {
    if (measurement.isBloodPressure()) {
      return Observation.builder()
          .resourceType("Observation")
          .category(category())
          .code(code(measurement))
          .component(component(measurement))
          .effectiveDateTime(valueOfValueOnlyXmlAttribute(vistaVital.taken()))
          .issued(valueOfValueOnlyXmlAttribute(vistaVital.entered()))
          .id(measurement.id())
          .performer(performer(vistaVital.facility()))
          .status(status(vistaVital.removed()))
          .build();
    }
    return Observation.builder()
        .resourceType("Observation")
        .category(category())
        .code(code(measurement))
        .effectiveDateTime(valueOfValueOnlyXmlAttribute(vistaVital.taken()))
        .issued(valueOfValueOnlyXmlAttribute(vistaVital.entered()))
        .id(measurement.id())
        .performer(performer(vistaVital.facility()))
        .referenceRange(referenceRange(measurement.high(), measurement.low()))
        .status(status(vistaVital.removed()))
        .valueQuantity(valueQuantity(measurement.value(), measurement.units()))
        .build();
  }

  Stream<Observation> toFhir() {
    if (isEmpty(vistaVital.measurements())) {
      return Stream.empty();
    }
    return vistaVital.measurements().stream()
        .filter(Objects::nonNull)
        .map(this::observationFromMeasurement);
  }
}
