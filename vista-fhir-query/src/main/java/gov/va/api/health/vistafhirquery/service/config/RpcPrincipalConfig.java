package gov.va.api.health.vistafhirquery.service.config;

import static java.util.stream.Collectors.joining;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.charon.api.RpcPrincipalLookup;
import gov.va.api.lighthouse.charon.api.RpcPrincipals;
import java.io.File;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@NoArgsConstructor
public class RpcPrincipalConfig {

  @Bean
  @SneakyThrows
  RpcPrincipalLookup loadPrincipals(
      @NonNull @Value("${vista-fhir-query.rpc-principals.file}") String principalsFile) {
    RpcPrincipals rpcPrincipals =
        new ObjectMapper().readValue(new File(principalsFile), RpcPrincipals.class);
    validate(rpcPrincipals, principalsFile);
    return RpcPrincipalLookup.of(rpcPrincipals);
  }

  void validate(RpcPrincipals rpcPrincipals, String principalsFile) {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    var violations = validator.validate(rpcPrincipals);
    if (!violations.isEmpty()) {
      throw new IllegalArgumentException(
          "Failed to validate "
              + principalsFile
              + ": \n"
              + violations.stream().map(ConstraintViolation::getMessage).collect(joining("\n")));
    }
  }
}
