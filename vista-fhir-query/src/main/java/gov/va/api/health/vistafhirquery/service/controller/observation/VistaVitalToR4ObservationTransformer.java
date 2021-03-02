package gov.va.api.health.vistafhirquery.service.controller.observation;

import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.isBlank;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.toHumanDateTime;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.toReference;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.toResourceId;
import static gov.va.api.health.vistafhirquery.service.controller.observation.ObservationTransformers.referenceRange;
import static gov.va.api.health.vistafhirquery.service.controller.observation.ObservationTransformers.valueQuantity;
import static gov.va.api.health.vistafhirquery.service.controller.observation.VitalVuidMapper.asCodeableConcept;
import static gov.va.api.health.vistafhirquery.service.controller.observation.VitalVuidMapper.forSystem;
import static gov.va.api.health.vistafhirquery.service.controller.observation.VitalVuidMapper.forVuid;
import static org.springframework.util.CollectionUtils.isEmpty;

import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import gov.va.api.health.r4.api.resources.Observation;
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

@Slf4j
@Builder
public class VistaVitalToR4ObservationTransformer {
  @NonNull private final String patientIcn;

  @NonNull private final String vistaSiteId;

  @NonNull private final Vitals.Vital vistaVital;

  private final AllowedObservationCodes conditions;

  private final VitalVuidMapper vuidMapper;

  List<CodeableConcept> category() {
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

  CodeableConcept code(Vitals.Measurement measurement) {
    if (isBlank(measurement) || isBlank(measurement.vuid())) {
      return null;
    }
    Optional<CodeableConcept> maybeLoincMapping =
        vuidMapper.mappings().stream()
            .filter(forVuid(measurement.vuid()).and(forSystem("http://loinc.org")))
            .map(asCodeableConcept())
            .filter(Objects::nonNull)
            .findFirst();
    return maybeLoincMapping.orElse(null);
  }

  /**
   * FHIR expects blood pressure to be split into two components within a single vital-signs
   * response, with the values provided in the format of `systolic/diastolic` so when split systolic
   * is represented by the 0 index while diastolic is represented by the 1 index.
   */
  List<Observation.Component> component(Vitals.Measurement measurement) {
    Optional<BloodPressure> bp = measurement.asBloodPressure();
    if (bp.isEmpty()) {
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

  Stream<Observation> conditionallyToFhir() {
    if (isEmpty(vistaVital.measurements())) {
      return Stream.empty();
    }
    return vistaVital.measurements().parallelStream()
        .filter(Objects::nonNull)
        .filter(
            measurement -> isBlank(conditions) || conditions.isAllowedVuidCode(measurement.vuid()))
        .map(this::observationFromMeasurement);
  }

  String idFrom(String id) {
    log.info("ToDo: Is null logical id an illegal state?");
    if (isBlank(id)) {
      return null;
    }
    return toResourceId(patientIcn, vistaSiteId, id);
  }

  Observation observationFromMeasurement(Vitals.Measurement measurement) {
    var patientReference = toReference("Patient", patientIcn, null);
    if (measurement.isBloodPressure()) {
      return Observation.builder()
          .resourceType("Observation")
          .id(idFrom(measurement.id()))
          .category(category())
          .subject(patientReference)
          .code(code(measurement))
          .component(component(measurement))
          .effectiveDateTime(toHumanDateTime(vistaVital.taken()))
          .issued(toHumanDateTime(vistaVital.entered()))
          .status(status(vistaVital.removed()))
          .build();
    }
    return Observation.builder()
        .resourceType("Observation")
        .id(idFrom(measurement.id()))
        .category(category())
        .subject(patientReference)
        .code(code(measurement))
        .effectiveDateTime(toHumanDateTime(vistaVital.taken()))
        .issued(toHumanDateTime(vistaVital.entered()))
        .referenceRange(referenceRange(measurement.high(), measurement.low()))
        .status(status(vistaVital.removed()))
        .valueQuantity(valueQuantity(measurement.value(), measurement.units()))
        .build();
  }

  Observation.ObservationStatus status(List<ValueOnlyXmlAttribute> removed) {
    if (isEmpty(removed) || removed.get(0) == null || removed.get(0).value() == null) {
      return Observation.ObservationStatus._final;
    }
    return Observation.ObservationStatus.entered_in_error;
  }
}
