package gov.va.api.health.vistafhirquery.service.controller.coverage;

import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.allBlank;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.isBlank;

import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import gov.va.api.health.r4.api.datatypes.Period;
import gov.va.api.health.r4.api.elements.Extension;
import gov.va.api.health.r4.api.elements.Reference;
import gov.va.api.health.r4.api.resources.Coverage;
import gov.va.api.lighthouse.charon.models.FilemanDate;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.InsuranceType;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayResponse;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.NonNull;

@Builder
public class R4CoverageTransformer {
  @NonNull Map.Entry<String, LhsLighthouseRpcGatewayResponse.Results> rpcResults;

  @NonNull String patientIcn;

  /** Assumes UTC if zoneId is not provided. */
  @Builder.Default ZoneId vistaZoneId = ZoneOffset.UTC;

  private Reference beneficiary() {
    if (isBlank(patientIcn)) {
      return null;
    }
    return Reference.builder().reference("Patient/" + patientIcn).build();
  }

  private List<Coverage.CoverageClass> classes(LhsLighthouseRpcGatewayResponse.Values groupPlan) {
    if (isBlank(groupPlan) || isBlank(groupPlan.in())) {
      return null;
    }
    // Fhir InsurancePlan
    return List.of(
        Coverage.CoverageClass.builder()
            .value(patientIcn + "^" + rpcResults.getKey() + "^" + groupPlan.in())
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

  private List<Extension> extensions(
      LhsLighthouseRpcGatewayResponse.Values pharmacyPersonCode,
      LhsLighthouseRpcGatewayResponse.Values stopPolicyFromBilling) {
    // ToDo update urls (needs to substitute host/base-path per env) and use the correct host
    List<Extension> extensions = new ArrayList<>();
    if (!isBlank(pharmacyPersonCode) && !isBlank(pharmacyPersonCode.in())) {
      try {
        extensions.add(
            Extension.builder()
                .url("http://va.gov/fhir/StructureDefinition/coverage-pharmacyPersonCode")
                .valueInteger(Integer.parseInt(pharmacyPersonCode.in()))
                .build());
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Bad VistA pharmacy person code: " + pharmacyPersonCode);
      }
    }
    if (!isBlank(stopPolicyFromBilling) && !isBlank(stopPolicyFromBilling.in())) {
      extensions.add(
          Extension.builder()
              .url("http://va.gov/fhir/StructureDefinition/coverage-stopPolicyFromBilling")
              .valueBoolean(yesNo(stopPolicyFromBilling.in()))
              .build());
    }
    if (extensions.isEmpty()) {
      return null;
    }
    return extensions;
  }

  private String fromFilemanDate(String filemanDate) {
    if (filemanDate == null) {
      return null;
    }
    // Reformat to UTC
    return FilemanDate.from(filemanDate, vistaZoneId).instant().atZone(ZoneOffset.UTC).toString();
  }

  private Integer order(LhsLighthouseRpcGatewayResponse.Values coordinationOfBenefits) {
    if (isBlank(coordinationOfBenefits) || isBlank(coordinationOfBenefits.in())) {
      return null;
    }
    try {
      return Integer.parseInt(coordinationOfBenefits.in());
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(
          "Unable to determine coverage order from: " + coordinationOfBenefits);
    }
  }

  private List<Reference> payors(LhsLighthouseRpcGatewayResponse.Values insuranceCompany) {
    if (isBlank(insuranceCompany) || isBlank(insuranceCompany.in())) {
      return null;
    }
    /* The organization controller is likely to need to support Organization from both the
     * InsuranceCompany file _and_ the payer file. */
    return List.of(
        Reference.builder()
            .reference(
                "Organization/"
                    + patientIcn
                    + "^"
                    + rpcResults.getKey()
                    + "^36;"
                    + insuranceCompany.in())
            .build());
  }

  private Period period(
      LhsLighthouseRpcGatewayResponse.Values effectiveDate,
      LhsLighthouseRpcGatewayResponse.Values expirationDate) {
    Period period = Period.builder().build();
    if (!isBlank(effectiveDate) && !isBlank(effectiveDate.in())) {
      period.start(fromFilemanDate(effectiveDate.in()));
    }
    if (!isBlank(expirationDate) && !isBlank(expirationDate.in())) {
      period.end(fromFilemanDate(expirationDate.in()));
    }
    if (allBlank(period.start(), period.end())) {
      return null;
    }
    return period;
  }

  @SuppressWarnings("UnnecessaryParentheses")
  CodeableConcept relationship(LhsLighthouseRpcGatewayResponse.Values relationship) {
    if (isBlank(relationship) || isBlank(relationship.in())) {
      return null;
    }
    var relationshipCoding =
        Coding.builder().system("http://terminology.hl7.org/CodeSystem/subscriber-relationship");
    switch (relationship.in()) {
      case "01" -> relationshipCoding.code("spouse").display("Spouse");
      case "18" -> relationshipCoding.code("self").display("Self");
      case "19" -> relationshipCoding.code("child").display("Child");
      case "32", "33" -> relationshipCoding.code("parent").display("Parent");
      case "41" -> relationshipCoding.code("injured").display("Injured Party");
      case "53" -> relationshipCoding.code("common").display("Common Law Spouse");
      case "G8" -> relationshipCoding.code("other").display("Other");
      default -> throw new IllegalArgumentException(
          "Unknown Vista Relationship Code: " + relationship);
    }
    return CodeableConcept.builder().coding(List.of(relationshipCoding.build())).build();
  }

  private String subscriberId(LhsLighthouseRpcGatewayResponse.Values subscriberId) {
    if (isBlank(subscriberId) || isBlank(subscriberId.ext())) {
      return null;
    }
    return subscriberId.ext();
  }

  private Coverage toCoverage(LhsLighthouseRpcGatewayResponse.FilemanEntry entry) {
    if (isBlank(entry.fields())) {
      return null;
    }
    return Coverage.builder()
        .id(patientIcn + "^" + rpcResults.getKey() + "^" + entry.ien())
        .extension(
            extensions(
                entry.fields().get(InsuranceType.PHARMACY_PERSON_CODE),
                entry.fields().get(InsuranceType.STOP_POLICY_FROM_BILLING)))
        .status(Coverage.Status.active)
        .subscriberId(subscriberId(entry.fields().get(InsuranceType.SUBSCRIBER_ID)))
        .beneficiary(beneficiary())
        .relationship(relationship(entry.fields().get(InsuranceType.PT_RELATIONSHIP_HIPAA)))
        .period(
            period(
                entry.fields().get(InsuranceType.EFFECTIVE_DATE_OF_POLICY),
                entry.fields().get(InsuranceType.INSURANCE_EXPIRATION_DATE)))
        .payor(payors(entry.fields().get(InsuranceType.INSURANCE_TYPE)))
        .coverageClass(classes(entry.fields().get(InsuranceType.GROUP_PLAN)))
        .order(order(entry.fields().get(InsuranceType.COORDINATION_OF_BENEFITS)))
        .build();
  }

  /** Transform an RPC response to fhir. */
  public Stream<Coverage> toFhir() {
    return rpcResults.getValue().results().stream()
        .filter(Objects::nonNull)
        .filter(r -> InsuranceType.FILE_NUMBER.equals(r.file()))
        .map(this::toCoverage)
        .filter(Objects::nonNull);
  }

  @SuppressWarnings("UnnecessaryParentheses")
  boolean yesNo(String zeroOrOne) {
    return switch (zeroOrOne) {
      case "0" -> false;
      case "1" -> true;
      default -> throw new IllegalArgumentException("Unknown Yes/No code: " + zeroOrOne);
    };
  }
}
