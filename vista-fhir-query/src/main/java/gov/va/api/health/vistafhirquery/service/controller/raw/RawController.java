package gov.va.api.health.vistafhirquery.service.controller.raw;

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

/** Internal endpoint for getting raw payloads directly from vista. */
@Validated
@RestController
@RequestMapping(
    value = "/internal/raw",
    produces = {"application/json", "application/fhir+json"})
@AllArgsConstructor(onConstructor_ = {@Autowired, @NonNull})
@Builder
public class RawController {

  private final VistalinkApiClient vistalinkApiClient;

  @GetMapping(
      value = "/Organization",
      params = {"site", "icn"})
  public RpcResponse organization(String site, String icn) {
    return vistalinkApiClient.requestForVistaSite(
        site, IblhsAmcmsGetIns.Request.builder().icn(icn).build());
  }
}
