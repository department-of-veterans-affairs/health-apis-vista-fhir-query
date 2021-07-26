package gov.va.api.health.vistafhirquery.service.controller.organization;

import static java.util.stream.Collectors.toList;

import gov.va.api.health.r4.api.resources.Coverage;
import gov.va.api.health.r4.api.resources.Organization;
import gov.va.api.health.vistafhirquery.service.api.R4OrganizationApi;
import gov.va.api.health.vistafhirquery.service.controller.PatientTypeCoordinates;
import gov.va.api.health.vistafhirquery.service.controller.R4Transformation;
import gov.va.api.health.vistafhirquery.service.controller.VistalinkApiClient;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.WitnessProtection;
import gov.va.api.lighthouse.charon.api.RpcResponse;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.InsuranceType;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayGetsManifest;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayGetsManifest.Request;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayGetsManifest.Request.GetsManifestFlags;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayResponse;
import java.util.List;
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
public class R4OrganizationController implements R4OrganizationApi {

  private final WitnessProtection witnessProtection;
  private final VistalinkApiClient vistalinkApiClient;

  @Override
  @GetMapping(value = "/{publicId}")
  public Organization organizationRead(@PathVariable(value = "publicId") String id) {

    PatientTypeCoordinates coordinates = null;
    Request rpcRequest =
        Request.builder()
            .file(InsuranceType.FILE_NUMBER)
            .iens(coordinates.recordId())
            .fields(R4OrganizationTransformer.REQUIRED_FIELDS)
            .flags(
                List.of(
                    GetsManifestFlags.OMIT_NULL_VALUES,
                    GetsManifestFlags.RETURN_INTERNAL_VALUES,
                    GetsManifestFlags.RETURN_EXTERNAL_VALUES))
            .build();
    RpcResponse rpcResponse =
        vistalinkApiClient.requestForVistaSite(coordinates.siteId(), rpcRequest);
    LhsLighthouseRpcGatewayResponse getsManifestResults =
        LhsLighthouseRpcGatewayGetsManifest.create().fromResults(rpcResponse.results());

    List<Organization> resources = transformation().toResource().apply(getsManifestResults);

    return Organization.builder().id(id).build();
  }

  private R4Transformation<LhsLighthouseRpcGatewayResponse, Organization> transformation() {
    return R4Transformation.<LhsLighthouseRpcGatewayResponse, Coverage>builder()
        .toResource(
            rpcResponse ->
                rpcResponse.resultsByStation().entrySet().parallelStream()
                    .flatMap(
                        rpcResults ->
                            R4OrganizationTransformer.builder()
                                .rpcResults(rpcResults)
                                .build()
                                .toFhir())
                    .collect(toList()))
        .build();
  }
}
