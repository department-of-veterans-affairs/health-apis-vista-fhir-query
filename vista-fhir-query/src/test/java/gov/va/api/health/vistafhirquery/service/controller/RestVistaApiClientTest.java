package gov.va.api.health.vistafhirquery.service.controller;

import static gov.va.api.lighthouse.charon.api.RpcResponse.Status.FAILED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.health.vistafhirquery.service.config.VistaApiConfig;
import gov.va.api.lighthouse.charon.api.RpcDetails;
import gov.va.api.lighthouse.charon.api.RpcInvocationResult;
import gov.va.api.lighthouse.charon.api.RpcResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class RestVistaApiClientTest {
  RestTemplate rt = mock(RestTemplate.class);

  static Stream<Arguments> clientTypes() {
    return Stream.of(
        arguments(
            VistaApiConfig.builder()
                .url("http://fugazi.com/")
                .authenticationType(VistaApiConfig.AuthenticationType.STANDARD_USER)
                .accessCode("ac")
                .verifyCode("vc")
                .clientKey("ck")
                .build()),
        arguments(
            VistaApiConfig.builder()
                .url("http://fugazi.com/")
                .authenticationType(VistaApiConfig.AuthenticationType.APPLICATION_PROXY_USER)
                .applicationProxyUser("apu")
                .accessCode("ac")
                .verifyCode("vc")
                .clientKey("ck")
                .build()));
  }

  static Stream<Arguments> getAuthenticationForUserTypeThrowsWhenCriteriaIsNotMet() {
    return Stream.of(
        arguments(
            VistaApiConfig.builder()
                .authenticationType(VistaApiConfig.AuthenticationType.APPLICATION_PROXY_USER)
                .accessCode("ac")
                .verifyCode("vc")
                .build()),
        arguments(
            VistaApiConfig.builder()
                .authenticationType(VistaApiConfig.AuthenticationType.APPLICATION_PROXY_USER)
                .applicationProxyUser("apu")
                .verifyCode("vc")
                .build()),
        arguments(
            VistaApiConfig.builder()
                .authenticationType(VistaApiConfig.AuthenticationType.APPLICATION_PROXY_USER)
                .applicationProxyUser("apu")
                .accessCode("ac")
                .build()),
        arguments(
            VistaApiConfig.builder()
                .authenticationType(VistaApiConfig.AuthenticationType.APPLICATION_PROXY_USER)
                .build()),
        arguments(
            VistaApiConfig.builder()
                .authenticationType(VistaApiConfig.AuthenticationType.STANDARD_USER)
                .accessCode("ac")
                .build()),
        arguments(
            VistaApiConfig.builder()
                .authenticationType(VistaApiConfig.AuthenticationType.STANDARD_USER)
                .verifyCode("vc")
                .build()));
  }

  private RestVistaApiClient clientFor(VistaApiConfig config) {
    return RestVistaApiClient.builder().config(config).restTemplate(rt).build();
  }

  @ParameterizedTest
  @MethodSource
  void getAuthenticationForUserTypeThrowsWhenCriteriaIsNotMet(VistaApiConfig vistaApiConfig) {
    assertThatExceptionOfType(ResourceExceptions.ExpectationFailed.class)
        .isThrownBy(vistaApiConfig::getAuthenticationForUserType);
  }

  void mockVistalink200Response() {
    when(rt.exchange(any(), eq(RpcResponse.class)))
        .thenReturn(
            ResponseEntity.status(200)
                .body(
                    RpcResponse.builder()
                        .status(RpcResponse.Status.OK)
                        .results(
                            List.of(
                                RpcInvocationResult.builder()
                                    .vista("1")
                                    .response("SUCCESS")
                                    .build()))
                        .build()));
  }

  void mockVistalink500Response() {
    when(rt.exchange(any(), eq(RpcResponse.class)))
        .thenReturn(
            ResponseEntity.status(500)
                .body(
                    RpcResponse.builder()
                        .status(FAILED)
                        .results(
                            List.of(
                                RpcInvocationResult.builder()
                                    .vista("1")
                                    .error(Optional.of("OOF"))
                                    .build()))
                        .build()));
  }

  @ParameterizedTest
  @MethodSource(value = "clientTypes")
  void requestForPatientWithVistalink200Response(VistaApiConfig vistaApiConfig) {
    mockVistalink200Response();
    assertThat(
            clientFor(vistaApiConfig)
                .requestForPatient(
                    "p1", RpcDetails.builder().name("FAUX RPC").context("FAUX CONTEXT").build()))
        .isEqualTo(
            RpcResponse.builder()
                .status(RpcResponse.Status.OK)
                .results(
                    List.of(RpcInvocationResult.builder().vista("1").response("SUCCESS").build()))
                .build());
  }

  @ParameterizedTest
  @MethodSource(value = "clientTypes")
  void requestForPatientWithVistalink500Response(VistaApiConfig vistaApiConfig) {
    mockVistalink500Response();
    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(
            () ->
                clientFor(vistaApiConfig)
                    .requestForPatient(
                        "p1",
                        RpcDetails.builder().name("FAUX RPC").context("FAUX CONTEXT").build()));
  }

  @ParameterizedTest
  @MethodSource(value = "clientTypes")
  void requestForVistaSiteWithVistalink200Response(VistaApiConfig vistaApiConfig) {
    mockVistalink200Response();
    assertThat(
            clientFor(vistaApiConfig)
                .requestForVistaSite(
                    "123", RpcDetails.builder().name("FAUX RPC").context("FAUX CONTEXT").build()))
        .isEqualTo(
            RpcResponse.builder()
                .status(RpcResponse.Status.OK)
                .results(
                    List.of(RpcInvocationResult.builder().vista("1").response("SUCCESS").build()))
                .build());
  }

  @ParameterizedTest
  @MethodSource(value = "clientTypes")
  void requestForVistaSiteWithVistalink500Response(VistaApiConfig vistaApiConfig) {
    mockVistalink500Response();
    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(
            () ->
                clientFor(vistaApiConfig)
                    .requestForVistaSite(
                        "123",
                        RpcDetails.builder().name("FAUX RPC").context("FAUX CONTEXT").build()));
  }
}
