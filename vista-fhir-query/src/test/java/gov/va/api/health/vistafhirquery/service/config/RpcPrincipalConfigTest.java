package gov.va.api.health.vistafhirquery.service.config;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import gov.va.api.lighthouse.charon.api.RpcPrincipal;
import gov.va.api.lighthouse.charon.api.RpcPrincipalLookup;
import gov.va.api.lighthouse.charon.api.RpcPrincipals;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class RpcPrincipalConfigTest {

  @Test
  void loadPrincipals() {
    RpcPrincipalLookup testPrincipals =
        new RpcPrincipalConfig().loadPrincipals("src/test/resources/principals.json");
    assertThat(testPrincipals.findByNameAndSite("SASHIMI", "222-A"))
        .isEqualTo(
            Optional.of(
                RpcPrincipal.builder()
                    .applicationProxyUser("ASIAN!")
                    .accessCode("ASIAN_FOOD")
                    .verifyCode("IS_STILL_GREAT")
                    .build()));
  }

  @Test
  void loadPrincipalsNullFile() {
    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(() -> new RpcPrincipalConfig().loadPrincipals(null));
  }

  @Test
  void validate() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> new RpcPrincipalConfig().validate(RpcPrincipals.builder().build(), "whatever"));
  }
}
