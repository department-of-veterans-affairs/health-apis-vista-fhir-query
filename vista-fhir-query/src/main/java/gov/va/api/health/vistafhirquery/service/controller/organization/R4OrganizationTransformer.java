package gov.va.api.health.vistafhirquery.service.controller.organization;

import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.allBlank;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.asCodeableConcept;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.emptyToNull;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.isBlank;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.providerCoordinateStringFrom;
import static gov.va.api.health.vistafhirquery.service.controller.organization.OrganizationCoordinates.insuranceCompany;
import static java.util.Collections.emptyList;

import gov.va.api.health.r4.api.datatypes.Address;
import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import gov.va.api.health.r4.api.datatypes.ContactPoint;
import gov.va.api.health.r4.api.datatypes.Identifier;
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
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.NonNull;

@Builder
public class R4OrganizationTransformer {
  static final Map<String, Boolean> YES_NO = Map.of("1", true, "0", false);

  /** The insurance company fields needed by the transformer. */
  public static List<String> REQUIRED_FIELDS =
      List.of(
          InsuranceCompany.ALLOW_MULTIPLE_BEDSECTIONS,
          InsuranceCompany.AMBULATORY_SURG_REV_CODE,
          InsuranceCompany.ANOTHER_CO_PROCESS_IP_CLAIMS_,
          InsuranceCompany.APPEALS_ADDRESS_CITY,
          InsuranceCompany.APPEALS_ADDRESS_STATE,
          InsuranceCompany.APPEALS_ADDRESS_ST_LINE_1_,
          InsuranceCompany.APPEALS_ADDRESS_ST_LINE_2_,
          InsuranceCompany.APPEALS_ADDRESS_ST_LINE_3_,
          InsuranceCompany.APPEALS_ADDRESS_ZIP,
          InsuranceCompany.APPEALS_COMPANY_NAME,
          InsuranceCompany.APPEALS_FAX,
          InsuranceCompany.APPEALS_PHONE_NUMBER,
          InsuranceCompany.BILLING_COMPANY_NAME,
          InsuranceCompany.BILLING_PHONE_NUMBER,
          InsuranceCompany.CITY,
          InsuranceCompany.CLAIMS_DENTAL_COMPANY_NAME,
          InsuranceCompany.CLAIMS_DENTAL_FAX,
          InsuranceCompany.CLAIMS_DENTAL_PHONE_NUMBER,
          InsuranceCompany.CLAIMS_DENTAL_PROCESS_CITY,
          InsuranceCompany.CLAIMS_DENTAL_PROCESS_STATE,
          InsuranceCompany.CLAIMS_DENTAL_PROCESS_ZIP,
          InsuranceCompany.CLAIMS_DENTAL_STREET_ADDR_1,
          InsuranceCompany.CLAIMS_DENTAL_STREET_ADDR_2,
          InsuranceCompany.CLAIMS_INPT_COMPANY_NAME,
          InsuranceCompany.CLAIMS_INPT_PROCESS_CITY,
          InsuranceCompany.CLAIMS_INPT_PROCESS_STATE,
          InsuranceCompany.CLAIMS_INPT_PROCESS_ZIP,
          InsuranceCompany.CLAIMS_INPT_STREET_ADDRESS_1,
          InsuranceCompany.CLAIMS_INPT_STREET_ADDRESS_2,
          InsuranceCompany.CLAIMS_INPT_STREET_ADDRESS_3,
          InsuranceCompany.CLAIMS_OPT_COMPANY_NAME,
          InsuranceCompany.CLAIMS_OPT_FAX,
          InsuranceCompany.CLAIMS_OPT_PHONE_NUMBER,
          InsuranceCompany.CLAIMS_OPT_PROCESS_CITY,
          InsuranceCompany.CLAIMS_OPT_PROCESS_STATE,
          InsuranceCompany.CLAIMS_OPT_PROCESS_ZIP,
          InsuranceCompany.CLAIMS_OPT_STREET_ADDRESS_1,
          InsuranceCompany.CLAIMS_OPT_STREET_ADDRESS_2,
          InsuranceCompany.CLAIMS_OPT_STREET_ADDRESS_3,
          InsuranceCompany.CLAIMS_RX_CITY,
          InsuranceCompany.CLAIMS_RX_COMPANY_NAME,
          InsuranceCompany.CLAIMS_RX_FAX,
          InsuranceCompany.CLAIMS_RX_PHONE_NUMBER,
          InsuranceCompany.CLAIMS_RX_STATE,
          InsuranceCompany.CLAIMS_RX_STREET_ADDRESS_1,
          InsuranceCompany.CLAIMS_RX_STREET_ADDRESS_2,
          InsuranceCompany.CLAIMS_RX_STREET_ADDRESS_3,
          InsuranceCompany.CLAIMS_RX_ZIP,
          InsuranceCompany.FILE_NUMBER,
          InsuranceCompany.FILING_TIME_FRAME,
          InsuranceCompany.INACTIVE,
          InsuranceCompany.INQUIRY_ADDRESS_CITY,
          InsuranceCompany.INQUIRY_ADDRESS_STATE,
          InsuranceCompany.INQUIRY_ADDRESS_ST_LINE_1_,
          InsuranceCompany.INQUIRY_ADDRESS_ST_LINE_2_,
          InsuranceCompany.INQUIRY_ADDRESS_ST_LINE_3_,
          InsuranceCompany.INQUIRY_ADDRESS_ZIP_CODE,
          InsuranceCompany.INQUIRY_COMPANY_NAME,
          InsuranceCompany.INQUIRY_FAX,
          InsuranceCompany.INQUIRY_PHONE_NUMBER,
          InsuranceCompany.NAME,
          InsuranceCompany.ONE_OPT_VISIT_ON_BILL_ONLY,
          InsuranceCompany.PHONE_NUMBER,
          InsuranceCompany.PRECERTIFICATION_PHONE_NUMBER,
          InsuranceCompany.PRECERT_COMPANY_NAME,
          InsuranceCompany.STATE,
          InsuranceCompany.STREET_ADDRESS_LINE_1_,
          InsuranceCompany.STREET_ADDRESS_LINE_2_,
          InsuranceCompany.STREET_ADDRESS_LINE_3_,
          InsuranceCompany.ZIP_CODE);

