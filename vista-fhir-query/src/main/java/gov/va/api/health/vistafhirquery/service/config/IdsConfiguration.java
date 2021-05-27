package gov.va.api.health.vistafhirquery.service.config;

import gov.va.api.health.ids.client.EncryptingIdEncoder.Codebook;
import gov.va.api.health.vistafhirquery.idsmapping.VistaFhirQueryIdsCodebookSupplier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Loads configuration of the IDS. */
@Configuration
public class IdsConfiguration {
  @Bean
  @ConditionalOnMissingBean
  Codebook codebook() {
    return new VistaFhirQueryIdsCodebookSupplier().get();
  }
}
