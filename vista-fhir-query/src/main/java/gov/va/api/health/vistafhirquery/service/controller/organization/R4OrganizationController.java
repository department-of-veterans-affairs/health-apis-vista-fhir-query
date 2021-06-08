package gov.va.api.health.vistafhirquery.service.controller.organization;

import gov.va.api.health.vistafhirquery.service.controller.VistalinkApiClient;
import gov.va.api.lighthouse.charon.api.RpcResponse;
import gov.va.api.lighthouse.charon.models.iblhsamcmsgetins.IblhsAmcmsGetIns;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Request Mappings for Organization Profile using a VistA backend.
 *
 * @implSpec http://hl7.org/fhir/us/carin-bb/2020Feb/StructureDefinition-CARIN-BB-Organization.html
 */
@Validated
@RestController
@RequestMapping(
    value = "/r4/Organization",
    produces = {"application/json", "application/fhir+json"})
@AllArgsConstructor(onConstructor_ = {@Autowired, @NonNull})
@Builder
public class R4OrganizationController {

  private final VistalinkApiClient vistalinkApiClient;

  @GetMapping(params = {"site", "icn"})
  public RpcResponse searchRaw(String site, String icn) {
    return vistalinkApiClient.requestForVistaSite(
        site, IblhsAmcmsGetIns.Request.builder().icn(icn).build());
  }
}
