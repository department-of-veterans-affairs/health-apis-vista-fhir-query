package gov.va.api.health.vistafhirquery.service.config;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import gov.va.api.lighthouse.charon.api.RpcPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class VistaApiConfigTest {

  @Test
  void defaultPrincipal() {
    var config = loadConfig("src/test/resources/access-code-and-verify-code-and-apu.json");
    assertThat(config.defaultPrincipal())
        .isEqualTo(
            RpcPrincipal.builder()
                .accessCode("fake:ac1")
                .verifyCode("fake:vc1")
                .applicationProxyUser("fake:apu1")
                .build());

    config =
        loadConfig(
            "src/test/resources/"
                + "principals-and-site-specific-principals-and-access-code-and-verify-code-and-apu.json");
    assertThat(config.defaultPrincipal())
        .isEqualTo(
            RpcPrincipal.builder()
                .applicationProxyUser("FAKE:APU@999")
                .accessCode("FAKE:AC@999")
                .verifyCode("FAKE:VC@999")
                .build());
  }

  @Test
  void loadAccessCodeAndVerifyCodeAndApu() {
    var config = loadConfig("src/test/resources/access-code-and-verify-code-and-apu.json");
    assertThat(config.getSiteSpecificPrincipalOrDefault("666"))
        .isEqualTo(
            RpcPrincipal.builder()
                .accessCode("fake:ac1")
                .verifyCode("fake:vc1")
                .applicationProxyUser("fake:apu1")
                .build());
  }

  @Test
  void loadAccessCodeAndVerifyCodeAndApuAndPrincipalAndAltSites() {
    var config =
        loadConfig(
            "src/test/resources/"
                + "principals-and-site-specific-principals-and-access-code-and-verify-code-and-apu.json");
    assertThat(config.getSiteSpecificPrincipalOrDefault("999"))
        .isEqualTo(RpcPrincipal.builder().accessCode("FAKE:AC").verifyCode("FAKE:VC").build());
    assertThat(config.getSiteSpecificPrincipalOrDefault("123"))
        .isEqualTo(
            RpcPrincipal.builder()
                .applicationProxyUser("FAKE:APU@999")
                .accessCode("FAKE:AC@999")
                .verifyCode("FAKE:VC@999")
                .build());
  }

  /*
  @PostConstruct does not run without invoking the Spring Lifecycle,
   so just test the method directly.
  */
  private VistaApiConfig loadConfig(String filePath) {
    var config = VistaApiConfig.builder().principalFile(filePath).build();
    config.loadSiteSpecificInfoFromFile();
    return config;
  }

  @Test
  void loadPrincipalAndAltSites() {
    var config = loadConfig("src/test/resources/principal-and-site-specific-principals.json");
    assertThat(config.getSiteSpecificPrincipalOrDefault("123"))
        .isEqualTo(
            RpcPrincipal.builder()
                .applicationProxyUser("FAKE:APU@999")
                .accessCode("FAKE:AC@999")
                .verifyCode("FAKE:VC@999")
                .build());
    assertThat(config.getSiteSpecificPrincipalOrDefault("999"))
        .isEqualTo(RpcPrincipal.builder().accessCode("FAKE:AC").verifyCode("FAKE:VC").build());
  }
}
