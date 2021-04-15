package gov.va.api.health.vistafhirquery.service.controller.observation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import gov.va.api.health.r4.api.elements.Reference;
import gov.va.api.health.r4.api.resources.Observation;
import gov.va.api.lighthouse.charon.models.ValueOnlyXmlAttribute;
import gov.va.api.lighthouse.charon.models.vprgetpatientdata.Vitals;
import java.util.List;
import org.junit.jupiter.api.Test;

public class VistaVitalToR4ObservationTransformerTest {
  VitalVuidMapper mapper = mock(VitalVuidMapper.class);

  @Test
  void methodWithKnownAndUnknownReturnKnown() {
    assertThat(
            tx().method(
                    Vitals.Measurement.builder()
                        .qualifiers(
                            List.of(
                                Vitals.Qualifier.builder().vuid("-1").build(),
                                Vitals.Qualifier.builder().vuid("4710821").build()))
                        .build()))
        .isEqualTo(
            CodeableConcept.builder()
                .coding(
                    List.of(
                        Coding.builder()
                            .system("http://snomed.info/sct")
                            .code("303473005")
                            .display("Does remove prosthesis (finding)")
                            .build()))
                .build());
  }

  @Test
  void methodWithKnownVuidsReturn() {
    assertThat(
            tx().method(
                    Vitals.Measurement.builder()
                        .qualifiers(
                            List.of(
                                Vitals.Qualifier.builder().vuid("4710821").build(),
                                Vitals.Qualifier.builder().vuid("4711345").build()))
                        .build()))
        .isEqualTo(
            CodeableConcept.builder()
                .coding(
                    List.of(
                        Coding.builder()
                            .system("http://snomed.info/sct")
                            .code("303473005")
                            .display("Does remove prosthesis (finding)")
                            .build(),
                        Coding.builder()
                            .system("http://snomed.info/sct")
                            .code("258104002")
                            .display("Measured (qualifier value)")
                            .build()))
                .build());
    assertThat(
            tx().method(
                    Vitals.Measurement.builder()
                        .qualifiers(List.of(Vitals.Qualifier.builder().vuid("4711345").build()))
                        .build()))
        .isEqualTo(
            CodeableConcept.builder()
                .coding(
                    List.of(
                        Coding.builder()
                            .system("http://snomed.info/sct")
                            .code("258104002")
                            .display("Measured (qualifier value)")
                            .build()))
                .build());
  }

  @Test
  void methodWithUnknownVuidsReturnsNull() {
    assertThat(
            tx().method(
                    Vitals.Measurement.builder()
                        .qualifiers(List.of(Vitals.Qualifier.builder().vuid("-1").build()))
                        .build()))
        .isNull();
    assertThat(
            tx().method(
                    Vitals.Measurement.builder()
                        .qualifiers(
                            List.of(
                                Vitals.Qualifier.builder().vuid("-1").build(),
                                Vitals.Qualifier.builder().vuid("-2").build()))
                        .build()))
        .isNull();
  }

  @Test
  public void nullSafe() {
    when(mapper.mappings()).thenReturn(ObservationVitalSamples.Datamart.create().vuidMappings());
    assertThat(tx().conditionallyToFhir()).isEmpty();
    assertThat(
            VistaVitalToR4ObservationTransformer.builder()
                .patientIcn("p1")
                .vistaSiteId("123")
                .vistaVital(
                    Vitals.Vital.builder()
                        .removed(List.of(ValueOnlyXmlAttribute.builder().build()))
                        .measurements(
                            List.of(
                                Vitals.Measurement.builder()
                                    .qualifiers(List.of(Vitals.Qualifier.builder().build()))
                                    .build()))
                        .build())
                .vuidMapper(mapper)
                .build()
                .conditionallyToFhir())
        .isEqualTo(
            List.of(
                Observation.builder()
                    .status(Observation.ObservationStatus._final)
                    .resourceType("Observation")
                    .subject(Reference.builder().reference("Patient/p1").build())
                    .category(
                        List.of(
                            CodeableConcept.builder()
                                .coding(
                                    List.of(
                                        Coding.builder()
                                            .code("vital-signs")
                                            .system(
                                                "http://terminology.hl7.org/CodeSystem/observation-category")
                                            .display("Vital Signs")
                                            .build()))
                                .text("Vital Signs")
                                .build()))
                    .build()));
  }

  private VistaVitalToR4ObservationTransformer tx() {
    return VistaVitalToR4ObservationTransformer.builder()
        .patientIcn("p1")
        .vistaSiteId("123")
        .vistaVital(Vitals.Vital.builder().build())
        .vuidMapper(mapper)
        .build();
  }
}
