package gov.va.api.health.vistafhirquery.service.controller.organization;

import gov.va.api.health.r4.api.resources.Organization;
import gov.va.api.health.vistafhirquery.service.api.R4OrganizationApi;
import gov.va.api.health.vistafhirquery.service.controller.PatientTypeCoordinates;
import gov.va.api.health.vistafhirquery.service.controller.VistalinkApiClient;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.WitnessProtection;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.InsuranceType;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayGetsManifest.Request;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayGetsManifest.Request.GetsManifestFlags;
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
            .fields(
                List.of(
                    InsuranceType.INSURANCE_TYPE,
                    InsuranceType.GROUP_PLAN,
                    InsuranceType.COORDINATION_OF_BENEFITS,
                    InsuranceType.INSURANCE_EXPIRATION_DATE,
                    InsuranceType.STOP_POLICY_FROM_BILLING,
                    InsuranceType.PT_RELATIONSHIP_HIPAA,
                    InsuranceType.PHARMACY_PERSON_CODE,
                    InsuranceType.SUBSCRIBER_ID,
                    InsuranceType.EFFECTIVE_DATE_OF_POLICY))
            .flags(
                List.of(
                    GetsManifestFlags.OMIT_NULL_VALUES,
                    GetsManifestFlags.RETURN_INTERNAL_VALUES,
                    GetsManifestFlags.RETURN_EXTERNAL_VALUES))
            .build();
    // vistalinkApiClient.requestForVistaSite()

    return Organization.builder().id(id).build();
  }
}
