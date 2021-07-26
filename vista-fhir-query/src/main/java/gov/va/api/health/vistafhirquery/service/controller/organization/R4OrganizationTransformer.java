package gov.va.api.health.vistafhirquery.service.controller.organization;

import gov.va.api.health.r4.api.datatypes.Address;
import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import gov.va.api.health.r4.api.datatypes.ContactPoint;
import gov.va.api.health.r4.api.elements.Extension;
import gov.va.api.health.r4.api.elements.Reference;
import gov.va.api.health.r4.api.resources.Organization;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.InsuranceCompany;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayResponse;
import lombok.Builder;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.allBlank;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.asCodeableConcept;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.emptyToNull;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.isBlank;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.providerCoordinateStringFrom;
import static gov.va.api.health.vistafhirquery.service.controller.RpcGatewayTransformers.internalValueOf;
import static gov.va.api.health.vistafhirquery.service.controller.RpcGatewayTransformers.isInternalValueNotBlank;
import static gov.va.api.health.vistafhirquery.service.controller.RpcGatewayTransformers.yesNoToBoolean;
import static gov.va.api.health.vistafhirquery.service.controller.organization.OrganizationCoordinates.insuranceCompany;

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
        .build();
  }

  private Organization.Contact appealsContact(
      Map<String, LhsLighthouseRpcGatewayResponse.Values> fields) {
    return contact(
        fields.get(InsuranceCompany.APPEALS_ADDRESS_ST_LINE_1_),
        fields.get(InsuranceCompany.APPEALS_ADDRESS_ST_LINE_2_),
        fields.get(InsuranceCompany.APPEALS_ADDRESS_ST_LINE_3_),
        fields.get(InsuranceCompany.APPEALS_ADDRESS_CITY),
        fields.get(InsuranceCompany.APPEALS_ADDRESS_STATE),
        fields.get(InsuranceCompany.APPEALS_ADDRESS_ZIP),
        null,
        fields.get(InsuranceCompany.APPEALS_PHONE_NUMBER),
        fields.get(InsuranceCompany.APPEALS_FAX),
        fields.get(InsuranceCompany.APPEALS_COMPANY_NAME));
  }

  private Organization.Contact billingContact(
      Map<String, LhsLighthouseRpcGatewayResponse.Values> fields) {
    return contact(
        null,
        null,
        null,
        null,
        null,
        null,
        "BILL",
        fields.get(InsuranceCompany.BILLING_PHONE_NUMBER),
        null,
        fields.get(InsuranceCompany.BILLING_COMPANY_NAME));
  }

  private Organization.Contact claimsDentalContact(
      Map<String, LhsLighthouseRpcGatewayResponse.Values> fields) {
    return contact(
        fields.get(InsuranceCompany.CLAIMS_DENTAL_STREET_ADDR_1),
        fields.get(InsuranceCompany.CLAIMS_DENTAL_STREET_ADDR_2),
        null,
        fields.get(InsuranceCompany.CLAIMS_DENTAL_PROCESS_CITY),
        fields.get(InsuranceCompany.CLAIMS_DENTAL_PROCESS_STATE),
        fields.get(InsuranceCompany.CLAIMS_DENTAL_PROCESS_ZIP),
        "DENTALCLAIM",
        fields.get(InsuranceCompany.CLAIMS_DENTAL_PHONE_NUMBER),
        fields.get(InsuranceCompany.CLAIMS_DENTAL_FAX),
        fields.get(InsuranceCompany.CLAIMS_DENTAL_COMPANY_NAME));
  }

  private Organization.Contact claimsInptContact(
      Map<String, LhsLighthouseRpcGatewayResponse.Values> fields) {
    return contact(
        fields.get(InsuranceCompany.CLAIMS_INPT_STREET_ADDRESS_1),
        fields.get(InsuranceCompany.CLAIMS_INPT_STREET_ADDRESS_2),
        fields.get(InsuranceCompany.CLAIMS_INPT_STREET_ADDRESS_3),
        fields.get(InsuranceCompany.CLAIMS_INPT_PROCESS_CITY),
        fields.get(InsuranceCompany.CLAIMS_INPT_PROCESS_STATE),
        fields.get(InsuranceCompany.CLAIMS_INPT_PROCESS_ZIP),
        "RXCLAIMS",
        fields.get(InsuranceCompany.CLAIMS_RX_PHONE_NUMBER),
        fields.get(InsuranceCompany.CLAIMS_RX_FAX),
        fields.get(InsuranceCompany.CLAIMS_INPT_COMPANY_NAME));
  }

  private Organization.Contact claimsOptContact(
      Map<String, LhsLighthouseRpcGatewayResponse.Values> fields) {
    return contact(
        fields.get(InsuranceCompany.CLAIMS_OPT_STREET_ADDRESS_1),
        fields.get(InsuranceCompany.CLAIMS_OPT_STREET_ADDRESS_2),
        fields.get(InsuranceCompany.CLAIMS_OPT_STREET_ADDRESS_3),
        fields.get(InsuranceCompany.CLAIMS_OPT_PROCESS_CITY),
        fields.get(InsuranceCompany.CLAIMS_OPT_PROCESS_STATE),
        fields.get(InsuranceCompany.CLAIMS_OPT_PROCESS_ZIP),
        "OUTPTCLAIMS",
        fields.get(InsuranceCompany.CLAIMS_OPT_PHONE_NUMBER),
        fields.get(InsuranceCompany.CLAIMS_OPT_FAX),
        fields.get(InsuranceCompany.CLAIMS_OPT_COMPANY_NAME));
  }

  private Organization.Contact claimsRxContact(
      Map<String, LhsLighthouseRpcGatewayResponse.Values> fields) {
    return contact(
        fields.get(InsuranceCompany.CLAIMS_RX_STREET_ADDRESS_1),
        fields.get(InsuranceCompany.CLAIMS_RX_STREET_ADDRESS_2),
        fields.get(InsuranceCompany.CLAIMS_RX_STREET_ADDRESS_3),
        fields.get(InsuranceCompany.CLAIMS_RX_CITY),
        fields.get(InsuranceCompany.CLAIMS_RX_STATE),
        fields.get(InsuranceCompany.CLAIMS_RX_ZIP),
        "RXCLAIMS",
        fields.get(InsuranceCompany.CLAIMS_RX_PHONE_NUMBER),
        fields.get(InsuranceCompany.CLAIMS_RX_FAX),
        fields.get(InsuranceCompany.CLAIMS_RX_COMPANY_NAME));
  }

  private List<Address> collectAddress(LhsLighthouseRpcGatewayResponse.FilemanEntry entry) {
    return Collections.singletonList(
        address(
            internalValueOf(entry.fields().get(InsuranceCompany.STREET_ADDRESS_LINE_1_)),
            internalValueOf(entry.fields().get(InsuranceCompany.STREET_ADDRESS_LINE_2_)),
            internalValueOf(entry.fields().get(InsuranceCompany.STREET_ADDRESS_LINE_3_)),
            internalValueOf(entry.fields().get(InsuranceCompany.CITY)),
            internalValueOf(entry.fields().get(InsuranceCompany.STATE)),
            internalValueOf(entry.fields().get(InsuranceCompany.ZIP_CODE))));
  }

  private Organization.Contact contact(
      LhsLighthouseRpcGatewayResponse.Values streetAddressLine1,
      LhsLighthouseRpcGatewayResponse.Values streetAddressLine2,
      LhsLighthouseRpcGatewayResponse.Values streetAddressLine3,
      LhsLighthouseRpcGatewayResponse.Values city,
      LhsLighthouseRpcGatewayResponse.Values state,
      LhsLighthouseRpcGatewayResponse.Values zipCode,
      String purpose,
      LhsLighthouseRpcGatewayResponse.Values phone,
      LhsLighthouseRpcGatewayResponse.Values fax,
      LhsLighthouseRpcGatewayResponse.Values companyName) {
    String streetAddressLine1InternalValue = internalValueOf(streetAddressLine1);
    String streetAddressLine2InternalValue = internalValueOf(streetAddressLine2);
    String streetAddressLine3InternalValue = internalValueOf(streetAddressLine3);
    String cityInternalValue = internalValueOf(city);
    String stateInternalValue = internalValueOf(state);
    String zipCodeInternalValue = internalValueOf(zipCode);
    String phoneInternalValue = internalValueOf(phone);
    String faxInternalValue = internalValueOf(fax);
    String companyNameInternalValue = internalValueOf(companyName);
    if (allBlank(
        streetAddressLine1InternalValue,
        streetAddressLine2InternalValue,
        streetAddressLine3InternalValue,
        cityInternalValue,
        stateInternalValue,
        zipCodeInternalValue,
        purpose,
        phoneInternalValue,
        faxInternalValue,
        companyNameInternalValue)) {
      return null;
    }
    return Organization.Contact.builder()
        .address(
            address(
                streetAddressLine1InternalValue,
                streetAddressLine2InternalValue,
                streetAddressLine3InternalValue,
                cityInternalValue,
                stateInternalValue,
                zipCodeInternalValue))
        .purpose(
            asCodeableConcept(
                Coding.builder()
                    .code(purpose)
                    .display(purpose)
                    .system("http://terminology.hl7.org/CodeSystem/contactentity-type")
                    .build()))
        .telecom(contactTelecom(phoneInternalValue, faxInternalValue))
        .extension(companyNameExtension(companyNameInternalValue))
        .build();
  }

  private List<Extension> companyNameExtension(String companyName) {
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

  private List<Organization.Contact> contacts(
      Map<String, LhsLighthouseRpcGatewayResponse.Values> fields) {
    return List.of(
        appealsContact(fields),
        billingContact(fields),
        claimsDentalContact(fields),
        claimsInptContact(fields),
        claimsOptContact(fields),
        claimsRxContact(fields),
        inquiryContact(fields),
        precertificationContact(fields));
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

    var maybeFilingTimeFrame = fields.get(InsuranceCompany.FILING_TIME_FRAME);
    if (isInternalValueNotBlank(maybeFilingTimeFrame)) {
      extensions.add(
          Extension.builder()
              .url("http://va.gov/fhir/StructureDefinition/organization-filingTimeFrame")
              .valueString(maybeFilingTimeFrame.in())
              .build());
    }

    var maybeAnotherCoProcessIpClaims = fields.get(InsuranceCompany.ANOTHER_CO_PROCESS_IP_CLAIMS_);
    if (isInternalValueNotBlank(maybeAnotherCoProcessIpClaims)) {
      extensions.add(
          Extension.builder()
              .valueBoolean(yesNoToBoolean(maybeAnotherCoProcessIpClaims.in()))
              .url(
                  "http://va.gov/fhir/StructureDefinition/organization-anotherCompanyProcessesInpatClaims")
              .build());
    }

    var maybeTypeOfCoverage = fields.get(InsuranceCompany.TYPE_OF_COVERAGE);
    if (isInternalValueNotBlank(maybeTypeOfCoverage)) {
      extensions.add(
          Extension.builder()
              .valueCodeableConcept()
              .url("http://va.gov/fhir/StructureDefinition/organization-typeOfCoverage")
              .build());
    }

    return extensions.isEmpty() ? null : extensions;
  }

  private Organization.Contact inquiryContact(
      Map<String, LhsLighthouseRpcGatewayResponse.Values> fields) {
    return contact(
        fields.get(InsuranceCompany.INQUIRY_ADDRESS_ST_LINE_1_),
        fields.get(InsuranceCompany.INQUIRY_ADDRESS_ST_LINE_2_),
        fields.get(InsuranceCompany.INQUIRY_ADDRESS_ST_LINE_3_),
        fields.get(InsuranceCompany.INQUIRY_ADDRESS_CITY),
        fields.get(InsuranceCompany.INQUIRY_ADDRESS_STATE),
        fields.get(InsuranceCompany.INQUIRY_ADDRESS_ZIP_CODE),
        null,
        fields.get(InsuranceCompany.INQUIRY_PHONE_NUMBER),
        fields.get(InsuranceCompany.INQUIRY_FAX),
        fields.get(InsuranceCompany.INQUIRY_COMPANY_NAME));
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
      Map<String, LhsLighthouseRpcGatewayResponse.Values> fields) {
    return contact(
        null,
        null,
        null,
        null,
        null,
        null,
        "PRECERT",
        fields.get(InsuranceCompany.PRECERTIFICATION_PHONE_NUMBER),
        null,
        fields.get(InsuranceCompany.PRECERT_COMPANY_NAME));
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
        .name(internalValueOf(fields.get(InsuranceCompany.NAME)))
        .type(insuranceCompanyType())
        .address(collectAddress(entry))
        .contact(contacts(fields))
        .telecom(organizationTelecom(internalValueOf(fields.get(InsuranceCompany.PHONE_NUMBER))))
        .build();
  }
}
