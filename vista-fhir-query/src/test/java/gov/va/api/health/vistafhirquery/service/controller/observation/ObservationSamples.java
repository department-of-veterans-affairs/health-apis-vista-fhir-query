package gov.va.api.health.vistafhirquery.service.controller.observation;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import gov.va.api.health.r4.api.datatypes.Quantity;
import gov.va.api.health.r4.api.datatypes.SimpleQuantity;
import gov.va.api.health.r4.api.elements.Reference;
import gov.va.api.health.r4.api.resources.Observation;
import gov.va.api.lighthouse.vistalink.models.CodeAndNameXmlAttribute;
import gov.va.api.lighthouse.vistalink.models.ValueOnlyXmlAttribute;
import gov.va.api.lighthouse.vistalink.models.vprgetpatientdata.Vitals;
import gov.va.api.lighthouse.vistalink.models.vprgetpatientdata.VprGetPatientData;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ObservationSamples {
  @SneakyThrows
  static String json(Object o) {
    return JacksonConfig.createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(o);
  }

  @SneakyThrows
  static String xml(Object o) {
    return new XmlMapper().writeValueAsString(o);
  }

  @AllArgsConstructor(staticName = "create")
  public static class Vista {

    public List<Vitals.Measurement> measurements() {
      return measurements("32071");
    }

    public List<Vitals.Measurement> measurements(String vistaId) {
      return List.of(
          Vitals.Measurement.builder()
              .id(vistaId)
              .vuid("4500634")
              .name("BLOOD PRESSURE")
              .value("126/65")
              .units("mm[Hg]")
              .high("210/110")
              .low("100/60")
              .build(),
          weight());
    }

    public VprGetPatientData.Response.Results results() {
      return VprGetPatientData.Response.Results.builder()
          .version("1.13")
          .timeZone("-0500")
          .vitals(Vitals.builder().total(1).vitalResults(vitals()).build())
          .build();
    }

    public Map.Entry<String, VprGetPatientData.Response.Results> resultsByStation() {
      return Map.entry("673", results());
    }

    public List<Vitals.Vital> vitals() {
      return List.of(
          Vitals.Vital.builder()
              .entered(ValueOnlyXmlAttribute.builder().value("3110225.110428").build())
              .facility(
                  CodeAndNameXmlAttribute.builder().code("673").name("TAMPA (JAH VAH)").build())
              .location(
                  CodeAndNameXmlAttribute.builder().code("23").name("GENERAL MEDICINE").build())
              .measurements(measurements())
              .taken(ValueOnlyXmlAttribute.builder().value("3100406.14").build())
              .build());
    }

    public Vitals.Measurement weight() {
      return weight("32076");
    }

    public Vitals.Measurement weight(String vistaId) {
      return Vitals.Measurement.builder()
          .id(vistaId)
          .vuid("4500639")
          .name("WEIGHT")
          .value("190")
          .units("lb")
          .metricValue("86.18")
          .metricUnits("kg")
          .bmi("25")
          .build();
    }
  }

  @AllArgsConstructor(staticName = "create")
  public static class Fhir {
    public Observation bloodPressure() {
      return Observation.builder()
          .resourceType("Observation")
          .subject(subject("p1"))
          .category(category())
          .code(bloodPressureCode())
          .component(List.of(bloodPressureSystolic(), bloodPressureDiastolic()))
          .effectiveDateTime("3100406.14")
          .issued("3110225.110428")
          .id("Np1+673+32071")
          .status(Observation.ObservationStatus._final)
          .build();
    }

    public CodeableConcept bloodPressureCode() {
      return CodeableConcept.builder()
          .coding(
              List.of(
                  Coding.builder()
                      .system("http://loinc.org")
                      .code("4500634")
                      .display("BLOOD PRESSURE")
                      .build()))
          .build();
    }

    public Observation.Component bloodPressureDiastolic() {
      return Observation.Component.builder()
          .referenceRange(
              List.of(
                  Observation.ReferenceRange.builder()
                      .high(SimpleQuantity.builder().value(new BigDecimal("110")).build())
                      .low(SimpleQuantity.builder().value(new BigDecimal("60")).build())
                      .build()))
          .valueQuantity(Quantity.builder().value(new BigDecimal("65")).unit("mm[Hg]").build())
          .build();
    }

    public Observation.Component bloodPressureSystolic() {
      return Observation.Component.builder()
          .referenceRange(
              List.of(
                  Observation.ReferenceRange.builder()
                      .high(SimpleQuantity.builder().value(new BigDecimal("210")).build())
                      .low(SimpleQuantity.builder().value(new BigDecimal("100")).build())
                      .build()))
          .valueQuantity(Quantity.builder().value(new BigDecimal("126")).unit("mm[Hg]").build())
          .build();
    }

    public List<CodeableConcept> category() {
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

    List<Observation> observations() {
      return List.of(bloodPressure(), weight());
    }

    public Reference subject(String patientIcn) {
      return Reference.builder().reference("Patient/" + patientIcn).build();
    }

    public Observation weight() {
      return weight("Np1+673+32076");
    }

    public Observation weight(String idSegment) {
      return Observation.builder()
          .resourceType("Observation")
          .subject(subject("p1"))
          .category(category())
          .code(weightCode())
          .effectiveDateTime("3100406.14")
          .issued("3110225.110428")
          .id(idSegment)
          .status(Observation.ObservationStatus._final)
          .valueQuantity(Quantity.builder().value(new BigDecimal("190")).unit("lb").build())
          .build();
    }

    public CodeableConcept weightCode() {
      return CodeableConcept.builder()
          .coding(
              List.of(
                  Coding.builder()
                      .system("http://loinc.org")
                      .code("4500639")
                      .display("WEIGHT")
                      .build()))
          .build();
    }
  }
}
