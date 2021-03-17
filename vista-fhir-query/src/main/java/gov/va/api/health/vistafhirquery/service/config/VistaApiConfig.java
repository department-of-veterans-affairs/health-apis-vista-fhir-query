package gov.va.api.health.vistafhirquery.service.config;

import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.isBlank;

import gov.va.api.health.vistafhirquery.service.controller.ResourceExceptions;
import gov.va.api.lighthouse.charon.api.RpcPrincipal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Builder
@Configuration
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = false)
@EnableConfigurationProperties
@ConfigurationProperties("vista.api")
public class VistaApiConfig {
  private String url;

  private String clientKey;

  private String accessCode;

  private String verifyCode;

  private String applicationProxyUser;

  private AuthenticationType authenticationType;

  private boolean anyBlank(Object... values) {
    for (Object val : values) {
      if (isBlank(val)) {
        return true;
      }
    }
    return false;
  }

  private RpcPrincipal applicationProxyUser() {
    if (anyBlank(getApplicationProxyUser(), getAccessCode(), getVerifyCode())) {
      ResourceExceptions.ExpectationFailed.because(
          "To use %s authentication, an access code, verify code, and proxy-user must be set.",
          AuthenticationType.APPLICATION_PROXY_USER.toString());
    }
    return RpcPrincipal.applicationProxyUserBuilder()
        .applicationProxyUser(getApplicationProxyUser())
        .accessCode(getAccessCode())
        .verifyCode(getVerifyCode())
        .build();
  }

  /**
   * Use the authentication type provided in the configuration in order to determine and build an
   * RpcPrincipal to use in a Charon API request.
   */
  public RpcPrincipal getAuthenticationForUserType() {
    if (getAuthenticationType() == null) {
      ResourceExceptions.ExpectationFailed.because(
          "To get an authentication scheme, authentication type must be set.");
    }
    switch (getAuthenticationType()) {
      case STANDARD_USER:
        return standardUser();
      case APPLICATION_PROXY_USER:
        return applicationProxyUser();
      default:
        throw new IllegalStateException(
            "Invalid Vista Authentication Type: " + getAuthenticationType());
    }
  }

  private RpcPrincipal standardUser() {
    if (anyBlank(getAccessCode(), getVerifyCode())) {
      ResourceExceptions.ExpectationFailed.because(
          "To use %s authentication, access and verify codes must be set.",
          AuthenticationType.STANDARD_USER.toString());
    }
    return RpcPrincipal.standardUserBuilder()
        .accessCode(getAccessCode())
        .verifyCode(getVerifyCode())
        .build();
  }

  public enum AuthenticationType {
    STANDARD_USER,
    APPLICATION_PROXY_USER
  }
}
