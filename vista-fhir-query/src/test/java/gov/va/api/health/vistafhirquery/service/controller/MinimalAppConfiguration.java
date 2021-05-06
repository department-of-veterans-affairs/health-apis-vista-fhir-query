package gov.va.api.health.vistafhirquery.service.controller;

import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.AlternatePatientIds;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.AlternatePatientIds.DisabledAlternatePatientIds;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinimalAppConfiguration {
  @Bean
  AlternatePatientIds disabledAlternatePatientIds() {
    return new DisabledAlternatePatientIds();
  }
}