  @NonNull Map.Entry<String, LhsLighthouseRpcGatewayResponse.Results> rpcResults;

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
        .line(
            emptyToNull(
                Stream.of(streetAddressLine1, streetAddressLine2, streetAddressLine3)
                    .filter(Objects::nonNull)
                    .toList()))
        .postalCode(zipCode)
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
        "INPTCLAIMS",
        entry.internal(InsuranceCompany.CLAIMS_INPT_PHONE_NUMBER).orElse(null),
        entry.internal(InsuranceCompany.CLAIMS_INPT_FAX).orElse(null),
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

  private List<Extension> companyNameExtension(String companyName) {
    if (isBlank(companyName)) {
      return emptyList();
    }
    return List.of(
        Extension.builder()
            .valueReference(Reference.builder().display(companyName).build())
            .url(
                "http://hl7.org/fhir/us/davinci-pdex-plan-net/StructureDefinition/via-intermediary")
            .build());
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
        .telecom(contactTelecom(phone, fax))
        .extension(companyNameExtension(companyName))
        .purpose(purposeOrNull(purpose))
        .build();
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

  private List<Extension> extensions(LhsLighthouseRpcGatewayResponse.FilemanEntry entry) {

    ExtensionFactory extensions = ExtensionFactory.of(entry, YES_NO);

    return Stream.of(
            extensions.ofYesNoBoolean(
                InsuranceCompany.ALLOW_MULTIPLE_BEDSECTIONS,
                "http://va.gov/fhir/StructureDefinition/organization-allowMultipleBedsections"),
            extensions.ofYesNoBoolean(
                InsuranceCompany.ONE_OPT_VISIT_ON_BILL_ONLY,
                "http://va.gov/fhir/StructureDefinition/organization-oneOutpatVisitOnBillOnly"),
            extensions.ofCodeableConcept(
                InsuranceCompany.AMBULATORY_SURG_REV_CODE,
                "urn:oid:2.16.840.1.113883.6.301.3",
                "http://va.gov/fhir/StructureDefinition/organization-ambulatorySurgeryRevenueCode"),
            extensions.ofString(
                InsuranceCompany.FILING_TIME_FRAME,
                "http://va.gov/fhir/StructureDefinition/organization-filingTimeFrame"),
            extensions.ofYesNoBoolean(
                InsuranceCompany.ANOTHER_CO_PROCESS_IP_CLAIMS_,
                "http://va.gov/fhir/StructureDefinition/organization-anotherCompanyProcessesInpatClaims"),
            extensions.ofCodeableConcept(
                InsuranceCompany.TYPE_OF_COVERAGE,
                "urn:oid:2.16.840.1.113883.3.8901.3.36.8013",
                "http://va.gov/fhir/StructureDefinition/organization-typeOfCoverage"),
            extensions.ofYesNoBoolean(
                InsuranceCompany.ANOTHER_CO_PROCESS_APPEALS_,
                "http://va.gov/fhir/StructureDefinition/organization-anotherCompanyProcessesAppeals"),
            extensions.ofCodeableConcept(
                InsuranceCompany.PRESCRIPTION_REFILL_REV_CODE,
                "urn:oid:2.16.840.1.113883.6.301.3",
                "http://va.gov/fhir/StructureDefinition/organization-prescriptionRevenueCode"),
            extensions.ofYesNoBoolean(
                InsuranceCompany.ANOTHER_CO_PROCESS_INQUIRIES_,
                "http://va.gov/fhir/StructureDefinition/organization-anotherCompanyProcessesInquiries"),
            extensions.ofYesNoBoolean(
                InsuranceCompany.ANOTHER_CO_PROCESS_OP_CLAIMS_,
                "http://va.gov/fhir/StructureDefinition/organization-anotherCompanyProcessesOutpatClaims"),
            extensions.ofYesNoBoolean(
                InsuranceCompany.ANOTHER_CO_PROCESS_PRECERTS_,
                "http://va.gov/fhir/StructureDefinition/organization-anotherCompanyProcessesPrecert"),
            extensions.ofYesNoBoolean(
                InsuranceCompany.ANOTHER_CO_PROCESS_RX_CLAIMS_,
                "http://va.gov/fhir/StructureDefinition/organization-anotherCompanyProcessesRxClaims"),
            extensions.ofYesNoBoolean(
                InsuranceCompany.ANOTHER_CO_PROC_DENT_CLAIMS_,
                "http://va.gov/fhir/StructureDefinition/organization-anotherCompanyProcessesDentalClaims"),
            extensions.ofQuantity(
                InsuranceCompany.STANDARD_FTF_VALUE,
                "d",
                "urn:oid:2.16.840.1.113883.3.8901.3.3558013"),
            extensions.ofCodeableConcept(
                InsuranceCompany.REIMBURSE_,
                "urn:oid:2.16.840.1.113883.3.8901.3.36.1",
                "http://va.gov/fhir/StructureDefinition/organization-willReimburseForCare"),
            extensions.ofYesNoBoolean(
                InsuranceCompany.SIGNATURE_REQUIRED_ON_BILL_,
                "http://va.gov/fhir/StructureDefinition/organization-signatureRequiredOnBill"),
            extensions.ofCodeableConcept(
                InsuranceCompany.TRANSMIT_ELECTRONICALLY,
                "urn:oid:2.16.840.1.113883.3.8901.3.36.38001",
                "http://va.gov/fhir/StructureDefinition/organization-electronicTransmissionMode"),
            extensions.ofCodeableConcept(
                InsuranceCompany.ELECTRONIC_INSURANCE_TYPE,
                "urn:oid:2.16.840.1.113883.3.8901.3.36.38009",
                "http://va.gov/fhir/StructureDefinition/organization-electronicInsuranceType"),
            extensions.ofReference(
                InsuranceCompany.PAYER,
                "Organization",
                "http://hl7.org/fhir/us/davinci-pdex-plan-net/StructureDefinition/via-intermediary"),
            extensions.ofCodeableConcept(
                InsuranceCompany.PERF_PROV_SECOND_ID_TYPE_1500,
                "urn:oid:2.16.840.1.113883.3.8901.3.3558097.8001",
                "http://va.gov/fhir/StructureDefinition/organization-performingProviderSecondIDTypeCMS1500"),
            extensions.ofCodeableConcept(
                InsuranceCompany.PERF_PROV_SECOND_ID_TYPE_UB,
                "urn:oid:2.16.840.1.113883.3.8901.3.3558097.8001",
                "http://va.gov/fhir/StructureDefinition/organization-performingProviderSecondIDTypeUB04"),
            extensions.ofCodeableConcept(
                InsuranceCompany.REF_PROV_SEC_ID_DEF_CMS_1500,
                "urn:oid:2.16.840.1.113883.3.8901.3.3558097.8001",
                "http://va.gov/fhir/StructureDefinition/organization-referrngProviderSecondIDTypeCMS1500"),
            extensions.ofCodeableConcept(
                InsuranceCompany.REF_PROV_SEC_ID_REQ_ON_CLAIMS,
                "urn:oid:2.16.840.1.113883.3.8901.3.3558097.8001",
                "http://va.gov/fhir/StructureDefinition/organization-referrngProviderSecondIDTypeUB04"),
            extensions.ofYesNoBoolean(
                InsuranceCompany.ATT_REND_ID_BILL_SEC_ID_PROF,
                "http://va.gov/fhir/StructureDefinition/organization-attendingRenderingProviderSecondaryIDProfesionalRequired"),
            extensions.ofYesNoBoolean(
                InsuranceCompany.ATT_REND_ID_BILL_SEC_ID_INST,
                "http://va.gov/fhir/StructureDefinition/organization-attendingRenderingProviderSecondaryIDInstitutionalRequired"),
            extensions.ofYesNoBoolean(
                InsuranceCompany.PRINT_SEC_TERT_AUTO_CLAIMS_,
                "http://va.gov/fhir/StructureDefinition/organization-printSecTertAutoClaimsLocally"),
            extensions.ofYesNoBoolean(
                InsuranceCompany.PRINT_SEC_MED_CLAIMS_W_O_MRA_,
                "http://va.gov/fhir/StructureDefinition/organization-printSecMedClaimsWOMRALocally"))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private Identifier identifier(Optional<String> value, String code) {
    if (value.isEmpty()) {
      return null;
    }
    return Identifier.builder()
        .type(
            CodeableConcept.builder()
                .coding(List.of(Coding.builder().id(value.get()).code(code).build()))
                .build())
        .build();
  }

  private List<Identifier> identifiers(LhsLighthouseRpcGatewayResponse.FilemanEntry entry) {
    return Stream.of(
            identifier(entry.internal(InsuranceCompany.EDI_ID_NUMBER_PROF), "PROFEDI"),
            identifier(entry.internal(InsuranceCompany.EDI_ID_NUMBER_INST), "INSTEDI"),
            identifier(entry.internal(InsuranceCompany.BIN_NUMBER), "BIN"))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
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
      return emptyList();
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

  private CodeableConcept purposeOrNull(String purpose) {
    if (isBlank(purpose)) {
      return null;
    }
    return asCodeableConcept(
        Coding.builder()
            .code(purpose)
            .display(purpose)
            .system("http://terminology.hl7.org/CodeSystem/contactentity-type")
            .build());
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
    return Organization.builder()
        .id(
            providerCoordinateStringFrom(
                rpcResults.getKey(), insuranceCompany(entry.ien()).toString()))
        .extension(extensions(entry))
        .identifier(identifiers(entry))
        .active(entry.internal(InsuranceCompany.INACTIVE, YES_NO).map(value -> !value).orElse(null))
        .name(entry.internal(InsuranceCompany.NAME).orElse(null))
        .type(insuranceCompanyType())
        .address(collectAddress(entry))
        .contact(contacts(entry))
        .telecom(
            emptyToNull(
                organizationTelecom(entry.internal(InsuranceCompany.PHONE_NUMBER).orElse(null))))
        .build();
  }
}
