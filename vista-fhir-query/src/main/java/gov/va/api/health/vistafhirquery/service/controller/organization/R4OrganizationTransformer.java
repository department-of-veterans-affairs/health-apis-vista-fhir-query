package gov.va.api.health.vistafhirquery.service.controller.organization;

import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.allBlank;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.asCodeableConcept;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.emptyToNull;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.isBlank;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.providerCoordinateStringFrom;
import static gov.va.api.health.vistafhirquery.service.controller.RpcGatewayTransformers.isInternalValueNotBlank;
import static gov.va.api.health.vistafhirquery.service.controller.RpcGatewayTransformers.yesNoToBoolean;
import static gov.va.api.health.vistafhirquery.service.controller.organization.OrganizationCoordinates.insuranceCompany;

import gov.va.api.health.r4.api.datatypes.Address;
import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import gov.va.api.health.r4.api.datatypes.ContactPoint;
import gov.va.api.health.r4.api.elements.Extension;
import gov.va.api.health.r4.api.elements.Reference;
import gov.va.api.health.r4.api.resources.Organization;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.InsuranceCompany;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

@Builder
public class R4OrganizationTransformer {
  @NonNull Map.Entry<String, LhsLighthouseRpcGatewayResponse.Results> rpcResults;

  @NonNull String patientIcn;

