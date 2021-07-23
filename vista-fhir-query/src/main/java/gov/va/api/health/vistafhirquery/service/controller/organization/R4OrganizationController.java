package gov.va.api.health.vistafhirquery.service.controller.organization;

import gov.va.api.health.r4.api.resources.Organization;
import gov.va.api.health.vistafhirquery.service.controller.VistalinkApiClient;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.WitnessProtection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Builder
@Validated
@RestController
@RequestMapping(
    value = "/r4/Organization",
    produces = {"application/json", "application/fhir+json"})
@AllArgsConstructor(onConstructor_ = {@Autowired, @NonNull})
public class R4OrganizationController {

  private final WitnessProtection witnessProtection;
  private final VistalinkApiClient vistalinkApiClient;

  @GetMapping(value = "/{publicId}")
  public Organization read(@PathVariable(value = "publicId") String id) {

    // vistalinkApiClient.requestForVistaSite()

    return Organization.builder().id(id).build();
  }
}
