package gov.va.api.health.vistafhirquery.service.controller.observation;

import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.ifPresent;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.isBlank;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.toHumanDateTime;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.toReference;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.toResourceId;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.valueOfValueOnlyXmlAttribute;
import static gov.va.api.health.vistafhirquery.service.controller.observation.ObservationTransformers.referenceRange;
import static gov.va.api.health.vistafhirquery.service.controller.observation.ObservationTransformers.valueQuantity;

import gov.va.api.health.r4.api.datatypes.Annotation;
import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import gov.va.api.health.r4.api.resources.Observation;
import gov.va.api.lighthouse.charon.models.ValueOnlyXmlAttribute;
import gov.va.api.lighthouse.charon.models.vprgetpatientdata.Labs;
import gov.va.api.lighthouse.charon.models.vprgetpatientdata.VprGetPatientData;
import java.util.List;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
public class VistaLabToR4ObservationTransformer {
  @NonNull private final String patientIcn;

  @NonNull private final String vistaSiteId;

  @NonNull private final Labs.Lab vistaLab;

  private final AllowedObservationCodes conditions;

  private List<CodeableConcept> category() {
    return List.of(
        CodeableConcept.builder()
            .coding(
                List.of(
                    Coding.builder()
                        .system("http://terminology.hl7.org/CodeSystem/observation-category")
                        .code("laboratory")
                        .display("Laboratory")
                        .build()))
            .text("Laboratory")
            .build());
  }

  CodeableConcept code(ValueOnlyXmlAttribute maybeLoinc, ValueOnlyXmlAttribute maybeTestName) {
    String loinc = valueOfValueOnlyXmlAttribute(maybeLoinc);
    String testName = valueOfValueOnlyXmlAttribute(maybeTestName);
    if (isBlank(loinc)) {
      return null;
    }
    return CodeableConcept.builder()
        .coding(List.of(Coding.builder().system("http://loinc.org").code(loinc).build()))
        .text(testName)
        .build();
  }

  /** Transform a VPR PATIENT DATA VistA Lab result to FHIR Observation. */
  public Stream<Observation> conditionallyToFhir() {
    try {
      return conditionallyToFhirUnsafe();
    } catch (Exception e) {
      /*
       * There are a multitude of surprise exceptions that could crop up because data from Vista can
       * be very unreliable. Since the transformer is interacting strictly with data that is in
       * memory, (no services, db, etc. are used in this portion of the logic.) Any exceptions
       * thrown during transformation can be safely caught and the record ignored.
       */
      log.warn(
          "Ignoring malformed record: type=Lab, site={}, id={}, error={}",
          vistaSiteId,
          vistaLab.id(),
          e.getMessage());
      return Stream.empty();
    }
  }

  private Stream<Observation> conditionallyToFhirUnsafe() {
    // References Not Reflected: specimen, performer.facility, and performer.provider
    if (!hasAcceptedCode()) {
      return Stream.empty();
    }
    var observation =
        Observation.builder()
            .resourceType("Observation")
            .id(idFrom(vistaLab.id()))
            .category(category())
            .subject(toReference("Patient", patientIcn, null))
            .issued(toHumanDateTime(vistaLab.resulted()))
            .note(note(vistaLab.comment()))
            .referenceRange(referenceRange(vistaLab.high(), vistaLab.low()))
            .interpretation(interpretation(vistaLab.interpretation()))
            .code(code(vistaLab.loinc(), vistaLab.test()))
            .valueQuantity(valueQuantity(vistaLab.result(), vistaLab.units()))
            .effectiveDateTime(toHumanDateTime(vistaLab.collected()))
            .status(status(vistaLab.status()))
            .build();
    return Stream.of(observation);
  }

  private boolean hasAcceptedCode() {
    if (isBlank(conditions)) {
      return true;
    }
    var loinc = valueOfValueOnlyXmlAttribute(vistaLab.loinc());
    return conditions.isAllowedLoincCode(loinc);
  }

  String idFrom(ValueOnlyXmlAttribute maybeId) {
    String id = valueOfValueOnlyXmlAttribute(maybeId);
    if (isBlank(id)) {
      return null;
    }
    return toResourceId(patientIcn, vistaSiteId, VprGetPatientData.Domains.labs, id);
  }

  List<CodeableConcept> interpretation(ValueOnlyXmlAttribute maybeInterpretation) {
    String interpretation = valueOfValueOnlyXmlAttribute(maybeInterpretation);
    if (isBlank(interpretation)) {
      return null;
    }
    return ifPresent(
        InterpretationDisplayMapping.forCode(interpretation),
        display ->
            List.of(
                CodeableConcept.builder()
                    .coding(
                        List.of(
                            Coding.builder()
                                .system(
                                    "http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation")
                                .code(interpretation)
                                .display(display)
                                .build()))
                    .text(interpretation)
                    .build()));
  }

  List<Annotation> note(ValueOnlyXmlAttribute maybeNote) {
    String note = valueOfValueOnlyXmlAttribute(maybeNote);
    if (isBlank(note)) {
      return null;
    }
    return List.of(Annotation.builder().text(note).build());
  }

  Observation.ObservationStatus status(ValueOnlyXmlAttribute maybeStatus) {
    String status = valueOfValueOnlyXmlAttribute(maybeStatus);
    if (isBlank(status)) {
      return null;
    }
    switch (status) {
      case "completed":
        return Observation.ObservationStatus._final;
      case "incomplete":
        return Observation.ObservationStatus.preliminary;
      default:
        throw new IllegalArgumentException("Invalid Observation Status Type: " + status);
    }
  }
}
