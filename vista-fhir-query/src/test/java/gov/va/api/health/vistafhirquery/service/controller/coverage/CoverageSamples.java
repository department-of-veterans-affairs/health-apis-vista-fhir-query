package gov.va.api.health.vistafhirquery.service.controller.coverage;

import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import gov.va.api.health.r4.api.datatypes.Period;
import gov.va.api.health.r4.api.elements.Extension;
import gov.va.api.health.r4.api.elements.Reference;
import gov.va.api.health.r4.api.resources.Coverage;
import gov.va.api.lighthouse.charon.models.iblhsamcmsgetins.GetInsEntry;
import gov.va.api.lighthouse.charon.models.iblhsamcmsgetins.GetInsRpcResults;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CoverageSamples {
  @NoArgsConstructor(staticName = "create")
  public static class VistaIblhsGetInsRpc {
    private GetInsEntry entry(String fieldNumber, String internalRep, String externalRep) {
      return GetInsEntry.builder()
          .fileNumber("2.312")
          .ien("1")
          .fieldNumber(fieldNumber)
          .internalValueRepresentation(internalRep)
          .externalValueRepresentation(externalRep)
          .build();
    }

    GetInsRpcResults getInsResults() {
      return GetInsRpcResults.builder()
          .insTypeInsuranceType(entry(".01", "TYPE", "type"))
          .insTypeGroupPlan(entry(".18", "12345", "12345"))
          .insTypeCoordinationOfBenefits(entry(".2", "1", "1"))
          .insTypeSendBillToEmployer(entry("2.01", "0", "0"))
          .insTypeEsghp(entry("2.1", "3", "3"))
          .insTypeInsuranceExpirationDate(entry("3", "3210621.1542", "2021-06-21T15:42:00Z"))
          .insTypeStopPolicyFromBilling(entry("3.04", "1", "1"))
          .insTypePtRelationshipHipaa(
              entry("4.03", "HIPAA 53 LIFE PARTNER", "HIPAA 53 LIFE PARTNER"))
          .insTypePharmacyPersonCode(entry("4.06", "67890", "67890"))
          .insTypePatientId(entry("5.01", "13579", "13579"))
          .insTypeSubscriberId(entry("7.02", "24680", "24680"))
          .insTypeEffectiveDateOfPolicy(entry("8", "3210120.2136", "2021-01-20T21:36:00Z"))
          .build();
    }
  }

  @NoArgsConstructor(staticName = "create")
  public static class R4 {
    private List<Coverage.CoverageClass> classes() {
      return List.of(
          Coverage.CoverageClass.builder()
              .value("12345")
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
          .id(patient + "^" + station)
          .extension(extensions())
          .status(Coverage.Status.active)
          .subscriberId("24680")
          .beneficiary(Reference.builder().reference("Patient/" + patient).build())
          .relationship(relationship())
          .period(period())
          .payor(List.of(Reference.builder().reference("Organization/TYPE").build()))
          .coverageClass(classes())
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
      return Period.builder().start("2021-01-20T21:36:00Z").end("2021-06-21T15:42:00Z").build();
    }

    private CodeableConcept relationship() {
      return CodeableConcept.builder()
          .coding(
              List.of(
                  Coding.builder()
                      .system("http://terminology.hl7.org/CodeSystem/subscriber-relationship")
                      .code("common")
                      .display("Common Law Spouse")
                      .build()))
          .build();
    }
  }
}