  private Address address(
      String streetAddressLine1,
      String streetAddressLine2,
      String streetAddressLine3,
      String city,
      String state,
      String zipCode) {
    if (allBlank(
        streetAddressLine1, streetAddressLine2, streetAddressLine3, city, state, zipCode)) {
      return null;
    }
    return Address.builder()
        .city(city)
        .state(state)
        .line(emptyToNull(List.of(streetAddressLine1, streetAddressLine2, streetAddressLine3)))
        .postalCode(zipCode)
        .text(
            Stream.of(
                    streetAddressLine1,
                    streetAddressLine2,
                    streetAddressLine3,
                    city,
                    state,
                    zipCode)
                .map(StringUtils::trimToNull)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" ")))
        .build();
  }

  private Organization.Contact appealsContact(LhsLighthouseRpcGatewayResponse.FilemanEntry entry) {
    return contact(
        entry.internal(InsuranceCompany.APPEALS_ADDRESS_ST_LINE_1_).orElse(null),
        entry.internal(InsuranceCompany.APPEALS_ADDRESS_ST_LINE_2_).orElse(null),
        entry.internal(InsuranceCompany.APPEALS_ADDRESS_ST_LINE_3_).orElse(null),
        entry.internal(InsuranceCompany.APPEALS_ADDRESS_CITY).orElse(null),
        entry.internal(InsuranceCompany.APPEALS_ADDRESS_STATE).orElse(null),
        entry.internal(InsuranceCompany.APPEALS_ADDRESS_ZIP).orElse(null),
        null,
        entry.internal(InsuranceCompany.APPEALS_PHONE_NUMBER).orElse(null),
        entry.internal(InsuranceCompany.APPEALS_FAX).orElse(null),
        entry.internal(InsuranceCompany.APPEALS_COMPANY_NAME).orElse(null));
  }

  private Organization.Contact billingContact(LhsLighthouseRpcGatewayResponse.FilemanEntry entry) {
    return contact(
        null,
        null,
        null,
        null,
        null,
        null,
        "BILL",
        entry.internal(InsuranceCompany.BILLING_PHONE_NUMBER).orElse(null),
        null,
        entry.internal(InsuranceCompany.BILLING_COMPANY_NAME).orElse(null));
  }

  private Organization.Contact claimsDentalContact(
      LhsLighthouseRpcGatewayResponse.FilemanEntry entry) {
    return contact(
        entry.internal(InsuranceCompany.CLAIMS_DENTAL_STREET_ADDR_1).orElse(null),
        entry.internal(InsuranceCompany.CLAIMS_DENTAL_STREET_ADDR_2).orElse(null),
        null,
        entry.internal(InsuranceCompany.CLAIMS_DENTAL_PROCESS_CITY).orElse(null),
        entry.internal(InsuranceCompany.CLAIMS_DENTAL_PROCESS_STATE).orElse(null),
        entry.internal(InsuranceCompany.CLAIMS_DENTAL_PROCESS_ZIP).orElse(null),
        "DENTALCLAIM",
        entry.internal(InsuranceCompany.CLAIMS_DENTAL_PHONE_NUMBER).orElse(null),
        entry.internal(InsuranceCompany.CLAIMS_DENTAL_FAX).orElse(null),
        entry.internal(InsuranceCompany.CLAIMS_DENTAL_COMPANY_NAME).orElse(null));
  }

  private Organization.Contact claimsInptContact(
      LhsLighthouseRpcGatewayResponse.FilemanEntry entry) {
    return contact(
        entry.internal(InsuranceCompany.CLAIMS_INPT_STREET_ADDRESS_1).orElse(null),
        entry.internal(InsuranceCompany.CLAIMS_INPT_STREET_ADDRESS_2).orElse(null),
        entry.internal(InsuranceCompany.CLAIMS_INPT_STREET_ADDRESS_3).orElse(null),
        entry.internal(InsuranceCompany.CLAIMS_INPT_PROCESS_CITY).orElse(null),
        entry.internal(InsuranceCompany.CLAIMS_INPT_PROCESS_STATE).orElse(null),
        entry.internal(InsuranceCompany.CLAIMS_INPT_PROCESS_ZIP).orElse(null),
        "RXCLAIMS",
        entry.internal(InsuranceCompany.CLAIMS_RX_PHONE_NUMBER).orElse(null),
        entry.internal(InsuranceCompany.CLAIMS_RX_FAX).orElse(null),
        entry.internal(InsuranceCompany.CLAIMS_INPT_COMPANY_NAME).orElse(null));
  }

  private Organization.Contact claimsOptContact(
      LhsLighthouseRpcGatewayResponse.FilemanEntry entry) {
    return contact(
        entry.internal(InsuranceCompany.CLAIMS_OPT_STREET_ADDRESS_1).orElse(null),
        entry.internal(InsuranceCompany.CLAIMS_OPT_STREET_ADDRESS_2).orElse(null),
        entry.internal(InsuranceCompany.CLAIMS_OPT_STREET_ADDRESS_3).orElse(null),
        entry.internal(InsuranceCompany.CLAIMS_OPT_PROCESS_CITY).orElse(null),
        entry.internal(InsuranceCompany.CLAIMS_OPT_PROCESS_STATE).orElse(null),
        entry.internal(InsuranceCompany.CLAIMS_OPT_PROCESS_ZIP).orElse(null),
        "OUTPTCLAIMS",
        entry.internal(InsuranceCompany.CLAIMS_OPT_PHONE_NUMBER).orElse(null),
        entry.internal(InsuranceCompany.CLAIMS_OPT_FAX).orElse(null),
        entry.internal(InsuranceCompany.CLAIMS_OPT_COMPANY_NAME).orElse(null));
  }

  private Organization.Contact claimsRxContact(LhsLighthouseRpcGatewayResponse.FilemanEntry entry) {
    return contact(
        entry.internal(InsuranceCompany.CLAIMS_RX_STREET_ADDRESS_1).orElse(null),
        entry.internal(InsuranceCompany.CLAIMS_RX_STREET_ADDRESS_2).orElse(null),
        entry.internal(InsuranceCompany.CLAIMS_RX_STREET_ADDRESS_3).orElse(null),
        entry.internal(InsuranceCompany.CLAIMS_RX_CITY).orElse(null),
        entry.internal(InsuranceCompany.CLAIMS_RX_STATE).orElse(null),
        entry.internal(InsuranceCompany.CLAIMS_RX_ZIP).orElse(null),
        "RXCLAIMS",
        entry.internal(InsuranceCompany.CLAIMS_RX_PHONE_NUMBER).orElse(null),
        entry.internal(InsuranceCompany.CLAIMS_RX_FAX).orElse(null),
        entry.internal(InsuranceCompany.CLAIMS_RX_COMPANY_NAME).orElse(null));
  }

  private List<Address> collectAddress(LhsLighthouseRpcGatewayResponse.FilemanEntry entry) {
    return Collections.singletonList(
        address(
            entry.internal(InsuranceCompany.STREET_ADDRESS_LINE_1_).orElse(null),
            entry.internal(InsuranceCompany.STREET_ADDRESS_LINE_2_).orElse(null),
            entry.internal(InsuranceCompany.STREET_ADDRESS_LINE_3_).orElse(null),
            entry.internal(InsuranceCompany.CITY).orElse(null),
            entry.internal(InsuranceCompany.STATE).orElse(null),
            entry.internal(InsuranceCompany.ZIP_CODE).orElse(null)));
  }

  private Organization.Contact contact(
      String streetAddressLine1,
      String streetAddressLine2,
      String streetAddressLine3,
      String city,
      String state,
      String zipCode,
      String purpose,
      String phone,
      String fax,
      String companyName) {
    if (allBlank(
        streetAddressLine1,
        streetAddressLine2,
        streetAddressLine3,
        city,
        state,
        zipCode,
        purpose,
        phone,
        fax,
        companyName)) {
      return null;
    }
    return Organization.Contact.builder()
        .address(
            address(
                streetAddressLine1, streetAddressLine2, streetAddressLine3, city, state, zipCode))
        .purpose(asCodeableConcept(Coding.builder().code(purpose).display(purpose).build()))
        .telecom(contactTelecom(phone, fax))
        .extension(contactExtension(companyName))
        .build();
  }

  private List<Extension> contactExtension(String companyName) {
    return List.of(
        Extension.builder()
            .valueReference(Reference.builder().display(companyName).build())
            .url(
                "http://hl7.org/fhir/us/davinci-pdex-plan-net/StructureDefinition/via-intermediary")
            .build());
  }

  private List<ContactPoint> contactTelecom(String phone, String fax) {
    List<ContactPoint> telecoms = new ArrayList<>();
    if (!isBlank(phone)) {
      telecoms.add(
          ContactPoint.builder()
              .value(phone)
              .system(ContactPoint.ContactPointSystem.phone)
              .build());
    }
    if (!isBlank(fax)) {
      telecoms.add(
          ContactPoint.builder().value(fax).system(ContactPoint.ContactPointSystem.fax).build());
    }
    return telecoms;
  }

  private List<Organization.Contact> contacts(LhsLighthouseRpcGatewayResponse.FilemanEntry entry) {
    return List.of(
        appealsContact(entry),
        billingContact(entry),
        claimsDentalContact(entry),
        claimsInptContact(entry),
        claimsOptContact(entry),
        claimsRxContact(entry),
        inquiryContact(entry),
        precertificationContact(entry));
  }

  private List<Extension> extensions(Map<String, LhsLighthouseRpcGatewayResponse.Values> fields) {
    List<Extension> extensions = new ArrayList<>();

    var maybeBedsections = fields.get(InsuranceCompany.ALLOW_MULTIPLE_BEDSECTIONS);
    if (isInternalValueNotBlank(maybeBedsections)) {
      extensions.add(
          Extension.builder()
              .url("http://va.gov/fhir/StructureDefinition/organization-allowMultipleBedsections")
              .valueBoolean(yesNoToBoolean(maybeBedsections.in()))
              .build());
    }

    var maybeOneOptVisit = fields.get(InsuranceCompany.ONE_OPT_VISIT_ON_BILL_ONLY);
    if (isInternalValueNotBlank(maybeOneOptVisit)) {
      extensions.add(
          Extension.builder()
              .url("http://va.gov/fhir/StructureDefinition/organization-oneOutpatVisitOnBillOnly")
              .valueBoolean(yesNoToBoolean(maybeOneOptVisit.in()))
              .build());
    }

    var maybeAmbulatorySurgeryRevenueCode = fields.get(InsuranceCompany.AMBULATORY_SURG_REV_CODE);
    if (isInternalValueNotBlank(maybeAmbulatorySurgeryRevenueCode)) {
      extensions.add(
          Extension.builder()
              .url(
                  "http://va.gov/fhir/StructureDefinition/organization-ambulatorySurgeryRevenueCode")
              .valueCodeableConcept(
                  CodeableConcept.builder()
                      .coding(
                          List.of(
                              Coding.builder()
                                  .code(maybeAmbulatorySurgeryRevenueCode.in())
                                  .system("http://terminology.hl7.org/ValueSet/v2-0456")
                                  .build()))
                      .build())
              .build());
    }

    return extensions.isEmpty() ? null : extensions;
  }

  private Organization.Contact inquiryContact(LhsLighthouseRpcGatewayResponse.FilemanEntry entry) {
    return contact(
        entry.internal(InsuranceCompany.INQUIRY_ADDRESS_ST_LINE_1_).orElse(null),
        entry.internal(InsuranceCompany.INQUIRY_ADDRESS_ST_LINE_2_).orElse(null),
        entry.internal(InsuranceCompany.INQUIRY_ADDRESS_ST_LINE_3_).orElse(null),
        entry.internal(InsuranceCompany.INQUIRY_ADDRESS_CITY).orElse(null),
        entry.internal(InsuranceCompany.INQUIRY_ADDRESS_STATE).orElse(null),
        entry.internal(InsuranceCompany.INQUIRY_ADDRESS_ZIP_CODE).orElse(null),
        null,
        entry.internal(InsuranceCompany.INQUIRY_PHONE_NUMBER).orElse(null),
        entry.internal(InsuranceCompany.INQUIRY_FAX).orElse(null),
        entry.internal(InsuranceCompany.INQUIRY_COMPANY_NAME).orElse(null));
  }

  private List<CodeableConcept> insuranceCompanyType() {
    return List.of(
        asCodeableConcept(
            Coding.builder()
                .code("ins")
                .display("Insurance Company")
                .system("http://hl7.org/fhir/ValueSet/organization-type")
                .build()));
  }

  private List<ContactPoint> organizationTelecom(String phoneNumber) {
    if (isBlank(phoneNumber)) {
      return Collections.emptyList();
    }
    return Collections.singletonList(
        ContactPoint.builder()
            .value(phoneNumber)
            .system(ContactPoint.ContactPointSystem.phone)
            .build());
  }

  private Organization.Contact precertificationContact(
      LhsLighthouseRpcGatewayResponse.FilemanEntry entry) {
    return contact(
        null,
        null,
        null,
        null,
        null,
        null,
        "PRECERT",
        entry.internal(InsuranceCompany.PRECERTIFICATION_PHONE_NUMBER).orElse(null),
        null,
        entry.internal(InsuranceCompany.PRECERT_COMPANY_NAME).orElse(null));
  }

  /** Transform an RPC response to fhir. */
  public Stream<Organization> toFhir() {
    return rpcResults.getValue().results().stream()
        .filter(Objects::nonNull)
        .filter(r -> InsuranceCompany.FILE_NUMBER.equals(r.file()))
        .map(this::toOrganization)
        .filter(Objects::nonNull);
  }

  private Organization toOrganization(LhsLighthouseRpcGatewayResponse.FilemanEntry entry) {
    if (entry == null || isBlank(entry.fields())) {
      return null;
    }
    Map<String, LhsLighthouseRpcGatewayResponse.Values> fields = entry.fields();
    return Organization.builder()
        .id(
            providerCoordinateStringFrom(
                rpcResults.getKey(), insuranceCompany(entry.ien()).toString()))
        // TODO: MORE EXTENSIONS
        .extension(extensions(fields))
        .name(entry.internal(InsuranceCompany.NAME).orElse(null))
        .type(insuranceCompanyType())
        .address(collectAddress(entry))
        .contact(contacts(entry))
        .telecom(organizationTelecom(entry.internal(InsuranceCompany.PHONE_NUMBER).orElse(null)))
        .build();
  }
}
