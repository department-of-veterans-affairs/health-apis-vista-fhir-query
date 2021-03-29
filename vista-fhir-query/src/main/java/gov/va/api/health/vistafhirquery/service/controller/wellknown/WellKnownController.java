package gov.va.api.health.vistafhirquery.service.controller.wellknown;

import gov.va.api.health.r4.api.information.WellKnown;
import gov.va.api.health.vistafhirquery.service.controller.metadata.MetadataProperties;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping(produces = {"application/json", "application/fhir+json"})
class WellKnownController {
  private final WellKnownProperties wellKnownProperties;
  private final MetadataProperties metadataProperties;

  @GetMapping(value = "/r4/.well-known/smart-configuration")
  public WellKnown r4() {
    return WellKnown.builder()
        .authorizationEndpoint(metadataProperties.getSecurity().getAuthorizeEndpoint())
        .tokenEndpoint(metadataProperties.getSecurity().getTokenEndpoint())
        .managementEndpoint(metadataProperties.getSecurity().getManagementEndpoint())
        .revocationEndpoint(metadataProperties.getSecurity().getRevocationEndpoint())
        .capabilities(wellKnownProperties.getCapabilities())
        .responseTypeSupported(wellKnownProperties.getResponseTypeSupported())
        .scopesSupported(wellKnownProperties.getScopesSupported())
        .build();
  }
}
