package gov.va.api.health.vistafhirquery.service.controller.organization;

import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.allBlank;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.asCodeableConcept;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.emptyToNull;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.internalValueOf;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.isBlank;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.toResourceId;

import gov.va.api.health.r4.api.datatypes.Address;
import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import gov.va.api.health.r4.api.datatypes.ContactPoint;
import gov.va.api.health.r4.api.resources.Organization;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.InsuranceCompany;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayResponse;
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

  private Organization.Contact contact(
      String streetAddressLine1,
      String streetAddressLine2,
      String streetAddressLine3,
      String city,
      String state,
      String zipCode,
      String purpose,
      String telecomValue,
      ContactPoint.ContactPointSystem telecomSystem) {
    if (allBlank(
        streetAddressLine1,
        streetAddressLine2,
        streetAddressLine3,
        city,
        state,
        zipCode,
        purpose,
        telecomValue,
        telecomSystem)) {
      return null;
    }
    return Organization.Contact.builder()
        .address(
            address(
                streetAddressLine1, streetAddressLine2, streetAddressLine3, city, state, zipCode))
        .purpose(asCodeableConcept(Coding.builder().code(purpose).display(purpose).build()))
        .telecom(contactTelecom(telecomValue, telecomSystem))
        .build();
  }

  private List<ContactPoint> contactTelecom(String value, ContactPoint.ContactPointSystem system) {
    if (allBlank(value, system)) {
      return Collections.emptyList();
    }
    return Collections.singletonList(ContactPoint.builder().value(value).system(system).build());
  }

  private List<Organization.Contact> contacts(
      Map<String, LhsLighthouseRpcGatewayResponse.Values> fields) {
    // TO-DO add more contacts
    return List.of(
        contact(
            internalValueOf(fields.get(InsuranceCompany.CLAIMS_INPT_STREET_ADDRESS_1)),
            internalValueOf(fields.get(InsuranceCompany.CLAIMS_INPT_STREET_ADDRESS_2)),
            internalValueOf(fields.get(InsuranceCompany.CLAIMS_INPT_STREET_ADDRESS_3)),
            internalValueOf(fields.get(InsuranceCompany.CLAIMS_INPT_PROCESS_CITY)),
            internalValueOf(fields.get(InsuranceCompany.CLAIMS_INPT_PROCESS_STATE)),
            internalValueOf(fields.get(InsuranceCompany.CLAIMS_INPT_PROCESS_ZIP)),
            "RXCLAIMS",
            internalValueOf(fields.get(InsuranceCompany.CLAIMS_RX_PHONE_NUMBER)),
            ContactPoint.ContactPointSystem.phone));
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
        .id(toResourceId(patientIcn, rpcResults.getKey(), "36;" + entry.ien()))
        .name(internalValueOf(fields.get(InsuranceCompany.NAME)))
        .type(insuranceCompanyType())
        .address(
            Collections.singletonList(
                address(
                    internalValueOf(entry.fields().get(InsuranceCompany.STREET_ADDRESS_LINE_1_)),
                    internalValueOf(fields.get(InsuranceCompany.STREET_ADDRESS_LINE_2_)),
                    internalValueOf(fields.get(InsuranceCompany.STREET_ADDRESS_LINE_3_)),
                    internalValueOf(fields.get(InsuranceCompany.CITY)),
                    internalValueOf(fields.get(InsuranceCompany.STATE)),
                    internalValueOf(fields.get(InsuranceCompany.ZIP_CODE)))))
        .contact(contacts(fields))
        .telecom(organizationTelecom(internalValueOf(fields.get(InsuranceCompany.PHONE_NUMBER))))
        .build();
  }
}
