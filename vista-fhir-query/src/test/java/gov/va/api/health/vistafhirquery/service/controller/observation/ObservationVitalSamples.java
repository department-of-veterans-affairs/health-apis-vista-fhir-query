package gov.va.api.health.vistafhirquery.service.controller.observation;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.r4.api.bundle.AbstractBundle;
import gov.va.api.health.r4.api.bundle.AbstractEntry;
import gov.va.api.health.r4.api.bundle.BundleLink;
import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import gov.va.api.health.r4.api.datatypes.Quantity;
import gov.va.api.health.r4.api.datatypes.SimpleQuantity;
import gov.va.api.health.r4.api.elements.Reference;
import gov.va.api.health.r4.api.resources.Observation;
import gov.va.api.lighthouse.charon.models.CodeAndNameXmlAttribute;
import gov.va.api.lighthouse.charon.models.ValueOnlyXmlAttribute;
import gov.va.api.lighthouse.charon.models.vprgetpatientdata.Labs;
import gov.va.api.lighthouse.charon.models.vprgetpatientdata.Vitals;
import gov.va.api.lighthouse.charon.models.vprgetpatientdata.VprGetPatientData;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ObservationVitalSamples {
  @SneakyThrows
  static String json(Object o) {
    return JacksonConfig.createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(o);
  }

  @SneakyThrows
  static String xml(Object o) {
    return new XmlMapper().writeValueAsString(o);
  }

  @NoArgsConstructor(staticName = "create")
  public static class Datamart {
    public List<VitalVuidMappingEntity> mappingEntities() {
      return List.of(
          VitalVuidMappingEntity.builder()
              .sourceValue("4500634")
              .code("55284-4")
              .display("Blood pressure systolic and diastolic")
              .uri("http://loinc.org")
              .codingSystemId((short) 11)
              .build(),
          VitalVuidMappingEntity.builder()
              .sourceValue("4500639")
              .code("29463-7")
              .display("Body weight")
              .uri("http://loinc.org")
              .codingSystemId((short) 11)
              .build());
    }

    public List<VitalVuidMapper.VitalVuidMapping> vuidMappings() {
      return List.of(
          VitalVuidMapper.VitalVuidMapping.builder()
              .vuid("4500634")
              .code("55284-4")
              .display("Blood pressure systolic and diastolic")
              .system("http://loinc.org")
              .build(),
          VitalVuidMapper.VitalVuidMapping.builder()
              .vuid("4500639")
              .code("29463-7")
              .display("Body weight")
              .system("http://loinc.org")
              .build());
    }
  }

  @AllArgsConstructor(staticName = "create")
  public static class Fhir {
    static Observation.Bundle asBundle(
        String baseUrl,
        Collection<Observation> observations,
        int totalRecords,
        BundleLink... links) {
      return Observation.Bundle.builder()
          .resourceType("Bundle")
          .type(AbstractBundle.BundleType.searchset)
          .total(totalRecords)
          .link(Arrays.asList(links))
          .entry(
              observations.stream()
                  .map(
                      resource ->
                          Observation.Entry.builder()
                              .fullUrl(baseUrl + "/Observation/" + resource.id())
                              .resource(resource)
                              .search(
                                  AbstractEntry.Search.builder()
                                      .mode(AbstractEntry.SearchMode.match)
                                      .build())
                              .build())
                  .collect(Collectors.toList()))
          .build();
    }

    public static BundleLink link(BundleLink.LinkRelation rel, String base, String query) {
      return BundleLink.builder().relation(rel).url(base + "?" + query).build();
    }

    public Observation bloodPressure() {
      return Observation.builder()
          .resourceType("Observation")
          .subject(subject("p1"))
          .category(category())
          .code(bloodPressureCode())
          .component(List.of(bloodPressureSystolic(), bloodPressureDiastolic()))
          .effectiveDateTime("2010-04-06T14:00:00Z")
          .issued("2011-02-25T11:04:28Z")
          .id("sNp1+673+V32071")
          .status(Observation.ObservationStatus._final)
          .method(method())
          .build();
    }

    public CodeableConcept bloodPressureCode() {
      return CodeableConcept.builder()
          .coding(
              List.of(
                  Coding.builder()
                      .system("http://loinc.org")
                      .code("55284-4")
                      .display("Blood pressure systolic and diastolic")
                      .build()))
          .build();
    }

    public Observation.Component bloodPressureDiastolic() {
      return Observation.Component.builder()
          .code(
              CodeableConcept.builder()
                  .coding(
                      List.of(
                          Coding.builder()
                              .system("http://loinc.org")
                              .code("8462-4")
                              .display("Diastolic blood pressure")
                              .build()))
                  .build())
          .referenceRange(
              List.of(
                  Observation.ReferenceRange.builder()
                      .high(SimpleQuantity.builder().value(new BigDecimal("110")).build())
                      .low(SimpleQuantity.builder().value(new BigDecimal("60")).build())
                      .build()))
          .valueQuantity(
              Quantity.builder()
                  .system("http://unitsofmeasure.org")
                  .code("mm[Hg]")
                  .value(new BigDecimal("65"))
                  .unit("mm[Hg]")
                  .build())
          .build();
    }

    public Observation.Component bloodPressureSystolic() {
      return Observation.Component.builder()
          .code(
              CodeableConcept.builder()
                  .coding(
                      List.of(
                          Coding.builder()
                              .system("http://loinc.org")
                              .code("8480-6")
                              .display("Systolic blood pressure")
                              .build()))
                  .build())
          .referenceRange(
              List.of(
                  Observation.ReferenceRange.builder()
                      .high(SimpleQuantity.builder().value(new BigDecimal("210")).build())
                      .low(SimpleQuantity.builder().value(new BigDecimal("100")).build())
                      .build()))
          .valueQuantity(
              Quantity.builder()
                  .system("http://unitsofmeasure.org")
                  .code("mm[Hg]")
                  .value(new BigDecimal("126"))
                  .unit("mm[Hg]")
                  .build())
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

    public CodeableConcept method() {
      return CodeableConcept.builder()
          .coding(
              List.of(
                  Coding.builder()
                      .system("http://snomed.info/sct")
                      .display("Measured (qualifier value)")
                      .code("258104002")
                      .build()))
          .build();
    }

    List<Observation> observations() {
      return List.of(bloodPressure(), weight());
    }

    public Reference subject(String patientIcn) {
      return Reference.builder().reference("Patient/" + patientIcn).build();
    }

    public Observation weight() {
      return weight("sNp1+673+V32076");
    }

    public Observation weight(String idSegment) {
      return Observation.builder()
          .resourceType("Observation")
          .subject(subject("p1"))
          .category(category())
          .code(weightCode())
          .effectiveDateTime("2010-04-06T14:00:00Z")
          .issued("2011-02-25T11:04:28Z")
          .id(idSegment)
          .status(Observation.ObservationStatus._final)
          .valueQuantity(
              Quantity.builder()
                  .system("http://unitsofmeasure.org")
                  .code("[lb_av]")
                  .value(new BigDecimal("190"))
                  .unit("lb")
                  .build())
          .build();
    }

    public CodeableConcept weightCode() {
      return CodeableConcept.builder()
          .coding(
              List.of(
                  Coding.builder()
                      .system("http://loinc.org")
                      .code("29463-7")
                      .display("Body weight")
                      .build()))
          .build();
    }
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
              .qualifiers(
                  List.of(Vitals.Qualifier.builder().vuid("4711345").name("ACTUAL").build()))
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

    public VprGetPatientData.Response.Results resultsWithLab() {
      return VprGetPatientData.Response.Results.builder()
          .version("1.13")
          .timeZone("-0500")
          .vitals(Vitals.builder().total(1).vitalResults(vitals()).build())
          .labs(
              Labs.builder()
                  .total(1)
                  .labResults(ObservationLabSamples.Vista.create().labs())
                  .build())
          .build();
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
}
