package gov.va.api.health.vistafhirquery.service.controller.observation;

import gov.va.api.health.r4.api.datatypes.Annotation;
import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import gov.va.api.health.r4.api.datatypes.Quantity;
import gov.va.api.health.r4.api.datatypes.SimpleQuantity;
import gov.va.api.health.r4.api.elements.Reference;
import gov.va.api.health.r4.api.resources.Observation;
import gov.va.api.lighthouse.vistalink.models.CodeAndNameXmlAttribute;
import gov.va.api.lighthouse.vistalink.models.ValueOnlyXmlAttribute;
import gov.va.api.lighthouse.vistalink.models.vprgetpatientdata.Labs;
import gov.va.api.lighthouse.vistalink.models.vprgetpatientdata.VprGetPatientData;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ObservationLabSamples {
  @NoArgsConstructor(staticName = "create")
  public static class Vista {
    public List<Labs.Lab> labs() {
      return List.of(
          Labs.Lab.builder()
              .collected(ValueOnlyXmlAttribute.of("3100715.110004"))
              .comment(ValueOnlyXmlAttribute.of("Ordering Provider: Eightyeight Vehu"))
              .facility(CodeAndNameXmlAttribute.of("500", "CAMP MASTER"))
              .groupName(ValueOnlyXmlAttribute.of("RIA 0412 7"))
              .high(ValueOnlyXmlAttribute.of("9"))
              .id(ValueOnlyXmlAttribute.of("CH;6899283.889996;741"))
              .localName(ValueOnlyXmlAttribute.of("TSH"))
              .low(ValueOnlyXmlAttribute.of("5"))
              .performingLab(ValueOnlyXmlAttribute.of("ALBANY VA MEDICAL CENTER"))
              .provider(provider())
              .result(ValueOnlyXmlAttribute.of("7.3"))
              .resulted(ValueOnlyXmlAttribute.of("3110412.125156"))
              .specimen(CodeAndNameXmlAttribute.of("0X500", "SERUM"))
              .status(ValueOnlyXmlAttribute.of("completed"))
              .test(ValueOnlyXmlAttribute.of("TSH"))
              .type(ValueOnlyXmlAttribute.of("CH"))
              .units(ValueOnlyXmlAttribute.of("MCIU/ML"))
              .interpretation(ValueOnlyXmlAttribute.of("H"))
              .loinc(ValueOnlyXmlAttribute.of("1751-7"))
              .build());
    }

    Labs.Provider provider() {
      return Labs.Provider.builder()
          .code("20090")
          .name("VEHU,EIGHTYEIGHT")
          .taxonomyCode("203B00000N")
          .providerType("Physicians (M.D. and D.O.)")
          .classification("Physician/Osteopath")
          .service("MEDICINE")
          .build();
    }

    public VprGetPatientData.Response.Results results() {
      return VprGetPatientData.Response.Results.builder()
          .version("1.13")
          .timeZone("-0500")
          .labs(Labs.builder().labResults(labs()).build())
          .build();
    }

    public Map.Entry<String, VprGetPatientData.Response.Results> resultsByStation() {
      return Map.entry("673", results());
    }
  }

  @NoArgsConstructor(staticName = "create")
  public static class Fhir {
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

    private CodeableConcept code() {
      return CodeableConcept.builder()
          .coding(
              List.of(
                  Coding.builder()
                      .system("http://loinc.org")
                      .code("1751-7")
                      .display("TSH")
                      .build()))
          .build();
    }

    private List<CodeableConcept> interpretation() {
      return List.of(
          CodeableConcept.builder()
              .coding(
                  List.of(
                      Coding.builder()
                          .system(
                              "http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation")
                          .code("H")
                          .display("High")
                          .build()))
              .text("H")
              .build());
    }

    private List<Annotation> note() {
      return List.of(Annotation.builder().text("Ordering Provider: Eightyeight Vehu").build());
    }

    Observation observation() {
      return Observation.builder()
          .id("Np1+673+CH;6899283.889996;741")
          .category(category())
          .subject(Reference.builder().reference("Patient/p1").build())
          .issued("3100715.110004")
          .note(note())
          .referenceRange(referenceRange())
          .interpretation(interpretation())
          .code(code())
          .valueQuantity(valueQuantity())
          .effectiveDateTime("3110412.125156")
          .status(Observation.ObservationStatus._final)
          .build();
    }

    private List<Observation.ReferenceRange> referenceRange() {
      return List.of(
          Observation.ReferenceRange.builder()
              .low(SimpleQuantity.builder().value(new BigDecimal("5")).build())
              .high(SimpleQuantity.builder().value(new BigDecimal("9")).build())
              .build());
    }

    private Quantity valueQuantity() {
      return Quantity.builder().value(new BigDecimal("7.3")).unit("MCIU/ML").build();
    }
  }
}
