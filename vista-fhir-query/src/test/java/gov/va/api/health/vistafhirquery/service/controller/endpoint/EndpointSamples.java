package gov.va.api.health.vistafhirquery.service.controller.endpoint;

import gov.va.api.health.r4.api.bundle.AbstractBundle;
import gov.va.api.health.r4.api.bundle.AbstractEntry;
import gov.va.api.health.r4.api.bundle.BundleLink;
import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import gov.va.api.health.r4.api.resources.Endpoint;
import gov.va.api.health.vistafhirquery.service.config.LinkProperties;
import gov.va.api.lighthouse.charon.api.RpcPrincipalLookup;
import gov.va.api.lighthouse.charon.api.RpcPrincipals;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EndpointSamples {

  public static LinkProperties linkProperties() {
    return LinkProperties.builder()
        .publicUrl("http://fake.com")
        .publicR4BasePath("r4")
        .defaultPageSize(10)
        .maxPageSize(100)
        .build();
  }

  RpcPrincipalLookup rpcPrincipalLookup() {
    return RpcPrincipalLookup.builder()
        .rpcPrincipals(
            RpcPrincipals.builder()
                .entries(
                    List.of(
                        RpcPrincipals.PrincipalEntry.builder()
                            .codes(
                                List.of(
                                    RpcPrincipals.Codes.builder()
                                        .sites(List.of("101"))
                                        .verifyCode("1")
                                        .accessCode("2")
                                        .build(),
                                    RpcPrincipals.Codes.builder()
                                        .sites(List.of("104"))
                                        .verifyCode("1")
                                        .accessCode("2")
                                        .build()))
                            .rpcNames(List.of("LHS LIGHTHOUSE RPC GATEWAY"))
                            .build(),
                        RpcPrincipals.PrincipalEntry.builder()
                            .codes(
                                List.of(
                                    RpcPrincipals.Codes.builder()
                                        .sites(List.of("102"))
                                        .verifyCode("1")
                                        .accessCode("2")
                                        .build()))
                            .rpcNames(List.of("EXAMPLE RPC"))
                            .build(),
                        RpcPrincipals.PrincipalEntry.builder()
                            .codes(
                                List.of(
                                    RpcPrincipals.Codes.builder()
                                        .sites(List.of("103"))
                                        .verifyCode("1")
                                        .accessCode("2")
                                        .build()))
                            .rpcNames(List.of("LHS LIGHTHOUSE RPC GATEWAY"))
                            .build()))
                .build())
        .build();
  }

  @NoArgsConstructor(staticName = "create")
  public static class R4 {
    static Endpoint.Bundle asBundle(
        String baseUrl, Collection<Endpoint> resources, int totalRecords, BundleLink... links) {
      return Endpoint.Bundle.builder()
          .resourceType("Bundle")
          .type(AbstractBundle.BundleType.searchset)
          .total(totalRecords)
          .link(Arrays.asList(links))
          .entry(
              resources.stream()
                  .map(
                      resource ->
                          Endpoint.Entry.builder()
                              .fullUrl(resource.address())
                              .resource(resource)
                              .search(
                                  AbstractEntry.Search.builder()
                                      .mode(AbstractEntry.SearchMode.match)
                                      .build())
                              .build())
                  .collect(Collectors.toList()))
          .build();
    }

    static BundleLink link(BundleLink.LinkRelation rel, String base) {
      return BundleLink.builder().relation(rel).url(base).build();
    }

    Endpoint endpoint(String site) {
      return Endpoint.builder()
          .resourceType("Endpoint")
          .id(site)
          .name(site)
          .status(Endpoint.EndpointStatus.active)
          .connectionType(
              Coding.builder()
                  .code("hl7-fhir-rest")
                  .display("hl7-fhir-rest")
                  .system("http://terminology.hl7.org/CodeSystem/endpoint-connection-type")
                  .build())
          .payloadType(
              List.of(
                  CodeableConcept.builder()
                      .coding(
                          List.of(
                              Coding.builder()
                                  .code("any")
                                  .display("Any")
                                  .system(
                                      "http://terminology.hl7.org/CodeSystem/endpoint-payload-type")
                                  .build()))
                      .text("Any")
                      .build()))
          .payloadMimeType(List.of("application/json", "application/fhir+json"))
          .address("http://fake.com/" + site + "/r4")
          .build();
    }
  }
}
