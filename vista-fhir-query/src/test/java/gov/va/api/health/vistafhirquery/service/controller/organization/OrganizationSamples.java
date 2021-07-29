package gov.va.api.health.vistafhirquery.service.controller.organization;

import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.asCodeableConcept;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.toBigDecimal;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.r4.api.datatypes.Address;
import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import gov.va.api.health.r4.api.datatypes.ContactPoint;
import gov.va.api.health.r4.api.datatypes.Identifier;
import gov.va.api.health.r4.api.datatypes.Quantity;
import gov.va.api.health.r4.api.elements.Extension;
import gov.va.api.health.r4.api.elements.Reference;
import gov.va.api.health.r4.api.resources.Organization;
import gov.va.api.health.vistafhirquery.service.controller.ProviderTypeCoordinates;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OrganizationSamples {
  @SneakyThrows
  public static String json(Object o) {
    return JacksonConfig.createMapper().writeValueAsString(o);
  }

  @NoArgsConstructor(staticName = "create")
  public static class VistaLhsLighthouseRpcGateway {
    private Map<String, LhsLighthouseRpcGatewayResponse.Values> fields() {
      Map<String, LhsLighthouseRpcGatewayResponse.Values> fields = new HashMap<>();
      // Active
      fields.put("#.05", LhsLighthouseRpcGatewayResponse.Values.of("FALSE", "0"));
      // Address
      fields.put(
          "#.01",
          LhsLighthouseRpcGatewayResponse.Values.of("SHANKS OF FL: EXT", "SHANKS OF FL: IN"));
      fields.put(
          "#.111",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "SHANKSVILLE LINE 1", "SHANKSVILLE LINE 1: IN"));
      fields.put(
          "#.112",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "SHANKSVILLE LINE 2", "SHANKSVILLE LINE 2: IN"));
      fields.put(
          "#.113",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "SHANKSVILLE LINE 3", "SHANKSVILLE LINE 3: IN"));
      fields.put(
          "#.114", LhsLighthouseRpcGatewayResponse.Values.of("SHANK CITY: EXT", "SHANK CITY: IN"));
      fields.put(
          "#.115", LhsLighthouseRpcGatewayResponse.Values.of("SHANKTICUT: EXT", "SHANKTICUT: IN"));
      fields.put(
          "#.116", LhsLighthouseRpcGatewayResponse.Values.of("SHANK ZIP: EXT", "SHANK ZIP: IN"));
      // Contact - Appeals
      fields.put(
          "#.141",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "SHANK-APPEALS LINE 1", "SHANK-APPEALS LINE 1: IN"));
      fields.put(
          "#.142",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "SHANK-APPEALS LINE 2", "SHANK-APPEALS LINE 2: IN"));
      fields.put(
          "#.143",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "SHANK-APPEALS LINE 3", "SHANK-APPEALS LINE 3: IN"));
      fields.put(
          "#.144",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "SHANK-APPEALS CITY: EXT", "SHANK-APPEALS CITY: IN"));
      fields.put(
          "#.145", LhsLighthouseRpcGatewayResponse.Values.of("SHANKTICUT: EXT", "SHANKTICUT: IN"));
      fields.put(
          "#.146",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "SHANK-APPEALS ZIP: EXT", "SHANK-APPEALS ZIP: IN"));
      fields.put(
          "#.147",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "SHANK-APPEALS NAME: EXT", "SHANK-APPEALS NAME: IN"));
      fields.put(
          "#.137",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "1-800-SHANK-APPEALS: EXT", "1-800-SHANK-APPEALS: IN"));
      fields.put(
          "#.149",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "FAX SHANK-APPEALS: EXT", "FAX SHANK-APPEALS: IN"));
      // Contact - Billing
      fields.put(
          "#.117",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "SHANK-BILLING NAME: EXT", "SHANK-BILLING NAME: IN"));
      fields.put(
          "#.132",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "1-800-SHANK-BILLING: EXT", "1-800-SHANK-BILLING: IN"));
      // Contact - Claims Dental
      fields.put(
          "#.191",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "SHANK-DENTAL LINE 1: EXT", "SHANK-DENTAL LINE 1: IN"));
      fields.put(
          "#.192",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "SHANK-DENTAL LINE 2: EXT", "SHANK-DENTAL LINE 2: IN"));
      fields.put(
          "#.194",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "SHANK-DENTAL CITY: EXT", "SHANK-DENTAL CITY: IN"));
      fields.put(
          "#.195", LhsLighthouseRpcGatewayResponse.Values.of("SHANKTICUT: EXT", "SHANKTICUT: IN"));
      fields.put(
          "#.196",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "SHANK-DENTAL ZIP: EXT", "SHANK-DENTAL ZIP: IN"));
      fields.put(
          "#.197",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "SHANK-DENTAL NAME: EXT", "SHANK-DENTAL NAME: IN"));
      fields.put(
          "#.199",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "FAX SHANK-DENTAL: EXT", "FAX SHANK-DENTAL: IN"));
      fields.put(
          "#.1911",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "1-800-SHANK-DENTAL: EXT", "1-800-SHANK-DENTAL: IN"));
      // Contact - Claims Inpt
      fields.put(
          "#.121",
          LhsLighthouseRpcGatewayResponse.Values.of("SHANK-INPT LINE 1", "SHANK-INPT LINE 1: IN"));
      fields.put(
          "#.122",
          LhsLighthouseRpcGatewayResponse.Values.of("SHANK-INPT LINE 2", "SHANK-INPT LINE 2: IN"));
      fields.put(
          "#.123",
          LhsLighthouseRpcGatewayResponse.Values.of("SHANK-INPT LINE 3", "SHANK-INPT LINE 3: IN"));
      fields.put(
          "#.124",
          LhsLighthouseRpcGatewayResponse.Values.of("SHANK-INPT CITY: EXT", "SHANK-INPT CITY: IN"));
      fields.put(
          "#.125", LhsLighthouseRpcGatewayResponse.Values.of("SHANKTICUT: EXT", "SHANKTICUT: IN"));
      fields.put(
          "#.126",
          LhsLighthouseRpcGatewayResponse.Values.of("SHANK-INPT ZIP: EXT", "SHANK-INPT ZIP: IN"));
      fields.put(
          "#.127",
          LhsLighthouseRpcGatewayResponse.Values.of("SHANK-INPT NAME: EXT", "SHANK-INPT NAME: IN"));
      fields.put(
          "#.135",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "1-800-SHANK-INPT: EXT", "1-800-SHANK-INPT: IN"));
      fields.put(
          "#.129",
          LhsLighthouseRpcGatewayResponse.Values.of("FAX SHANK-INPT: EXT", "FAX SHANK-INPT: IN"));
      // Contact - Opt
      fields.put(
          "#.161",
          LhsLighthouseRpcGatewayResponse.Values.of("SHANK-OPT LINE 1", "SHANK-OPT LINE 1: IN"));
      fields.put(
          "#.162",
          LhsLighthouseRpcGatewayResponse.Values.of("SHANK-OPT LINE 2", "SHANK-OPT LINE 2: IN"));
      fields.put(
          "#.163",
          LhsLighthouseRpcGatewayResponse.Values.of("SHANK-OPT LINE 3", "SHANK-OPT LINE 3: IN"));
      fields.put(
          "#.164",
          LhsLighthouseRpcGatewayResponse.Values.of("SHANK-OPT CITY: EXT", "SHANK-OPT CITY: IN"));
      fields.put(
          "#.165", LhsLighthouseRpcGatewayResponse.Values.of("SHANKTICUT: EXT", "SHANKTICUT: IN"));
      fields.put(
          "#.166",
          LhsLighthouseRpcGatewayResponse.Values.of("SHANK-OPT ZIP: EXT", "SHANK-OPT ZIP: IN"));
      fields.put(
          "#.167",
          LhsLighthouseRpcGatewayResponse.Values.of("SHANK-OPT NAME: EXT", "SHANK-OPT NAME: IN"));
      fields.put(
          "#.136",
          LhsLighthouseRpcGatewayResponse.Values.of("1-800-SHANK-OPT: EXT", "1-800-SHANK-OPT: IN"));
      fields.put(
          "#.169",
          LhsLighthouseRpcGatewayResponse.Values.of("FAX SHANK-OPT: EXT", "FAX SHANK-OPT: IN"));
      // Contact - RX
      fields.put(
          "#.181",
          LhsLighthouseRpcGatewayResponse.Values.of("SHANK-RX LINE 1", "SHANK-RX LINE 1: IN"));
      fields.put(
          "#.182",
          LhsLighthouseRpcGatewayResponse.Values.of("SHANK-RX LINE 2", "SHANK-RX LINE 2: IN"));
      fields.put(
          "#.183",
          LhsLighthouseRpcGatewayResponse.Values.of("SHANK-RX LINE 3", "SHANK-RX LINE 3: IN"));
      fields.put(
          "#.184",
          LhsLighthouseRpcGatewayResponse.Values.of("SHANK-RX CITY: EXT", "SHANK-RX CITY: IN"));
      fields.put(
          "#.185", LhsLighthouseRpcGatewayResponse.Values.of("SHANKTICUT: EXT", "SHANKTICUT: IN"));
      fields.put(
          "#.186",
          LhsLighthouseRpcGatewayResponse.Values.of("SHANK-RX ZIP: EXT", "SHANK-RX ZIP: IN"));
      fields.put(
          "#.187",
          LhsLighthouseRpcGatewayResponse.Values.of("SHANK-RX NAME: EXT", "SHANK-RX NAME: IN"));
      fields.put(
          "#.1311",
          LhsLighthouseRpcGatewayResponse.Values.of("1-800-SHANK-RX: EXT", "1-800-SHANK-RX: IN"));
      fields.put(
          "#.189",
          LhsLighthouseRpcGatewayResponse.Values.of("FAX SHANK-RX: EXT", "FAX SHANK-RX: IN"));
      // Contact - Inquiry
      fields.put(
          "#.151",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "SHANK-INQUIRY LINE 1", "SHANK-INQUIRY LINE 1: IN"));
      fields.put(
          "#.152",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "SHANK-INQUIRY LINE 2", "SHANK-INQUIRY LINE 2: IN"));
      fields.put(
          "#.153",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "SHANK-INQUIRY LINE 3", "SHANK-INQUIRY LINE 3: IN"));
      fields.put(
          "#.154",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "SHANK-INQUIRY CITY: EXT", "SHANK-INQUIRY CITY: IN"));
      fields.put(
          "#.155", LhsLighthouseRpcGatewayResponse.Values.of("SHANKTICUT: EXT", "SHANKTICUT: IN"));
      fields.put(
          "#.156",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "SHANK-INQUIRY ZIP: EXT", "SHANK-INQUIRY ZIP: IN"));
      fields.put(
          "#.157",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "SHANK-INQUIRY NAME: EXT", "SHANK-INQUIRY NAME: IN"));
      fields.put(
          "#.138",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "1-800-SHANK-INQUIRY: EXT", "1-800-SHANK-INQUIRY: IN"));
      fields.put(
          "#.159",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "FAX SHANK-INQUIRY: EXT", "FAX SHANK-INQUIRY: IN"));
      // Contact - Precertification
      fields.put(
          "#.133",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "1-800-SHANK-PRECERT: EXT", "1-800-SHANK-PRECERT: IN"));
      fields.put(
          "#.139",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "SHANK-PRECERT NAME: EXT", "SHANK-PRECERT NAME: IN"));
      // Telecom
      fields.put(
          "#.131",
          LhsLighthouseRpcGatewayResponse.Values.of("1-800-SHANK: EXT", "1-800-SHANK: IN"));
      // Extension
      fields.put("#.06", LhsLighthouseRpcGatewayResponse.Values.of("TRUE", "1"));
      fields.put("#.08", LhsLighthouseRpcGatewayResponse.Values.of("TRUE", "1"));
      fields.put("#.09", LhsLighthouseRpcGatewayResponse.Values.of("TV/RADIO", "994"));
      fields.put(
          "#.12",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "FILING SHANKTOTIME FRAME: EX", "FILING SHANKTOTIME FRAME: IN"));
      fields.put("#.128", LhsLighthouseRpcGatewayResponse.Values.of("TRUE", "1"));
      fields.put(
          "#.13",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "SHANK HEALTH INSURANCE: EXT", "SHANK HEALTH INSURANCE: IN"));
      fields.put("#.148", LhsLighthouseRpcGatewayResponse.Values.of("TRUE", "1"));
      fields.put(
          "#.15",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "SHANK PRESCRIPTION REV CODE: EXT", "SHANK PRESCRIPTION REV CODE: IN"));
      fields.put("#.158", LhsLighthouseRpcGatewayResponse.Values.of("TRUE", "1"));
      fields.put("#.168", LhsLighthouseRpcGatewayResponse.Values.of("TRUE", "1"));
      fields.put("#.178", LhsLighthouseRpcGatewayResponse.Values.of("TRUE", "1"));
      fields.put("#.188", LhsLighthouseRpcGatewayResponse.Values.of("TRUE", "1"));
      fields.put("#.198", LhsLighthouseRpcGatewayResponse.Values.of("TRUE", "1"));
      fields.put("#.19", LhsLighthouseRpcGatewayResponse.Values.of("SHANK FTF: EXT", "8675309"));
      fields.put(
          "#1",
          LhsLighthouseRpcGatewayResponse.Values.of("SHANK REIMBURSE: EXT", "SHANK REIMBURSE: IN"));
      fields.put("#2", LhsLighthouseRpcGatewayResponse.Values.of("TRUE", "1"));
      fields.put(
          "#3.01",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "SHANKED ELECTRONICALLY: EXT", "SHANKED ELECTRONICALLY: IN"));
      fields.put(
          "#3.09",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "ELECTRONIC INSHANKANCE TYPE: EXT", "ELECTRONIC INSHANKANCE TYPE: IN"));
      fields.put(
          "#3.1", LhsLighthouseRpcGatewayResponse.Values.of("SHANK PAYER: EXT", "SHANK PAYER: IN"));
      fields.put(
          "#4.01",
          LhsLighthouseRpcGatewayResponse.Values.of("PERF SHANK 1500: EXT", "SHANK 1500: IN"));
      fields.put(
          "#4.02",
          LhsLighthouseRpcGatewayResponse.Values.of("PERF SHANK UB: EXT", "PERF SHANK UB: IN"));
      fields.put(
          "#4.04",
          LhsLighthouseRpcGatewayResponse.Values.of("REF SHANK 1500: EXT", "REF SHANK 1500: IN"));
      fields.put(
          "#4.05",
          LhsLighthouseRpcGatewayResponse.Values.of(
              "REF SHANK CLAIMS: EXT", "REF SHANK CLAIMS: IN"));
      fields.put("#4.06", LhsLighthouseRpcGatewayResponse.Values.of("TRUE", "1"));
      fields.put("#4.08", LhsLighthouseRpcGatewayResponse.Values.of("TRUE", "1"));
      fields.put("#6.09", LhsLighthouseRpcGatewayResponse.Values.of("TRUE", "1"));
      fields.put("#6.1", LhsLighthouseRpcGatewayResponse.Values.of("TRUE", "1"));
      // Identifiers
      fields.put(
          "#3.02", LhsLighthouseRpcGatewayResponse.Values.of("SHANKFEDI: EXT", "SHANKFEDI: IN"));
      fields.put(
          "#3.03", LhsLighthouseRpcGatewayResponse.Values.of("SHANKBIN: EXT", "SHANKBIN: IN"));
      fields.put(
          "#3.04", LhsLighthouseRpcGatewayResponse.Values.of("SHANKTEDI: EXT", "SHANKTEDI: IN"));
      return Map.copyOf(fields);
    }

    LhsLighthouseRpcGatewayResponse.Results getsManifestResults() {
      return getsManifestResults("1,8,");
    }

    LhsLighthouseRpcGatewayResponse.Results getsManifestResults(String id) {
      return LhsLighthouseRpcGatewayResponse.Results.builder()
          .results(
              List.of(
                  LhsLighthouseRpcGatewayResponse.FilemanEntry.builder()
                      .file("36")
                      .ien(id)
                      .fields(fields())
                      .build()))
          .build();
    }
  }

  @NoArgsConstructor(staticName = "create")
  public static class R4 {
    private List<Address> address() {
      return List.of(
          Address.builder()
              .line(
                  List.of(
                      "SHANKSVILLE LINE 1: IN", "SHANKSVILLE LINE 2: IN", "SHANKSVILLE LINE 3: IN"))
              .city("SHANK CITY: IN")
              .state("SHANKTICUT: IN")
              .postalCode("SHANK ZIP: IN")
              .build());
    }

    private Organization.Contact appealsContact() {
      return Organization.Contact.builder()
          .extension(
              List.of(
                  Extension.builder()
                      .valueReference(Reference.builder().display("SHANK-APPEALS NAME: IN").build())
                      .url(
                          "http://hl7.org/fhir/us/davinci-pdex-plan-net/StructureDefinition/via-intermediary")
                      .build()))
          .address(
              Address.builder()
                  .line(
                      List.of(
                          "SHANK-APPEALS LINE 1: IN",
                          "SHANK-APPEALS LINE 2: IN",
                          "SHANK-APPEALS LINE 3: IN"))
                  .city("SHANK-APPEALS CITY: IN")
                  .state("SHANKTICUT: IN")
                  .postalCode("SHANK-APPEALS ZIP: IN")
                  .build())
          .telecom(
              List.of(
                  ContactPoint.builder()
                      .value("1-800-SHANK-APPEALS: IN")
                      .system(ContactPoint.ContactPointSystem.phone)
                      .build(),
                  ContactPoint.builder()
                      .value("FAX SHANK-APPEALS: IN")
                      .system(ContactPoint.ContactPointSystem.fax)
                      .build()))
          .build();
    }

    private Organization.Contact billingContact() {
      return Organization.Contact.builder()
          .extension(
              List.of(
                  Extension.builder()
                      .valueReference(Reference.builder().display("SHANK-BILLING NAME: IN").build())
                      .url(
                          "http://hl7.org/fhir/us/davinci-pdex-plan-net/StructureDefinition/via-intermediary")
                      .build()))
          .telecom(
              List.of(
                  ContactPoint.builder()
                      .value("1-800-SHANK-BILLING: IN")
                      .system(ContactPoint.ContactPointSystem.phone)
                      .build()))
          .purpose(
              CodeableConcept.builder()
                  .coding(
                      Collections.singletonList(
                          Coding.builder()
                              .system("http://terminology.hl7.org/CodeSystem/contactentity-type")
                              .code("BILL")
                              .display("BILL")
                              .build()))
                  .build())
          .build();
    }

    private Organization.Contact claimsDentalContact() {
      return Organization.Contact.builder()
          .extension(
              List.of(
                  Extension.builder()
                      .valueReference(Reference.builder().display("SHANK-DENTAL NAME: IN").build())
                      .url(
                          "http://hl7.org/fhir/us/davinci-pdex-plan-net/StructureDefinition/via-intermediary")
                      .build()))
          .address(
              Address.builder()
                  .line(List.of("SHANK-DENTAL LINE 1: IN", "SHANK-DENTAL LINE 2: IN"))
                  .city("SHANK-DENTAL CITY: IN")
                  .state("SHANKTICUT: IN")
                  .postalCode("SHANK-DENTAL ZIP: IN")
                  .build())
          .telecom(
              List.of(
                  ContactPoint.builder()
                      .value("1-800-SHANK-DENTAL: IN")
                      .system(ContactPoint.ContactPointSystem.phone)
                      .build(),
                  ContactPoint.builder()
                      .value("FAX SHANK-DENTAL: IN")
                      .system(ContactPoint.ContactPointSystem.fax)
                      .build()))
          .purpose(
              CodeableConcept.builder()
                  .coding(
                      Collections.singletonList(
                          Coding.builder()
                              .system("http://terminology.hl7.org/CodeSystem/contactentity-type")
                              .code("DENTALCLAIM")
                              .display("DENTALCLAIM")
                              .build()))
                  .build())
          .build();
    }

    private Organization.Contact claimsInptContact() {
      return Organization.Contact.builder()
          .extension(
              List.of(
                  Extension.builder()
                      .valueReference(Reference.builder().display("SHANK-INPT NAME: IN").build())
                      .url(
                          "http://hl7.org/fhir/us/davinci-pdex-plan-net/StructureDefinition/via-intermediary")
                      .build()))
          .address(
              Address.builder()
                  .line(
                      List.of(
                          "SHANK-INPT LINE 1: IN",
                          "SHANK-INPT LINE 2: IN",
                          "SHANK-INPT LINE 3: IN"))
                  .city("SHANK-INPT CITY: IN")
                  .state("SHANKTICUT: IN")
                  .postalCode("SHANK-INPT ZIP: IN")
                  .build())
          .telecom(
              List.of(
                  ContactPoint.builder()
                      .value("1-800-SHANK-INPT: IN")
                      .system(ContactPoint.ContactPointSystem.phone)
                      .build(),
                  ContactPoint.builder()
                      .value("FAX SHANK-INPT: IN")
                      .system(ContactPoint.ContactPointSystem.fax)
                      .build()))
          .purpose(
              CodeableConcept.builder()
                  .coding(
                      Collections.singletonList(
                          Coding.builder()
                              .system("http://terminology.hl7.org/CodeSystem/contactentity-type")
                              .code("INPTCLAIMS")
                              .display("INPTCLAIMS")
                              .build()))
                  .build())
          .build();
    }

    private Organization.Contact claimsOptContact() {
      return Organization.Contact.builder()
          .extension(
              List.of(
                  Extension.builder()
                      .valueReference(Reference.builder().display("SHANK-OPT NAME: IN").build())
                      .url(
                          "http://hl7.org/fhir/us/davinci-pdex-plan-net/StructureDefinition/via-intermediary")
                      .build()))
          .address(
              Address.builder()
                  .line(
                      List.of(
                          "SHANK-OPT LINE 1: IN", "SHANK-OPT LINE 2: IN", "SHANK-OPT LINE 3: IN"))
                  .city("SHANK-OPT CITY: IN")
                  .state("SHANKTICUT: IN")
                  .postalCode("SHANK-OPT ZIP: IN")
                  .build())
          .telecom(
              List.of(
                  ContactPoint.builder()
                      .value("1-800-SHANK-OPT: IN")
                      .system(ContactPoint.ContactPointSystem.phone)
                      .build(),
                  ContactPoint.builder()
                      .value("FAX SHANK-OPT: IN")
                      .system(ContactPoint.ContactPointSystem.fax)
                      .build()))
          .purpose(
              CodeableConcept.builder()
                  .coding(
                      Collections.singletonList(
                          Coding.builder()
                              .system("http://terminology.hl7.org/CodeSystem/contactentity-type")
                              .code("OUTPTCLAIMS")
                              .display("OUTPTCLAIMS")
                              .build()))
                  .build())
          .build();
    }

    private Organization.Contact claimsRxContact() {
      return Organization.Contact.builder()
          .extension(
              List.of(
                  Extension.builder()
                      .valueReference(Reference.builder().display("SHANK-RX NAME: IN").build())
                      .url(
                          "http://hl7.org/fhir/us/davinci-pdex-plan-net/StructureDefinition/via-intermediary")
                      .build()))
          .address(
              Address.builder()
                  .line(
                      List.of("SHANK-RX LINE 1: IN", "SHANK-RX LINE 2: IN", "SHANK-RX LINE 3: IN"))
                  .city("SHANK-RX CITY: IN")
                  .state("SHANKTICUT: IN")
                  .postalCode("SHANK-RX ZIP: IN")
                  .build())
          .telecom(
              List.of(
                  ContactPoint.builder()
                      .value("1-800-SHANK-RX: IN")
                      .system(ContactPoint.ContactPointSystem.phone)
                      .build(),
                  ContactPoint.builder()
                      .value("FAX SHANK-RX: IN")
                      .system(ContactPoint.ContactPointSystem.fax)
                      .build()))
          .purpose(
              CodeableConcept.builder()
                  .coding(
                      Collections.singletonList(
                          Coding.builder()
                              .system("http://terminology.hl7.org/CodeSystem/contactentity-type")
                              .code("RXCLAIMS")
                              .display("RXCLAIMS")
                              .build()))
                  .build())
          .build();
    }

    private List<Organization.Contact> contacts() {
      return List.of(
          appealsContact(),
          billingContact(),
          claimsDentalContact(),
          claimsInptContact(),
          claimsOptContact(),
          claimsRxContact(),
          inquiryContact(),
          precertificationContact());
    }

    private List<Extension> extensions() {
      return List.of(
          Extension.builder()
              .valueBoolean(Boolean.TRUE)
              .url("http://va.gov/fhir/StructureDefinition/organization-allowMultipleBedsections")
              .build(),
          Extension.builder()
              .url("http://va.gov/fhir/StructureDefinition/organization-oneOutpatVisitOnBillOnly")
              .valueBoolean(Boolean.TRUE)
              .build(),
          Extension.builder()
              .valueCodeableConcept(
                  CodeableConcept.builder()
                      .coding(
                          Collections.singletonList(
                              Coding.builder()
                                  .code("994")
                                  .system("urn:oid:2.16.840.1.113883.6.301.3")
                                  .build()))
                      .build())
              .url(
                  "http://va.gov/fhir/StructureDefinition/organization-ambulatorySurgeryRevenueCode")
              .build(),
          Extension.builder()
              .valueString("FILING SHANKTOTIME FRAME: IN")
              .url("http://va.gov/fhir/StructureDefinition/organization-filingTimeFrame")
              .build(),
          Extension.builder()
              .valueBoolean(Boolean.TRUE)
              .url(
                  "http://va.gov/fhir/StructureDefinition/organization-anotherCompanyProcessesInpatClaims")
              .build(),
          Extension.builder()
              .valueCodeableConcept(
                  CodeableConcept.builder()
                      .coding(
                          Collections.singletonList(
                              Coding.builder()
                                  .code("SHANK HEALTH INSURANCE: IN")
                                  .system("urn:oid:2.16.840.1.113883.3.8901.3.36.8013")
                                  .build()))
                      .build())
              .url("http://va.gov/fhir/StructureDefinition/organization-typeOfCoverage")
              .build(),
          Extension.builder()
              .valueBoolean(Boolean.TRUE)
              .url(
                  "http://va.gov/fhir/StructureDefinition/organization-anotherCompanyProcessesAppeals")
              .build(),
          Extension.builder()
              .url("http://va.gov/fhir/StructureDefinition/organization-prescriptionRevenueCode")
              .valueCodeableConcept(
                  CodeableConcept.builder()
                      .coding(
                          Collections.singletonList(
                              Coding.builder()
                                  .system("urn:oid:2.16.840.1.113883.6.301.3")
                                  .code("SHANK PRESCRIPTION REV CODE: IN")
                                  .build()))
                      .build())
              .build(),
          Extension.builder()
              .valueBoolean(Boolean.TRUE)
              .url(
                  "http://va.gov/fhir/StructureDefinition/organization-anotherCompanyProcessesInquiries")
              .build(),
          Extension.builder()
              .url(
                  "http://va.gov/fhir/StructureDefinition/organization-anotherCompanyProcessesOutpatClaims")
              .valueBoolean(Boolean.TRUE)
              .build(),
          Extension.builder()
              .url(
                  "http://va.gov/fhir/StructureDefinition/organization-anotherCompanyProcessesPrecert")
              .valueBoolean(Boolean.TRUE)
              .build(),
          Extension.builder()
              .url(
                  "http://va.gov/fhir/StructureDefinition/organization-anotherCompanyProcessesRxClaims")
              .valueBoolean(Boolean.TRUE)
              .build(),
          Extension.builder()
              .url(
                  "http://va.gov/fhir/StructureDefinition/organization-anotherCompanyProcessesDentalClaims")
              .valueBoolean(Boolean.TRUE)
              .build(),
          Extension.builder()
              .valueQuantity(
                  Quantity.builder()
                      .value(toBigDecimal("8675309"))
                      .unit("d")
                      .system("urn:oid:2.16.840.1.113883.3.8901.3.3558013")
                      .build())
              .build(),
          Extension.builder()
              .url("http://va.gov/fhir/StructureDefinition/organization-willReimburseForCare")
              .valueCodeableConcept(
                  CodeableConcept.builder()
                      .coding(
                          Collections.singletonList(
                              Coding.builder()
                                  .code("SHANK REIMBURSE: IN")
                                  .system("urn:oid:2.16.840.1.113883.3.8901.3.36.1")
                                  .build()))
                      .build())
              .build(),
          Extension.builder()
              .url("http://va.gov/fhir/StructureDefinition/organization-signatureRequiredOnBill")
              .valueBoolean(Boolean.TRUE)
              .build(),
          Extension.builder()
              .url("http://va.gov/fhir/StructureDefinition/organization-electronicTransmissionMode")
              .valueCodeableConcept(
                  CodeableConcept.builder()
                      .coding(
                          Collections.singletonList(
                              Coding.builder()
                                  .system("urn:oid:2.16.840.1.113883.3.8901.3.36.38001")
                                  .code("SHANKED ELECTRONICALLY: IN")
                                  .build()))
                      .build())
              .build(),
          Extension.builder()
              .url("http://va.gov/fhir/StructureDefinition/organization-electronicInsuranceType")
              .valueCodeableConcept(
                  CodeableConcept.builder()
                      .coding(
                          Collections.singletonList(
                              Coding.builder()
                                  .code("ELECTRONIC INSHANKANCE TYPE: IN")
                                  .system("urn:oid:2.16.840.1.113883.3.8901.3.36.38009")
                                  .build()))
                      .build())
              .build(),
          Extension.builder()
              .url(
                  "http://hl7.org/fhir/us/davinci-pdex-plan-net/StructureDefinition/via-intermediary")
              .valueReference(
                  Reference.builder()
                      .reference(
                          "Organization/"
                              + OrganizationCoordinates.payer("SHANK PAYER: IN").toString())
                      .build())
              .build(),
          Extension.builder()
              .url(
                  "http://va.gov/fhir/StructureDefinition/organization-performingProviderSecondIDTypeCMS1500")
              .valueCodeableConcept(
                  CodeableConcept.builder()
                      .coding(
                          Collections.singletonList(
                              Coding.builder()
                                  .system("urn:oid:2.16.840.1.113883.3.8901.3.3558097.8001")
                                  .code("SHANK 1500: IN")
                                  .build()))
                      .build())
              .build(),
          Extension.builder()
              .url(
                  "http://va.gov/fhir/StructureDefinition/organization-performingProviderSecondIDTypeUB04")
              .valueCodeableConcept(
                  CodeableConcept.builder()
                      .coding(
                          Collections.singletonList(
                              Coding.builder()
                                  .code("PERF SHANK UB: IN")
                                  .system("urn:oid:2.16.840.1.113883.3.8901.3.3558097.8001")
                                  .build()))
                      .build())
              .build(),
          Extension.builder()
              .url(
                  "http://va.gov/fhir/StructureDefinition/organization-referrngProviderSecondIDTypeCMS1500")
              .valueCodeableConcept(
                  CodeableConcept.builder()
                      .coding(
                          Collections.singletonList(
                              Coding.builder()
                                  .code("REF SHANK 1500: IN")
                                  .system("urn:oid:2.16.840.1.113883.3.8901.3.3558097.8001")
                                  .build()))
                      .build())
              .build(),
          Extension.builder()
              .url(
                  "http://va.gov/fhir/StructureDefinition/organization-referrngProviderSecondIDTypeUB04")
              .valueCodeableConcept(
                  CodeableConcept.builder()
                      .coding(
                          Collections.singletonList(
                              Coding.builder()
                                  .code("REF SHANK CLAIMS: IN")
                                  .system("urn:oid:2.16.840.1.113883.3.8901.3.3558097.8001")
                                  .build()))
                      .build())
              .build(),
          Extension.builder()
              .url(
                  "http://va.gov/fhir/StructureDefinition/organization-attendingRenderingProviderSecondaryIDProfesionalRequired")
              .valueBoolean(Boolean.TRUE)
              .build(),
          Extension.builder()
              .url(
                  "http://va.gov/fhir/StructureDefinition/organization-attendingRenderingProviderSecondaryIDInstitutionalRequired")
              .valueBoolean(Boolean.TRUE)
              .build(),
          Extension.builder()
              .url(
                  "http://va.gov/fhir/StructureDefinition/organization-printSecTertAutoClaimsLocally")
              .valueBoolean(Boolean.TRUE)
              .build(),
          Extension.builder()
              .url(
                  "http://va.gov/fhir/StructureDefinition/organization-printSecMedClaimsWOMRALocally")
              .valueBoolean(Boolean.TRUE)
              .build());
    }

    List<Identifier> identifiers() {
      return List.of(
          Identifier.builder()
              .type(
                  CodeableConcept.builder()
                      .coding(
                          Collections.singletonList(
                              Coding.builder().id("SHANKFEDI: IN").code("PROFEDI").build()))
                      .build())
              .build(),
          Identifier.builder()
              .type(
                  CodeableConcept.builder()
                      .coding(
                          Collections.singletonList(
                              Coding.builder().id("SHANKTEDI: IN").code("INSTEDI").build()))
                      .build())
              .build(),
          Identifier.builder()
              .type(
                  CodeableConcept.builder()
                      .coding(
                          Collections.singletonList(
                              Coding.builder().id("SHANKBIN: IN").code("BIN").build()))
                      .build())
              .build());
    }

    private Organization.Contact inquiryContact() {
      return Organization.Contact.builder()
          .extension(
              List.of(
                  Extension.builder()
                      .valueReference(Reference.builder().display("SHANK-INQUIRY NAME: IN").build())
                      .url(
                          "http://hl7.org/fhir/us/davinci-pdex-plan-net/StructureDefinition/via-intermediary")
                      .build()))
          .address(
              Address.builder()
                  .line(
                      List.of(
                          "SHANK-INQUIRY LINE 1: IN",
                          "SHANK-INQUIRY LINE 2: IN",
                          "SHANK-INQUIRY LINE 3: IN"))
                  .city("SHANK-INQUIRY CITY: IN")
                  .state("SHANKTICUT: IN")
                  .postalCode("SHANK-INQUIRY ZIP: IN")
                  .build())
          .telecom(
              List.of(
                  ContactPoint.builder()
                      .value("1-800-SHANK-INQUIRY: IN")
                      .system(ContactPoint.ContactPointSystem.phone)
                      .build(),
                  ContactPoint.builder()
                      .value("FAX SHANK-INQUIRY: IN")
                      .system(ContactPoint.ContactPointSystem.fax)
                      .build()))
          .build();
    }

    Organization organization() {
      return organization("666", "1,8,");
    }

    Organization organization(String station, String ien) {
      return Organization.builder()
          .id(
              ProviderTypeCoordinates.builder()
                  .siteId(station)
                  .recordId(OrganizationCoordinates.insuranceCompany(ien).toString())
                  .build()
                  .toString())
          .identifier(identifiers())
          .type(type())
          .name("SHANKS OF FL: IN")
          .address(address())
          .active(Boolean.TRUE)
          .telecom(telecom())
          .contact(contacts())
          .extension(extensions())
          .build();
    }

    private Organization.Contact precertificationContact() {
      return Organization.Contact.builder()
          .extension(
              List.of(
                  Extension.builder()
                      .valueReference(Reference.builder().display("SHANK-PRECERT NAME: IN").build())
                      .url(
                          "http://hl7.org/fhir/us/davinci-pdex-plan-net/StructureDefinition/via-intermediary")
                      .build()))
          .telecom(
              List.of(
                  ContactPoint.builder()
                      .value("1-800-SHANK-PRECERT: IN")
                      .system(ContactPoint.ContactPointSystem.phone)
                      .build()))
          .purpose(
              CodeableConcept.builder()
                  .coding(
                      Collections.singletonList(
                          Coding.builder()
                              .system("http://terminology.hl7.org/CodeSystem/contactentity-type")
                              .code("PRECERT")
                              .display("PRECERT")
                              .build()))
                  .build())
          .build();
    }

    private List<ContactPoint> telecom() {
      return Collections.singletonList(
          ContactPoint.builder()
              .value("1-800-SHANK: IN")
              .system(ContactPoint.ContactPointSystem.phone)
              .build());
    }

    private List<CodeableConcept> type() {
      return List.of(
          asCodeableConcept(
              Coding.builder()
                  .code("ins")
                  .display("Insurance Company")
                  .system("http://hl7.org/fhir/ValueSet/organization-type")
                  .build()));
    }
  }
}
