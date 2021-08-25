package gov.va.api.health.vistafhirquery.service.controller.endpoint;

import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import gov.va.api.health.r4.api.resources.Endpoint;
import gov.va.api.health.r4.api.resources.Endpoint.EndpointStatus;
import gov.va.api.health.vistafhirquery.service.config.LinkProperties;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;

@Builder
public class R4EndpointTransformer {

  @NonNull String site;

  @NonNull LinkProperties linkProperties;

  Endpoint toFhir() {
    return Endpoint.builder()
        .id(site)
        .name(site)
        .status(EndpointStatus.active)
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
        .address(linkProperties.getPublicUrl() + "/" + site + "/r4")
        .build();
  }
}
