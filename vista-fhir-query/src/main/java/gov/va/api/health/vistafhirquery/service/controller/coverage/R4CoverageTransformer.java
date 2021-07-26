package gov.va.api.health.vistafhirquery.service.controller.coverage;

import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.allBlank;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.isBlank;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.patientCoordinateStringFrom;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.providerCoordinateStringFrom;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.toReference;

import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import gov.va.api.health.r4.api.datatypes.Period;
import gov.va.api.health.r4.api.elements.Extension;
import gov.va.api.health.r4.api.elements.Reference;
import gov.va.api.health.r4.api.resources.Coverage;
import gov.va.api.health.vistafhirquery.service.controller.organization.OrganizationCoordinates;
import gov.va.api.lighthouse.charon.models.FilemanDate;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.InsuranceType;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayResponse;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayResponse.FilemanEntry;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayResponse.UnexpectedVistaValue;
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

  @SuppressWarnings("UnnecessaryParentheses")
  static boolean stopPolicyFromBillingToBoolean(String value) {
    return switch (value) {
      case "0" -> false;
      case "1" -> true;
      default -> throw new UnexpectedVistaValue(
          InsuranceType.STOP_POLICY_FROM_BILLING, value, "Expected 0 or 1");
    };
  }

  private List<Coverage.CoverageClass> classes(FilemanEntry entry) {
    return entry
        .internal(InsuranceType.GROUP_PLAN)
        .map(value -> providerCoordinateStringFrom(rpcResults.getKey(), value))
        .map(coords -> Coverage.CoverageClass.builder().value(coords).type(coverageClass()).build())
        .map(List::of)
        .orElse(null);
  }

  private CodeableConcept coverageClass() {
    return CodeableConcept.builder()
        .coding(
            List.of(
                Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/coverage-class")
                    .code("group")
                    .build()))
        .build();
  }

  private List<Extension> extensions(FilemanEntry entry) {
    // ToDo update urls (needs to substitute host/base-path per env) and use the correct host
    List<Extension> extensions = new ArrayList<>(2);
    entry
        .internal(InsuranceType.PHARMACY_PERSON_CODE, Integer::valueOf)
        .map(
            value ->
                Extension.builder()
                    .url("http://va.gov/fhir/StructureDefinition/coverage-pharmacyPersonCode")
                    .valueInteger(value)
                    .build())
        .ifPresent(extensions::add);
    entry
        .internal(
            InsuranceType.STOP_POLICY_FROM_BILLING,
            R4CoverageTransformer::stopPolicyFromBillingToBoolean)
        .map(
            value ->
                Extension.builder()
                    .url("http://va.gov/fhir/StructureDefinition/coverage-stopPolicyFromBilling")
                    .valueBoolean(value)
                    .build())
        .ifPresent(extensions::add);
    return extensions.isEmpty() ? null : extensions;
  }

  private Integer order(FilemanEntry entry) {
    return entry.internal(InsuranceType.COORDINATION_OF_BENEFITS, Integer::valueOf).orElse(null);
  }

  private List<Reference> payors(FilemanEntry entry) {
    return entry
        .internal(InsuranceType.INSURANCE_TYPE)
        .map(OrganizationCoordinates::insuranceCompany)
        .map(ic -> providerCoordinateStringFrom(rpcResults.getKey(), ic.toString()))
        .map(coords -> toReference("Organization", coords, null))
        .map(List::of)
        .orElse(null);
  }

  private Period period(FilemanEntry entry) {
    Period period = Period.builder().build();
    entry
        .internal(InsuranceType.EFFECTIVE_DATE_OF_POLICY, this::toFilemanDate)
        .ifPresent(period::start);
    entry
        .internal(InsuranceType.INSURANCE_EXPIRATION_DATE, this::toFilemanDate)
        .ifPresent(period::end);
    if (allBlank(period.start(), period.end())) {
      return null;
    }
    return period;
  }

  @SuppressWarnings("UnnecessaryParentheses")
  CodeableConcept relationship(LhsLighthouseRpcGatewayResponse.FilemanEntry entry) {
    return entry
        .internal(InsuranceType.PT_RELATIONSHIP_HIPAA, this::relationshipCoding)
        .map(coding -> CodeableConcept.builder().coding(List.of(coding)).build())
        .orElse(null);
  }

  @SuppressWarnings("UnnecessaryParentheses")
  private Coding relationshipCoding(String internalValue) {
    var coding =
        Coding.builder().system("http://terminology.hl7.org/CodeSystem/subscriber-relationship");
    return switch (internalValue) {
      case "01" -> coding.code("spouse").display("Spouse").build();
      case "18" -> coding.code("self").display("Self").build();
      case "19" -> coding.code("child").display("Child").build();
      case "32", "33" -> coding.code("parent").display("Parent").build();
      case "41" -> coding.code("injured").display("Injured Party").build();
      case "53" -> coding.code("common").display("Common Law Spouse").build();
      case "G8" -> coding.code("other").display("Other").build();
      default -> throw new UnexpectedVistaValue(
          InsuranceType.PT_RELATIONSHIP_HIPAA, internalValue, "Unknown relation coding");
    };
  }

  private Coverage toCoverage(LhsLighthouseRpcGatewayResponse.FilemanEntry entry) {
    if (isBlank(entry.fields())) {
      return null;
    }
    return Coverage.builder()
        .id(patientCoordinateStringFrom(patientIcn, rpcResults.getKey(), entry.ien()))
        .extension(extensions(entry))
        .status(Coverage.Status.active)
        .subscriberId(entry.external(InsuranceType.SUBSCRIBER_ID).orElse(null))
        .beneficiary(toReference("Patient", patientIcn, null))
        .relationship(relationship(entry))
        .period(period(entry))
        .payor(payors(entry))
        .coverageClass(classes(entry))
        .order(order(entry))
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

  private String toFilemanDate(String filemanDate) {
    if (filemanDate == null) {
      return null;
    }
    // Reformat to UTC
    return FilemanDate.from(filemanDate, vistaZoneId).instant().atZone(ZoneOffset.UTC).toString();
  }
}
