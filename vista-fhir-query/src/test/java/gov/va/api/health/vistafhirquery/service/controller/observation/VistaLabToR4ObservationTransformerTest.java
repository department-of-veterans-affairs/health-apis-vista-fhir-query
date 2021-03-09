package gov.va.api.health.vistafhirquery.service.controller.observation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import gov.va.api.health.r4.api.datatypes.Annotation;
import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import gov.va.api.health.r4.api.elements.Reference;
import gov.va.api.health.r4.api.resources.Observation;
import gov.va.api.lighthouse.charon.models.ValueOnlyXmlAttribute;
import gov.va.api.lighthouse.charon.models.vprgetpatientdata.Labs;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class VistaLabToR4ObservationTransformerTest {
  static Stream<Arguments> interpretationWithDisplay() {
    return Stream.of(
        Arguments.of("CAR", "Carrier"),
        Arguments.of("CARRIER", "Carrier"),
        Arguments.of("<", "Off scale low"),
        Arguments.of(">", "Off scale high"),
        Arguments.of("A", "Abnormal"),
        Arguments.of("AA", "Critical abnormal"),
        Arguments.of("AC", "Anti-complementary substances present"),
        Arguments.of("B", "Better"),
        Arguments.of("D", "Significant change down"),
        Arguments.of("DET", "Detected"),
        Arguments.of("E", "Equivocal"),
        Arguments.of("EX", "outside threshold"),
        Arguments.of("EXP", "Expected"),
        Arguments.of("H", "High"),
        Arguments.of("H*", "Critical high"),
        Arguments.of("HH", "Critical high"),
        Arguments.of("HU", "Significantly high"),
        Arguments.of("H>", "Significantly high"),
        Arguments.of("HM", "Hold for Medical Review"),
        Arguments.of("HX", "above high threshold"),
        Arguments.of("I", "Intermediate"),
        Arguments.of("IE", "Insufficient evidence"),
        Arguments.of("IND", "Indeterminate"),
        Arguments.of("L", "Low"),
        Arguments.of("L*", "Critical low"),
        Arguments.of("LL", "Critical low"),
        Arguments.of("LU", "Significantly low"),
        Arguments.of("L<", "Significantly low"),
        Arguments.of("LX", "below low threshold"),
        Arguments.of("MS", "moderately susceptible"),
        Arguments.of("N", "Normal"),
        Arguments.of("NCL", "No CLSI defined breakpoint"),
        Arguments.of("ND", "Not detected"),
        Arguments.of("NEG", "Negative"),
        Arguments.of("NR", "Non-reactive"),
        Arguments.of("NS", "Non-susceptible"),
        Arguments.of("OBX", "Interpretation qualifiers in separate OBX segments"),
        Arguments.of("POS", "Positive"),
        Arguments.of("QCF", "Quality control failure"),
        Arguments.of("R", "Resistant"),
        Arguments.of("RR", "Reactive"),
        Arguments.of("S", "Susceptible"),
        Arguments.of("SDD", "Susceptible-dose dependent"),
        Arguments.of("SYN-R", "Synergy - resistant"),
        Arguments.of("SYN-S", "Synergy - susceptible"),
        Arguments.of("TOX", "Cytotoxic substance present"),
        Arguments.of("U", "Significant change up"),
        Arguments.of("UNE", "Unexpected"),
        Arguments.of("VS", "very susceptible"),
        Arguments.of("W", "Worse"),
        Arguments.of("WR", "Weakly reactive"));
  }

  @Test
  void code() {
    assertThat(
            tx().code(
                    ValueOnlyXmlAttribute.of("123"),
                    ValueOnlyXmlAttribute.of("Name"),
                    ValueOnlyXmlAttribute.of(null))
                .coding()
                .get(0))
        .isEqualTo(Coding.builder().system("http://loinc.org").code("123").display("Name").build());
    assertThat(
            tx().code(
                    ValueOnlyXmlAttribute.of("123"),
                    ValueOnlyXmlAttribute.of(null),
                    ValueOnlyXmlAttribute.of(null))
                .coding()
                .get(0))
        .isEqualTo(Coding.builder().system("http://loinc.org").code("123").build());
    assertThat(
            tx().code(
                    ValueOnlyXmlAttribute.of(null),
                    ValueOnlyXmlAttribute.of(null),
                    ValueOnlyXmlAttribute.of("123")))
        .isNull();
    assertThat(
            tx().code(
                    ValueOnlyXmlAttribute.of(null),
                    ValueOnlyXmlAttribute.of("name"),
                    ValueOnlyXmlAttribute.of(null)))
        .isNull();
    assertThat(
            tx().code(
                    ValueOnlyXmlAttribute.of(null),
                    ValueOnlyXmlAttribute.of(null),
                    ValueOnlyXmlAttribute.of(null)))
        .isNull();
  }

  @Test
  void idFrom() {
    assertThat(tx().idFrom(ValueOnlyXmlAttribute.of(null))).isNull();
    assertThat(tx().idFrom(ValueOnlyXmlAttribute.of(""))).isNull();
    assertThat(tx().idFrom(ValueOnlyXmlAttribute.of("id"))).isEqualTo("Np1+123+id");
  }

  @ParameterizedTest
  @MethodSource
  void interpretationWithDisplay(String code, String display) {
    assertThat(tx().interpretation(ValueOnlyXmlAttribute.of(code)).get(0))
        .isEqualTo(
            CodeableConcept.builder()
                .coding(
                    List.of(
                        Coding.builder()
                            .system(
                                "http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation")
                            .code(code)
                            .display(display)
                            .build()))
                .text(code)
                .build());
  }

  @Test
  void interpretationWithUnknownCode() {
    assertThat(tx().interpretation(ValueOnlyXmlAttribute.of(null))).isNull();
    assertThat(tx().interpretation(ValueOnlyXmlAttribute.of("NOPE"))).isNull();
  }

  @Test
  void note() {
    assertThat(tx().note(ValueOnlyXmlAttribute.of(null))).isNull();
    assertThat(tx().note(ValueOnlyXmlAttribute.of("a")).get(0))
        .isEqualTo(Annotation.builder().text("a").build());
  }

  @Test
  void nullSafe() {
    assertThat(
            VistaLabToR4ObservationTransformer.builder()
                .patientIcn("p1")
                .vistaSiteId("123")
                .vistaLab(Labs.Lab.builder().build())
                .build()
                .conditionallyToFhir()
                .collect(Collectors.toList())
                .get(0))
        .isEqualTo(
            Observation.builder()
                .resourceType("Observation")
                .category(
                    List.of(
                        CodeableConcept.builder()
                            .coding(
                                List.of(
                                    Coding.builder()
                                        .system(
                                            "http://terminology.hl7.org/CodeSystem/observation-category")
                                        .code("laboratory")
                                        .display("Laboratory")
                                        .build()))
                            .text("Laboratory")
                            .build()))
                .subject(Reference.builder().reference("Patient/p1").build())
                .build());
  }

  @Test
  void status() {
    assertThat(tx().status(null)).isNull();
    assertThat(tx().status(ValueOnlyXmlAttribute.of("completed")))
        .isEqualTo(Observation.ObservationStatus._final);
    assertThat(tx().status(ValueOnlyXmlAttribute.of("incomplete")))
        .isEqualTo(Observation.ObservationStatus.preliminary);
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> tx().status(ValueOnlyXmlAttribute.of("NOPE")));
  }

  private VistaLabToR4ObservationTransformer tx() {
    return VistaLabToR4ObservationTransformer.builder()
        .patientIcn("p1")
        .vistaSiteId("123")
        .vistaLab(Labs.Lab.builder().build())
        .build();
  }
}
