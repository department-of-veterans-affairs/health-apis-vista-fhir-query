package gov.va.api.health.vistafhirquery.service.controller.coverage;

import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.allBlank;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.isBlank;

import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import gov.va.api.health.r4.api.datatypes.Period;
import gov.va.api.health.r4.api.elements.Extension;
import gov.va.api.health.r4.api.elements.Reference;
import gov.va.api.health.r4.api.resources.Coverage;
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

  GetInsRpcResults vista;

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
    if (isBlank(vista.insTypeGroupPlan())) {
      return null;
    }
    return List.of(
        Coverage.CoverageClass.builder()
            .value(vista.insTypeGroupPlan().externalValueRepresentation())
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
    if (!isBlank(vista.insTypePharmacyPersonCode())) {
      try {
        extensions.add(
            Extension.builder()
                .url("http://va.gov/fhir/StructureDefinition/coverage-pharmacyPersonCode")
                .valueInteger(
                    Integer.parseInt(
                        vista.insTypePharmacyPersonCode().externalValueRepresentation()))
                .build());
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException(
            "Bad VistA pharmacy person code: " + vista.insTypePharmacyPersonCode());
      }
    }
    if (!isBlank(vista.insTypeStopPolicyFromBilling())) {
      extensions.add(
          Extension.builder()
              .url("http://va.gov/fhir/StructureDefinition/coverage-stopPolicyFromBilling")
              .valueBoolean(
                  yesNo(vista.insTypeStopPolicyFromBilling().internalValueRepresentation()))
              .build());
    }
    if (extensions.isEmpty()) {
      return null;
    }
    return extensions;
  }

  private Integer order() {
    if (isBlank(vista.insTypeCoordinationOfBenefits())) {
      return null;
    }
    try {
      return Integer.parseInt(vista.insTypeCoordinationOfBenefits().externalValueRepresentation());
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(
          "Unable to determine coverage order from: " + vista.insTypeCoordinationOfBenefits());
    }
  }

  private List<Reference> payors() {
    if (isBlank(vista.insTypeInsuranceType())) {
      return null;
    }
    // ToDo this needs more parts for easier identification
    return List.of(
        Reference.builder()
            .reference("Coverage/" + vista.insTypeInsuranceType().internalValueRepresentation())
            .build());
  }

  private Period period() {
    Period period = Period.builder().build();
    if (!isBlank(vista.insTypeEffectiveDateOfPolicy())) {
      period.start(vista.insTypeEffectiveDateOfPolicy().externalValueRepresentation());
    }
    if (!isBlank(vista.insTypeInsuranceExpirationDate())) {
      period.end(vista.insTypeInsuranceExpirationDate().externalValueRepresentation());
    }
    if (allBlank(period.start(), period.end())) {
      return null;
    }
    return period;
  }

  private CodeableConcept relationship() {
    if (isBlank(vista.insTypePtRelationshipHipaa())) {
      return null;
    }
    var fhirTerm =
        RELATIONSHIP_MAPPING.get(vista.insTypePtRelationshipHipaa().internalValueRepresentation());
    if (isBlank(fhirTerm)) {
      throw new IllegalArgumentException(
          "Unknown Vista Relationship Code: " + vista.insTypePtRelationshipHipaa());
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
    if (isBlank(vista.insTypeSubscriberId())) {
      return null;
    }
    return vista.insTypeSubscriberId().externalValueRepresentation();
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
