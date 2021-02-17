package gov.va.api.health.vistafhirquery.service.controller.observation;

import static gov.va.api.health.vistafhirquery.service.controller.observation.ObservationVitalSamples.json;
import static gov.va.api.health.vistafhirquery.service.controller.observation.ObservationVitalSamples.xml;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.health.vistafhirquery.service.config.LinkProperties;
import gov.va.api.health.vistafhirquery.service.controller.ResourceExceptions;
import gov.va.api.health.vistafhirquery.service.controller.VistalinkApiClient;
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
    return R4ObservationController.builder()
        .vistalinkApiClient(vlClient)
        .witnessProtection(wp)
        .linkProperties(
            LinkProperties.builder()
                .defaultPageSize(15)
                .maxPageSize(100)
                .publicUrl("http://fugazi.com")
                .publicR4BasePath("r4")
                .build())
        .vitalVuids(mapper)
        .build();
  }

  @Test
  @SneakyThrows
  void read() {
    var vista = ObservationVitalSamples.Vista.create();
    VprGetPatientData.Response.Results sample = vista.results();
    sample.vitals().vitalResults().get(0).measurements(List.of(vista.weight("456")));
    String responseBody = xml(sample);
    when(vlClient.requestForVistaSite(eq("123"), any(RpcDetails.class)))
        .thenReturn(
            RpcResponse.builder()
                .status(RpcResponse.Status.OK)
                .results(
                    List.of(
                        RpcInvocationResult.builder().vista("123").response(responseBody).build()))
                .build());
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
        .thenReturn(
            RpcResponse.builder()
                .status(RpcResponse.Status.OK)
                .results(
                    List.of(
                        RpcInvocationResult.builder().vista("123").response(responseBody).build()))
                .build());
    when(wp.toPrivateId("public-Np1+123+NOPE")).thenReturn("Np1+123+NOPE");
    assertThatExceptionOfType(ResourceExceptions.NotFound.class)
        .isThrownBy(() -> controller().read("public-Np1+123+NOPE"));
  }

  @Test
  void searchByPatientWithVistaEmptyResults() {
    var responseBody =
        "<results version='1.13' timeZone='-0500'><vitals total='1'><vital></vital></vitals></results>";
    when(vlClient.requestForPatient(eq("p1"), any(RpcDetails.class)))
        .thenReturn(
            RpcResponse.builder()
                .status(RpcResponse.Status.OK)
                .results(
                    List.of(
                        RpcInvocationResult.builder().vista("123").response(responseBody).build()))
                .build());
    var actual = controller().searchByPatient("p1", 10);
    assertThat(actual.entry()).isEmpty();
  }

  @Test
  @SneakyThrows
  void searchByPatientWithVistaPopulatedResults() {
    String responseBody =
        new String(
            getClass().getResourceAsStream("vitals-only-rpcresponse-search.xml").readAllBytes());
    when(vlClient.requestForPatient(eq("p1"), any(RpcDetails.class)))
        .thenReturn(
            RpcResponse.builder()
                .status(RpcResponse.Status.OK)
                .results(
                    List.of(
                        RpcInvocationResult.builder().vista("123").response(responseBody).build()))
                .build());
    var actual = controller().searchByPatient("p1", 10);
    assertThat(actual.entry()).isNotEmpty();
  }
}
