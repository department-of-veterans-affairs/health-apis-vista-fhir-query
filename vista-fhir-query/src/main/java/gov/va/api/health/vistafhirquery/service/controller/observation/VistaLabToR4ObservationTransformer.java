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
import gov.va.api.lighthouse.vistalink.models.ValueOnlyXmlAttribute;
import gov.va.api.lighthouse.vistalink.models.vprgetpatientdata.Labs;
import java.util.List;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public class VistaLabToR4ObservationTransformer {
  @NonNull private final String patientIcn;

  @NonNull private final String vistaSiteId;

  @NonNull private final Labs.Lab vistaLab;

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

  CodeableConcept code(
      ValueOnlyXmlAttribute maybeLoinc,
      ValueOnlyXmlAttribute maybeTestName,
      ValueOnlyXmlAttribute maybeVuid) {
    String loinc = valueOfValueOnlyXmlAttribute(maybeLoinc);
    String testName = valueOfValueOnlyXmlAttribute(maybeTestName);
    String vuid = valueOfValueOnlyXmlAttribute(maybeVuid);
    if (!isBlank(loinc)) {
      log.info(
          "ToDo: Should vuid be mapped anyway to get the display value? "
              + "Can we be sure the testName is a valid loinc display value?");
      return CodeableConcept.builder()
          .coding(
              List.of(
                  Coding.builder()
                      .system("http://loinc.org")
                      .code(loinc)
                      .display(testName)
                      .build()))
          .build();
    }
    if (!isBlank(vuid)) {
      log.info("ToDo: If vuid is set and loinc isn't, map using database table.");
    }
    return null;
  }

  String idFrom(ValueOnlyXmlAttribute maybeId) {
    log.info("ToDo: Is null logical id an illegal state?");
    String id = valueOfValueOnlyXmlAttribute(maybeId);
    if (isBlank(id)) {
      return null;
    }
    return toResourceId(patientIcn, vistaSiteId, id);
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

  /** Transform a VPR PATIENT DATA VistA Lab result to FHIR Observation. */
  public Stream<Observation> toFhir() {
    // References Not Reflected: specimen, performer.facility, and performer.provider
    log.info("ToDo: Should groupName, labOrderId, orderId be in the identifier array?");
    var observation =
        Observation.builder()
            .resourceType("Observation")
            .id(idFrom(vistaLab.id()))
            .category(category())
            .subject(toReference("Patient", patientIcn, null))
            .issued(toHumanDateTime(vistaLab.collected()))
            .note(note(vistaLab.comment()))
            .referenceRange(referenceRange(vistaLab.high(), vistaLab.low()))
            .interpretation(interpretation(vistaLab.interpretation()))
            .code(code(vistaLab.loinc(), vistaLab.test(), vistaLab.vuid()))
            .valueQuantity(valueQuantity(vistaLab.result(), vistaLab.units()))
            .effectiveDateTime(toHumanDateTime(vistaLab.resulted()))
            .status(status(vistaLab.status()))
            .build();
    return Stream.of(observation);
  }
}
