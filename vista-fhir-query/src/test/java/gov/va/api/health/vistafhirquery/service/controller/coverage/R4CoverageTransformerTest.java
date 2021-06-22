package gov.va.api.health.vistafhirquery.service.controller.coverage;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import gov.va.api.health.r4.api.datatypes.Period;
import gov.va.api.health.r4.api.elements.Extension;
import gov.va.api.health.r4.api.elements.Reference;
import gov.va.api.health.r4.api.resources.Coverage;
import gov.va.api.lighthouse.charon.models.iblhsamcmsgetins.GetInsEntry;
import gov.va.api.lighthouse.charon.models.iblhsamcmsgetins.GetInsRpcResults;
import java.util.List;
import org.junit.jupiter.api.Test;

public class R4CoverageTransformerTest {
  @Test
  void empty() {
    assertThat(R4CoverageTransformer.builder().vista(GetInsRpcResults.empty()).build().toFhir())
        .isEqualTo(Coverage.builder().build());
  }

  private GetInsEntry entry(String value) {
    return GetInsEntry.builder()
        .fileNumber("x")
        .ien("x")
        .fieldNumber("x")
        .internalValueRepresentation(value.toUpperCase())
        .externalValueRepresentation(value)
        .build();
  }

  @Test
  void toFhir() {
    var vistaSample =
        GetInsRpcResults.builder()
            .insTypeInsuranceType(entry("type"))
            .insTypeGroupPlan(entry("12345"))
            .insTypeCoordinationOfBenefits(entry("1"))
            .insTypeSendBillToEmployer(entry("0"))
            .insTypeEsghp(entry("3"))
            .insTypeInsuranceExpirationDate(entry("2021-06-21T15:42:00Z"))
            .insTypePtRelationshipHipaa(entry("HIPAA 53 LIFE PARTNER"))
            .insTypePharmacyPersonCode(entry("67890"))
            .insTypePatientId(entry("13579"))
            .insTypeSubscriberId(entry("24680"))
            .insTypeEffectiveDateOfPolicy(entry("2021-01-20T21:36:00Z"))
            .insTypeStopPolicyFromBilling(entry("1"))
            .build();
    assertThat(R4CoverageTransformer.builder().vista(vistaSample).build().toFhir())
        .isEqualTo(
            Coverage.builder()
                .extension(
                    List.of(
                        Extension.builder()
                            .url(
                                "http://va.gov/fhir/StructureDefinition/coverage-pharmacyPersonCode")
                            .valueInteger(67890)
                            .build(),
                        Extension.builder()
                            .url(
                                "http://va.gov/fhir/StructureDefinition/coverage-stopPolicyFromBilling")
                            .valueCodeableConcept(
                                CodeableConcept.builder()
                                    .coding(
                                        List.of(
                                            Coding.builder()
                                                .system(
                                                    "http://terminology.hl7.org/ValueSet/v2-0136")
                                                .code("Y")
                                                .display("Yes")
                                                .build()))
                                    .build())
                            .build()))
                .subscriberId("24680")
                .relationship(
                    CodeableConcept.builder()
                        .coding(
                            List.of(
                                Coding.builder()
                                    .system(
                                        "http://terminology.hl7.org/CodeSystem/subscriber-relationship")
                                    .code("common")
                                    .display("Common Law Spouse")
                                    .build()))
                        .build())
                .period(
                    Period.builder()
                        .start("2021-01-20T21:36:00Z")
                        .end("2021-06-21T15:42:00Z")
                        .build())
                .payor(List.of(Reference.builder().reference("Coverage/TYPE").build()))
                .coverageClass(
                    List.of(
                        Coverage.CoverageClass.builder()
                            .value("12345")
                            .type(
                                CodeableConcept.builder()
                                    .coding(
                                        List.of(
                                            Coding.builder()
                                                .system(
                                                    "http://terminology.hl7.org/CodeSystem/coverage-class")
                                                .code("group")
                                                .build()))
                                    .build())
                            .build()))
                .order(1)
                .build());
  }
}
