package gov.va.api.health.vistafhirquery.service.controller.observation;

import com.google.common.base.Splitter;
import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import gov.va.api.health.r4.api.datatypes.Quantity;
import gov.va.api.health.r4.api.datatypes.SimpleQuantity;
import gov.va.api.health.r4.api.elements.Reference;
import gov.va.api.health.r4.api.resources.Observation;
import gov.va.api.lighthouse.vistalink.models.CodeAndNameXmlAttribute;
import gov.va.api.lighthouse.vistalink.models.ValueOnlyXmlAttribute;
import gov.va.api.lighthouse.vistalink.models.vprgetpatientdata.Vitals;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.NonNull;

@Builder
public class VistaVitalToR4ObservationTransformer {
  @NonNull private final Vitals.Vital vistaVital;

  // TODO: Assuming qualifiers map to bodySite, find a mapping from qualifier.vuid to SNOMED codes.
  static CodeableConcept bodySite(List<Vitals.Qualifier> qualifiers) {
    if (qualifiers == null || qualifiers.isEmpty()) {
      return null;
    }
    return CodeableConcept.builder()
        .coding(
            qualifiers.stream()
                .filter(Objects::nonNull)
                .map(
                    qualifier ->
                        Coding.builder().display(qualifier.name()).code(qualifier.vuid()).build())
                .collect(Collectors.toList()))
        .build();
  }

  static List<CodeableConcept> category() {
    return List.of(
        CodeableConcept.builder()
            .coding(
                List.of(
                    Coding.builder()
                        .system("http://terminology.hl7.org/CodeSystem/observation-category")
                        .code("vital-signs")
                        .build()))
            .build());
  }

  // TODO: Update Observation.code codings use proper LOINC code values that are matched with CDW
  // mappings.
  static CodeableConcept code(Vitals.Measurement measurement) {
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
    if ("BLOOD PRESSURE".equals(measurement.name())) {
      List<String> highs = Splitter.on("/").splitToList(measurement.high());
      List<String> lows = Splitter.on("/").splitToList(measurement.high());
      List<String> values = Splitter.on("/").splitToList(measurement.high());
      if (highs.size() == 2 && lows.size() == 2 && values.size() == 2) {
        Observation.Component systolic =
            Observation.Component.builder()
                .referenceRange(referenceRange(highs.get(0), lows.get(0)))
                .valueQuantity(valueQuantity(values.get(0), measurement.units()))
                .build();
        Observation.Component diastolic =
            Observation.Component.builder()
                .referenceRange(referenceRange(highs.get(1), lows.get(1)))
                .valueQuantity(valueQuantity(values.get(1), measurement.units()))
                .build();
        return List.of(systolic, diastolic);
      }
    }
    return null;
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
    if (high == null || low == null) {
      return null;
    }
    return List.of(
        Observation.ReferenceRange.builder()
            .high(SimpleQuantity.builder().value(new BigDecimal(high)).build())
            .low(SimpleQuantity.builder().value(new BigDecimal(low)).build())
            .build());
  }

  static Observation.ObservationStatus status(List<ValueOnlyXmlAttribute> removed) {
    if (removed == null || removed.isEmpty()) {
      return Observation.ObservationStatus._final;
    }
    return Observation.ObservationStatus.entered_in_error;
  }

  static String valueOf(ValueOnlyXmlAttribute valueOnlyXmlAttribute) {
    if (valueOnlyXmlAttribute == null) {
      return null;
    } else {
      return valueOnlyXmlAttribute.value();
    }
  }

  static Quantity valueQuantity(String value, String units) {
    if (value == null) {
      return null;
    }
    return Quantity.builder().value(new BigDecimal(value)).unit(units).build();
  }

  Observation observationFromMeasurement(Vitals.Measurement measurement) {
    if ("BLOOD PRESSURE".equals(measurement.name())) {
      return Observation.builder()
          .bodySite(bodySite(measurement.qualifiers()))
          .category(category())
          .code(code(measurement))
          .component(component(measurement))
          .effectiveDateTime(valueOf(vistaVital.taken()))
          .issued(valueOf(vistaVital.entered()))
          .id(measurement.id())
          .performer(performer(vistaVital.facility()))
          .status(status(vistaVital.removed()))
          .build();
    }
    return Observation.builder()
        .bodySite(bodySite(measurement.qualifiers()))
        .category(category())
        .code(code(measurement))
        .effectiveDateTime(valueOf(vistaVital.taken()))
        .issued(valueOf(vistaVital.entered()))
        .id(measurement.id())
        .performer(performer(vistaVital.facility()))
        .referenceRange(referenceRange(measurement.high(), measurement.low()))
        .status(status(vistaVital.removed()))
        .valueQuantity(valueQuantity(measurement.value(), measurement.units()))
        .build();
  }

  Stream<Observation> toFhir() {
    if (vistaVital.measurements() == null || vistaVital.measurements().isEmpty()) {
      return Stream.empty();
    }
    return vistaVital.measurements().stream()
        .filter(Objects::nonNull)
        .map(this::observationFromMeasurement);
  }
}
