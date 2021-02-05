package gov.va.api.health.vistafhirquery.service.controller.observation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.health.r4.api.resources.Observation;
import gov.va.api.health.vistafhirquery.service.config.LinkProperties;
import gov.va.api.health.vistafhirquery.service.controller.ResourceExceptions;
import gov.va.api.health.vistafhirquery.service.controller.VistalinkApiClient;
import gov.va.api.lighthouse.vistalink.api.RpcDetails;
import gov.va.api.lighthouse.vistalink.api.RpcInvocationResult;
import gov.va.api.lighthouse.vistalink.api.RpcResponse;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class R4ObservationControllerTest {
  VistalinkApiClient vlClient = mock(VistalinkApiClient.class);

  private R4ObservationController controller() {
    return new R4ObservationController(
        vlClient,
        LinkProperties.builder()
            .defaultPageSize(15)
            .maxPageSize(100)
            .publicUrl("http://fugazi.com")
            .publicR4BasePath("r4")
            .build());
  }

  @Test
  @SneakyThrows
  void read() {
    String responseBody =
        new String(
            getClass().getResourceAsStream("vitals-only-rpcresponse-read.xml").readAllBytes());
    when(vlClient.requestForVistaSite(eq("123"), any(RpcDetails.class)))
        .thenReturn(
            RpcResponse.builder()
                .status(RpcResponse.Status.OK)
                .results(
                    List.of(
                        RpcInvocationResult.builder().vista("123").response(responseBody).build()))
                .build());
    var actual = controller().read("Np1+123+456");
    assertThat(actual).isEqualTo(Observation.builder().id("Np1+123+456").build());
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
    assertThatExceptionOfType(ResourceExceptions.NotFound.class)
        .isThrownBy(() -> controller().read("Np1+123+NOPE"));
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
