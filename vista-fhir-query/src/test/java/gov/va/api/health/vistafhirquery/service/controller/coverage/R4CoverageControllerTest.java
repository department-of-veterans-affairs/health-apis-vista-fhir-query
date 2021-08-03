package gov.va.api.health.vistafhirquery.service.controller.coverage;

import static gov.va.api.health.vistafhirquery.service.controller.MockRequests.json;
import static gov.va.api.health.vistafhirquery.service.controller.MockRequests.requestFromUri;
import static gov.va.api.health.vistafhirquery.service.controller.coverage.CoverageSamples.R4.link;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import gov.va.api.health.r4.api.bundle.BundleLink;
import gov.va.api.health.vistafhirquery.service.config.LinkProperties;
import gov.va.api.health.vistafhirquery.service.controller.PatientTypeCoordinates;
import gov.va.api.health.vistafhirquery.service.controller.R4BundlerFactory;
import gov.va.api.health.vistafhirquery.service.controller.VistalinkApiClient;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.AlternatePatientIds;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.WitnessProtection;
import gov.va.api.lighthouse.charon.api.RpcInvocationResult;
import gov.va.api.lighthouse.charon.api.RpcResponse;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayCoverageSearch;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayGetsManifest;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayResponse;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class R4CoverageControllerTest {
  @Mock VistalinkApiClient vlClient;

  @Mock WitnessProtection witnessProtection;

  private R4CoverageController controller() {
    return R4CoverageController.builder()
        .bundlerFactory(
            R4BundlerFactory.builder()
                .linkProperties(
                    LinkProperties.builder()
                        .defaultPageSize(15)
                        .maxPageSize(100)
                        .publicUrl("http://fugazi.com")
                        .publicR4BasePath("r4")
                        .build())
                .alternatePatientIds(new AlternatePatientIds.DisabledAlternatePatientIds())
                .build())
        .vistalinkApiClient(vlClient)
        .witnessProtection(witnessProtection)
        .build();
  }

  @Test
  void read() {
    var samples = CoverageSamples.VistaLhsLighthouseRpcGateway.create();
    var results = samples.getsManifestResults("ip1");
    when(vlClient.requestForVistaSite(
            eq("123"), any(LhsLighthouseRpcGatewayGetsManifest.Request.class)))
        .thenReturn(
            RpcResponse.builder()
                .status(RpcResponse.Status.OK)
                .results(
                    List.of(
                        RpcInvocationResult.builder().vista("123").response(json(results)).build()))
                .build());
    when(witnessProtection.toPatientTypeCoordinates("pubCover1"))
        .thenReturn(
            PatientTypeCoordinates.builder().icn("p1").siteId("123").recordId("ip1").build());
    var actual = controller().coverageRead("pubCover1");
    var expected = CoverageSamples.R4.create().coverage("123", "ip1", "p1");
    assertThat(json(actual)).isEqualTo(json(expected));
  }

  @Test
  void searchByPatientWithResults() {
    var request = requestFromUri("?_count=10&patient=p1");
    var results = CoverageSamples.VistaLhsLighthouseRpcGateway.create().getsManifestResults();
    when(vlClient.requestForPatient(
            eq("p1"), any(LhsLighthouseRpcGatewayCoverageSearch.Request.class)))
        .thenReturn(
            RpcResponse.builder()
                .status(RpcResponse.Status.OK)
                .results(
                    List.of(
                        RpcInvocationResult.builder().vista("888").response(json(results)).build(),
                        RpcInvocationResult.builder()
                            .vista("666")
                            .error(Optional.of("I'm a failed response who'll get ignored."))
                            .build()))
                .build());
    var actual = controller().coverageSearch(request, "p1", 1, 10);
    var expected =
        CoverageSamples.R4.asBundle(
            "http://fugazi.com/r4",
            List.of(CoverageSamples.R4.create().coverage("888", "1,8,", "p1")),
            1,
            link(
                BundleLink.LinkRelation.self,
                "http://fugazi.com/r4/Coverage",
                "_count=10&patient=p1"));
    assertThat(json(actual)).isEqualTo(json(expected));
  }

  @Test
  void searchByPatientWithoutResults() {
    var request = requestFromUri("?page=1&_count=10&patient=p1");
    var results = LhsLighthouseRpcGatewayResponse.Results.builder().build();
    when(vlClient.requestForPatient(
            eq("p1"), any(LhsLighthouseRpcGatewayCoverageSearch.Request.class)))
        .thenReturn(
            RpcResponse.builder()
                .status(RpcResponse.Status.OK)
                .results(
                    List.of(
                        RpcInvocationResult.builder().vista("888").response(json(results)).build()))
                .build());
    var actual = controller().coverageSearch(request, "p1", 1, 10);
    var expected =
        CoverageSamples.R4.asBundle(
            "http://fugazi.com/r4",
            List.of(),
            0,
            link(
                BundleLink.LinkRelation.self,
                "http://fugazi.com/r4/Coverage",
                "page=1&_count=10&patient=p1"));
    assertThat(json(actual)).isEqualTo(json(expected));
  }
}
