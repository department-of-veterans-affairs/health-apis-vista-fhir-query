package gov.va.api.health.vistafhirquery.service.config;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import gov.va.api.lighthouse.charon.api.RpcPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class VistaApiConfigTest {

  @Test
  void loadSiteSpecificInfoFromFile() {
    var config =
        VistaApiConfig.builder().principalFile("src/test/resources/principals.json").build();
    config.loadSiteSpecificInfoFromFile();
    assertThat(config.getPrincipalOrDefault("123"))
        .isEqualTo(
            RpcPrincipal.builder()
                .applicationProxyUser("FAKE:APU@999")
                .accessCode("FAKE:AC@999")
                .verifyCode("FAKE:VC@999")
                .build());
    assertThat(config.getPrincipalOrDefault("999"))
        .isEqualTo(RpcPrincipal.builder().accessCode("FAKE:AC").verifyCode("FAKE:VC").build());
  }
}
