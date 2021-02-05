package gov.va.api.health.vistafhirquery.service.controller.observation;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import gov.va.api.health.r4.api.datatypes.Quantity;
import gov.va.api.health.r4.api.resources.Observation;
import gov.va.api.lighthouse.vistalink.models.ValueOnlyXmlAttribute;
import gov.va.api.lighthouse.vistalink.models.vprgetpatientdata.Vitals;
import java.util.List;
import org.junit.jupiter.api.Test;

public class VistaVitalToR4ObservationTest {
  @Test
  public void nullSafe() {
    assertThat(
            VistaVitalToR4ObservationTransformer.builder()
                .vistaVital(Vitals.Vital.builder().build())
                .build()
                .toFhir())
        .isEmpty();
    assertThat(
            VistaVitalToR4ObservationTransformer.builder()
                .vistaVital(
                    Vitals.Vital.builder()
                        .removed(List.of(ValueOnlyXmlAttribute.builder().build()))
                        .measurements(List.of(Vitals.Measurement.builder().build()))
                        .build())
                .build()
                .toFhir())
        .isEqualTo(
            List.of(
                Observation.builder()
                    .valueQuantity(Quantity.builder().build())
                    .status(Observation.ObservationStatus.entered_in_error)
                    .resourceType("Observation")
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
                    .code(
                        CodeableConcept.builder()
                            .coding(List.of(Coding.builder().system("http://loinc.org").build()))
                            .build())
                    .build()));
  }
}
