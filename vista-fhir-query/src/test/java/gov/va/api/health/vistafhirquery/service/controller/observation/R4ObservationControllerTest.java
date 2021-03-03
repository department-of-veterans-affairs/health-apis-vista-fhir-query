package gov.va.api.health.vistafhirquery.service.controller.observation;

import static gov.va.api.health.vistafhirquery.service.controller.MockRequests.requestFromUri;
import static gov.va.api.health.vistafhirquery.service.controller.observation.ObservationVitalSamples.Fhir.link;
import static gov.va.api.health.vistafhirquery.service.controller.observation.ObservationVitalSamples.json;
import static gov.va.api.health.vistafhirquery.service.controller.observation.ObservationVitalSamples.xml;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.health.r4.api.bundle.BundleLink;
import gov.va.api.health.vistafhirquery.service.config.LinkProperties;
import gov.va.api.health.vistafhirquery.service.controller.R4BundlerFactory;
import gov.va.api.health.vistafhirquery.service.controller.ResourceExceptions;
import gov.va.api.health.vistafhirquery.service.controller.VistalinkApiClient;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.AlternatePatientIds.DisabledAlternatePatientIds;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.WitnessProtection;
import gov.va.api.lighthouse.vistalink.api.RpcDetails;
import gov.va.api.lighthouse.vistalink.api.RpcInvocationResult;
import gov.va.api.lighthouse.vistalink.api.RpcResponse;
import gov.va.api.lighthouse.vistalink.models.vprgetpatientdata.VprGetPatientData;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class R4ObservationControllerTest {
  private static VitalVuidMapper mapper;

  @Mock VistalinkApiClient vlClient;

  @Mock WitnessProtection wp;

  @BeforeAll
  static void _init() {
    VitalVuidMappingRepository repository = mock(VitalVuidMappingRepository.class);
    when(repository.findByCodingSystemId(eq((short) 11)))
        .thenReturn(ObservationVitalSamples.Datamart.create().mappingEntities());
    mapper = new VitalVuidMapper(repository);
  }

  private R4ObservationController controller() {
    var bundlerFactory =
        R4BundlerFactory.builder()
            .linkProperties(
                LinkProperties.builder()
                    .defaultPageSize(15)
                    .maxPageSize(100)
                    .publicUrl("http://fugazi.com")
                    .publicR4BasePath("r4")
                    .build())
            .alternatePatientIds(new DisabledAlternatePatientIds())
            .build();
    return R4ObservationController.builder()
        .vistalinkApiClient(vlClient)
        .vitalVuids(mapper)
        .witnessProtection(wp)
        .bundlerFactory(bundlerFactory)
        .build();
  }

  @Test
  @SneakyThrows
  void read() {
    var vista = ObservationVitalSamples.Vista.create();
    VprGetPatientData.Response.Results results = vista.results();
    results.vitals().vitalResults().get(0).measurements(List.of(vista.weight("456")));
    when(vlClient.requestForVistaSite(eq("123"), any(RpcDetails.class)))
        .thenReturn(rpcResponse(RpcResponse.Status.OK, "123", xml(results)));
    when(wp.toPrivateId("public-Np1+123+456")).thenReturn("Np1+123+456");
    var actual = controller().read("public-Np1+123+456");
    assertThat(json(actual))
        .isEqualTo(json(ObservationVitalSamples.Fhir.create().weight("Np1+123+456")));
  }

  @Test
  void readUnusableIdReturnsNotFound() {
    when(wp.toPrivateId("garbage")).thenReturn("garbage");
    assertThatExceptionOfType(ResourceExceptions.NotFound.class)
        .isThrownBy(() -> controller().read("garbage"));
  }

  @Test
  void readVitalsNotFound() {
    var responseBody =
        "<results version='1.13' timeZone='-0500'><vitals total='1'><vital></vital></vitals></results>";
    when(vlClient.requestForVistaSite(eq("123"), any(RpcDetails.class)))
        .thenReturn(rpcResponse(RpcResponse.Status.OK, "123", responseBody));
    when(wp.toPrivateId("public-Np1+123+NOPE")).thenReturn("Np1+123+NOPE");
    assertThatExceptionOfType(ResourceExceptions.NotFound.class)
        .isThrownBy(() -> controller().read("public-Np1+123+NOPE"));
  }

  private RpcResponse rpcResponse(RpcResponse.Status status, String siteId, String response) {
    return RpcResponse.builder()
        .status(status)
        .results(List.of(RpcInvocationResult.builder().vista(siteId).response(response).build()))
        .build();
  }

  @Test
  void searchByPatientAndCodeKnownAndUnknown() {
    var request = requestFromUri("?_count=10&code=29463-7,NOPE&patient=p1");
    var results = ObservationVitalSamples.Vista.create().results();
    when(vlClient.requestForPatient(eq("p1"), any(RpcDetails.class)))
        .thenReturn(rpcResponse(RpcResponse.Status.OK, "673", xml(results)));
    var actual = controller().searchByPatient("p1", null, "29463-7,NOPE", 10, request);
    var expected =
        ObservationVitalSamples.Fhir.asBundle(
            "http://fugazi.com/r4",
            List.of(ObservationVitalSamples.Fhir.create().weight()),
            1,
            link(
                BundleLink.LinkRelation.self,
                "http://fugazi.com/r4/Observation",
                "_count=10&code=29463-7,NOPE&patient=p1"));
    assertThat(json(actual)).isEqualTo(json(expected));
  }

  @Test
  void searchByPatientAndDateAndCode() {
    var request = requestFromUri("?_count=10&date=2010&code=29463-7&patient=p1");
    var results = ObservationVitalSamples.Vista.create().results();
    when(vlClient.requestForPatient(eq("p1"), any(RpcDetails.class)))
        .thenReturn(rpcResponse(RpcResponse.Status.OK, "673", xml(results)));
    var actual = controller().searchByPatient("p1", new String[] {"2010"}, "29463-7", 10, request);
    var expected =
        ObservationVitalSamples.Fhir.asBundle(
            "http://fugazi.com/r4",
            List.of(ObservationVitalSamples.Fhir.create().weight()),
            1,
            link(
                BundleLink.LinkRelation.self,
                "http://fugazi.com/r4/Observation",
                "_count=10&date=2010&code=29463-7&patient=p1"));
    assertThat(json(actual)).isEqualTo(json(expected));
  }

  @Test
  void searchByPatientAndKnownCode() {
    var request = requestFromUri("?_count=10&code=29463-7&patient=p1");
    var results = ObservationVitalSamples.Vista.create().results();
    when(vlClient.requestForPatient(eq("p1"), any(RpcDetails.class)))
        .thenReturn(rpcResponse(RpcResponse.Status.OK, "673", xml(results)));
    var actual = controller().searchByPatient("p1", null, "29463-7", 10, request);
    var expected =
        ObservationVitalSamples.Fhir.asBundle(
            "http://fugazi.com/r4",
            List.of(ObservationVitalSamples.Fhir.create().weight()),
            1,
            link(
                BundleLink.LinkRelation.self,
                "http://fugazi.com/r4/Observation",
                "_count=10&code=29463-7&patient=p1"));
    assertThat(json(actual)).isEqualTo(json(expected));
  }

  @Test
  void searchByPatientAndMultipleDates() {
    var request = requestFromUri("?_count=10&date=ge2010&date=lt2012&patient=p1");
    var results = ObservationVitalSamples.Vista.create().results();
    when(vlClient.requestForPatient(eq("p1"), any(RpcDetails.class)))
        .thenReturn(rpcResponse(RpcResponse.Status.OK, "673", xml(results)));
    var actual =
        controller().searchByPatient("p1", new String[] {"ge2010", "lt2012"}, null, 10, request);
    var expected =
        ObservationVitalSamples.Fhir.asBundle(
            "http://fugazi.com/r4",
            ObservationVitalSamples.Fhir.create().observations(),
            2,
            link(
                BundleLink.LinkRelation.self,
                "http://fugazi.com/r4/Observation",
                "_count=10&date=ge2010&date=lt2012&patient=p1"));
    assertThat(json(actual)).isEqualTo(json(expected));
  }

  @Test
  void searchByPatientAndMultipleKnownCode() {
    var request = requestFromUri("?_count=10&code=29463-7,55284-4&patient=p1");
    var results = ObservationVitalSamples.Vista.create().results();
    when(vlClient.requestForPatient(eq("p1"), any(RpcDetails.class)))
        .thenReturn(rpcResponse(RpcResponse.Status.OK, "673", xml(results)));
    var actual = controller().searchByPatient("p1", null, "29463-7,55284-4", 10, request);
    var expected =
        ObservationVitalSamples.Fhir.asBundle(
            "http://fugazi.com/r4",
            ObservationVitalSamples.Fhir.create().observations(),
            2,
            link(
                BundleLink.LinkRelation.self,
                "http://fugazi.com/r4/Observation",
                "_count=10&code=29463-7,55284-4&patient=p1"));
    assertThat(json(actual)).isEqualTo(json(expected));
  }

  @Test
  void searchByPatientAndUnknownCode() {
    var request = requestFromUri("?_count=10&code=NOPE&patient=p1");
    var results = ObservationVitalSamples.Vista.create().results();
    when(vlClient.requestForPatient(eq("p1"), any(RpcDetails.class)))
        .thenReturn(rpcResponse(RpcResponse.Status.OK, "123", xml(results)));
    var actual = controller().searchByPatient("p1", null, "NOPE", 10, request);
    var expected =
        ObservationVitalSamples.Fhir.asBundle(
            "http://fugazi.com/r4",
            List.of(),
            0,
            link(
                BundleLink.LinkRelation.self,
                "http://fugazi.com/r4/Observation",
                "_count=10&code=NOPE&patient=p1"));
    assertThat(json(actual)).isEqualTo(json(expected));
  }

  @Test
  void searchByPatientWithVistaEmptyResults() {
    var request = requestFromUri("?_count=10&patient=p1");
    var responseBody =
        "<results version='1.13' timeZone='-0500'><vitals total='1'><vital></vital></vitals></results>";
    when(vlClient.requestForPatient(eq("p1"), any(RpcDetails.class)))
        .thenReturn(rpcResponse(RpcResponse.Status.OK, "123", responseBody));
    var actual = controller().searchByPatient("p1", null, null, 10, request);
    var expected =
        ObservationVitalSamples.Fhir.asBundle(
            "http://fugazi.com/r4",
            List.of(),
            0,
            link(
                BundleLink.LinkRelation.self,
                "http://fugazi.com/r4/Observation",
                "_count=10&patient=p1"));
    assertThat(json(actual)).isEqualTo(json(expected));
  }

  @Test
  @SneakyThrows
  void searchByPatientWithVistaPopulatedResults() {
    var request = requestFromUri("?_count=10&patient=p1");
    var results = ObservationVitalSamples.Vista.create().results();
    when(vlClient.requestForPatient(eq("p1"), any(RpcDetails.class)))
        .thenReturn(rpcResponse(RpcResponse.Status.OK, "673", xml(results)));
    var actual = controller().searchByPatient("p1", null, null, 10, request);
    var expected =
        ObservationVitalSamples.Fhir.asBundle(
            "http://fugazi.com/r4",
            ObservationVitalSamples.Fhir.create().observations(),
            2,
            link(
                BundleLink.LinkRelation.self,
                "http://fugazi.com/r4/Observation",
                "_count=10&patient=p1"));
    assertThat(json(actual)).isEqualTo(json(expected));
  }
}
