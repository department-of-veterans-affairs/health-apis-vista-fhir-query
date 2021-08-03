package gov.va.api.health.vistafhirquery.service.controller.raw;

import static org.apache.commons.lang3.StringUtils.isBlank;

import gov.va.api.health.autoconfig.logging.Redact;
import gov.va.api.health.vistafhirquery.service.controller.VistalinkApiClient;
import gov.va.api.health.vistafhirquery.service.controller.organization.R4OrganizationController;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.WitnessProtection;
import gov.va.api.lighthouse.charon.api.RpcPrincipal;
import gov.va.api.lighthouse.charon.api.RpcRequest;
import gov.va.api.lighthouse.charon.api.RpcResponse;
import gov.va.api.lighthouse.charon.api.RpcVistaTargets;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayCoverageSearch;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayGetsManifest;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
  private final WitnessProtection witnessProtection;

  /** Get the raw response that the coverage controller transforms to fhir. */
  @GetMapping(
      value = "/Coverage",
      params = {"site", "icn"})
  public RpcResponse coverageBySiteAndIcn(
      @RequestParam(name = "site") String site,
      @RequestParam(name = "icn") String icn,
      @Redact @RequestParam(name = "accessCode", required = false) String accessCode,
      @Redact @RequestParam(name = "verifyCode", required = false) String verifyCode,
      @Redact @RequestParam(name = "apu", required = false) String apu) {

    LhsLighthouseRpcGatewayCoverageSearch.Request rpcRequest =
        LhsLighthouseRpcGatewayCoverageSearch.Request.builder()
            .id(LhsLighthouseRpcGatewayCoverageSearch.Request.PatientId.forIcn(icn))
            .build();

    if (isBlank(accessCode) || isBlank(verifyCode) || isBlank(apu)) {
      return vistalinkApiClient.requestForVistaSite(site, rpcRequest);
    }
    return vistalinkApiClient.makeRequest(
        RpcRequest.builder()
            .principal(
                RpcPrincipal.builder()
                    .accessCode(accessCode)
                    .verifyCode(verifyCode)
                    .applicationProxyUser(apu)
                    .build())
            .target(RpcVistaTargets.builder().include(List.of(site)).build())
            .rpc(rpcRequest.asDetails())
            .build());
  }

  /** Get the raw data. */
  @GetMapping(
      value = {"/Organization"},
      params = {"id"})
  public LhsLighthouseRpcGatewayResponse organizationById(@RequestParam(name = "id") String id) {
    var coordinates = witnessProtection.toRecordCoordinates(id);
    var request = R4OrganizationController.createRequest(coordinates);
    var response = vistalinkApiClient.requestForVistaSite(coordinates.site(), request);
    return LhsLighthouseRpcGatewayGetsManifest.create().fromResults(response.results());
  }
}
