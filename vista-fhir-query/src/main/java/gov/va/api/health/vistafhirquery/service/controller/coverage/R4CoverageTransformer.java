package gov.va.api.health.vistafhirquery.service.controller.coverage;

import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.allBlank;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.isBlank;

import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import gov.va.api.health.r4.api.datatypes.Period;
import gov.va.api.health.r4.api.elements.Extension;
import gov.va.api.health.r4.api.elements.Reference;
import gov.va.api.health.r4.api.resources.Coverage;
import gov.va.api.lighthouse.charon.models.iblhsamcmsgetins.GetInsEntry;
import gov.va.api.lighthouse.charon.models.iblhsamcmsgetins.GetInsRpcResults;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public class R4CoverageTransformer {
  private static final Map<String, Coding> RELATIONSHIP_MAPPING =
      Map.of(
          "HIPAA 18 SELF",
          codeAndDisplay("self", "Self"),
          "HIPAA 01 SPOUSE",
          codeAndDisplay("spouse", "Spouse"),
          "HIPAA 19 CHILD",
          codeAndDisplay("child", "Child"),
          "HIPAA 41 INJURED PLAINTIFF",
          codeAndDisplay("injured", "Injured Party"),
          "HIPAA 32 MOTHER",
          codeAndDisplay("parent", "Parent"),
          "HIPAA 33 FATHER",
          codeAndDisplay("parent", "Parent"),
          "HIPAA 53 LIFE PARTNER",
          codeAndDisplay("common", "Common Law Spouse"),
          "HIPAA G8 OTHER RELATIONSHIP",
          codeAndDisplay("other", "Other"));

  Map.Entry<String, GetInsRpcResults> rpcResult;

  String patientIcn;

  private static Coding codeAndDisplay(String code, String display) {
    return Coding.builder().code(code).display(display).build();
  }

  private Reference beneficiary() {
    if (isBlank(patientIcn)) {
      return null;
    }
    return Reference.builder().reference("Patient/" + patientIcn).build();
  }

  private List<Coverage.CoverageClass> classes() {
    if (isEntryBlank(rpcResult.getValue().insTypeGroupPlan())) {
      return null;
    }
    return List.of(
        Coverage.CoverageClass.builder()
            .value(rpcResult.getValue().insTypeGroupPlan().externalValueRepresentation())
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

  private List<Extension> extensions() {
    // ToDo update urls (needs to substitute host/base-path per env)
    List<Extension> extensions = new ArrayList<>();
    if (!isEntryBlank(rpcResult.getValue().insTypePharmacyPersonCode())) {
      try {
        extensions.add(
            Extension.builder()
                .url("http://va.gov/fhir/StructureDefinition/coverage-pharmacyPersonCode")
                .valueInteger(
                    Integer.parseInt(
                        rpcResult
                            .getValue()
                            .insTypePharmacyPersonCode()
                            .externalValueRepresentation()))
                .build());
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException(
            "Bad VistA pharmacy person code: " + rpcResult.getValue().insTypePharmacyPersonCode());
      }
    }
    if (!isEntryBlank(rpcResult.getValue().insTypeStopPolicyFromBilling())) {
      extensions.add(
          Extension.builder()
              .url("http://va.gov/fhir/StructureDefinition/coverage-stopPolicyFromBilling")
              .valueBoolean(
                  yesNo(
                      rpcResult
                          .getValue()
                          .insTypeStopPolicyFromBilling()
                          .internalValueRepresentation()))
              .build());
    }
    if (extensions.isEmpty()) {
      return null;
    }
    return extensions;
  }

  private boolean isEntryBlank(GetInsEntry insEntry) {
    return isBlank(insEntry) || isBlank(insEntry.internalValueRepresentation());
  }

  private Integer order() {
    if (isEntryBlank(rpcResult.getValue().insTypeCoordinationOfBenefits())) {
      return null;
    }
    try {
      return Integer.parseInt(
          rpcResult.getValue().insTypeCoordinationOfBenefits().externalValueRepresentation());
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(
          "Unable to determine coverage order from: "
              + rpcResult.getValue().insTypeCoordinationOfBenefits());
    }
  }

  private List<Reference> payors() {
    if (isEntryBlank(rpcResult.getValue().insTypeInsuranceType())) {
      return null;
    }
    // ToDo this needs more parts for easier identification
    return List.of(
        Reference.builder()
            .reference(
                "Coverage/"
                    + rpcResult.getValue().insTypeInsuranceType().internalValueRepresentation())
            .build());
  }

  private Period period() {
    Period period = Period.builder().build();
    if (!isEntryBlank(rpcResult.getValue().insTypeEffectiveDateOfPolicy())) {
      period.start(
          rpcResult.getValue().insTypeEffectiveDateOfPolicy().externalValueRepresentation());
    }
    if (!isEntryBlank(rpcResult.getValue().insTypeInsuranceExpirationDate())) {
      period.end(
          rpcResult.getValue().insTypeInsuranceExpirationDate().externalValueRepresentation());
    }
    if (allBlank(period.start(), period.end())) {
      return null;
    }
    return period;
  }

  private CodeableConcept relationship() {
    if (isEntryBlank(rpcResult.getValue().insTypePtRelationshipHipaa())) {
      return null;
    }
    var fhirTerm =
        RELATIONSHIP_MAPPING.get(
            rpcResult.getValue().insTypePtRelationshipHipaa().internalValueRepresentation());
    if (isBlank(fhirTerm)) {
      throw new IllegalArgumentException(
          "Unknown Vista Relationship Code: " + rpcResult.getValue().insTypePtRelationshipHipaa());
    }
    return CodeableConcept.builder()
        .coding(
            List.of(
                Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/subscriber-relationship")
                    .code(fhirTerm.code())
                    .display(fhirTerm.display())
                    .build()))
        .build();
  }

  private String subscriberId() {
    if (isEntryBlank(rpcResult.getValue().insTypeSubscriberId())) {
      return null;
    }
    return rpcResult.getValue().insTypeSubscriberId().externalValueRepresentation();
  }

  /** Transform an RPC response to fhir. */
  public Coverage toFhir() {
    return Coverage.builder()
        .extension(extensions())
        .status(Coverage.Status.active)
        .subscriberId(subscriberId())
        .beneficiary(beneficiary())
        .relationship(relationship())
        .period(period())
        .payor(payors())
        .coverageClass(classes())
        .order(order())
        .build();
  }

  private boolean yesNo(String zeroOrOne) {
    switch (zeroOrOne) {
      case "0":
        return false;
      case "1":
        return true;
      default:
        throw new IllegalArgumentException("Unknown Yes/No code: " + zeroOrOne);
    }
  }
}
