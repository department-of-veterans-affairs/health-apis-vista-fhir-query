package gov.va.api.health.vistafhirquery.service.controller.coverage;

import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import gov.va.api.health.r4.api.datatypes.Period;
import gov.va.api.health.r4.api.elements.Extension;
import gov.va.api.health.r4.api.elements.Reference;
import gov.va.api.health.r4.api.resources.Coverage;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CoverageSamples {
  @NoArgsConstructor(staticName = "create")
  public static class VistaLhsLighthouseRpcGateway {
    private Map<String, LhsLighthouseRpcGatewayResponse.Values> fields() {
      Map<String, LhsLighthouseRpcGatewayResponse.Values> fields = new HashMap<>();
      fields.put("#.01", LhsLighthouseRpcGatewayResponse.Values.of("BCBS OF FL", "4"));
      fields.put("#.18", LhsLighthouseRpcGatewayResponse.Values.of("BCBS OF FL", "87"));
      fields.put("#.2", LhsLighthouseRpcGatewayResponse.Values.of("PRIMARY", "1"));
      fields.put("#2.01", LhsLighthouseRpcGatewayResponse.Values.of("0", "0"));
      fields.put("#2.1", LhsLighthouseRpcGatewayResponse.Values.of("3", "3"));
      fields.put("#3", LhsLighthouseRpcGatewayResponse.Values.of("JAN 01, 2025", "3250101"));
      fields.put("#3.04", LhsLighthouseRpcGatewayResponse.Values.of("1", "1"));
      fields.put("#4.03", LhsLighthouseRpcGatewayResponse.Values.of("SPOUSE", "01"));
      fields.put("#4.06", LhsLighthouseRpcGatewayResponse.Values.of("67890", "67890"));
      fields.put("#5.01", LhsLighthouseRpcGatewayResponse.Values.of("13579", "13579"));
      fields.put("#7.02", LhsLighthouseRpcGatewayResponse.Values.of("R50797108", "R50797108"));
      fields.put("#8", LhsLighthouseRpcGatewayResponse.Values.of("JAN 12, 1992", "2920112"));
      return Map.copyOf(fields);
    }

    LhsLighthouseRpcGatewayResponse.Results getsManifestResults() {
      return LhsLighthouseRpcGatewayResponse.Results.builder()
          .results(
              List.of(
                  LhsLighthouseRpcGatewayResponse.FilemanEntry.builder()
                      .file("2.312")
                      .ien("1,8,")
                      .fields(fields())
                      .build()))
          .build();
    }
  }

  @NoArgsConstructor(staticName = "create")
  public static class R4 {
    private List<Coverage.CoverageClass> classes(String station, String patient) {
      return List.of(
          Coverage.CoverageClass.builder()
              .value(patient + "^" + station + "^87")
              .type(
                  CodeableConcept.builder()
                      .coding(
                          List.of(
                              Coding.builder()
                                  .system("http://terminology.hl7.org/CodeSystem/coverage-class")
                                  .code("group")
                                  .build()))
                      .build())
              .build());
    }

    Coverage coverage() {
      return coverage("666", "1010101010V666666");
    }

    Coverage coverage(String station, String patient) {
      return Coverage.builder()
          .id(patient + "^" + station + "^1,8,")
          .extension(extensions())
          .status(Coverage.Status.active)
          .subscriberId("R50797108")
          .beneficiary(Reference.builder().reference("Patient/" + patient).build())
          .relationship(relationship())
          .period(period())
          .payor(
              List.of(
                  Reference.builder()
                      .reference("Organization/" + patient + "^" + station + "^36;4")
                      .build()))
          .coverageClass(classes(station, patient))
          .order(1)
          .build();
    }

    private List<Extension> extensions() {
      return List.of(
          Extension.builder()
              .url("http://va.gov/fhir/StructureDefinition/coverage-pharmacyPersonCode")
              .valueInteger(67890)
              .build(),
          Extension.builder()
              .url("http://va.gov/fhir/StructureDefinition/coverage-stopPolicyFromBilling")
              .valueBoolean(true)
              .build());
    }

    private Period period() {
      return Period.builder().start("1992-01-12T00:00Z").end("2025-01-01T00:00Z").build();
    }

    private CodeableConcept relationship() {
      return CodeableConcept.builder()
          .coding(
              List.of(
                  Coding.builder()
                      .system("http://terminology.hl7.org/CodeSystem/subscriber-relationship")
                      .code("spouse")
                      .display("Spouse")
                      .build()))
          .build();
    }
  }
}
