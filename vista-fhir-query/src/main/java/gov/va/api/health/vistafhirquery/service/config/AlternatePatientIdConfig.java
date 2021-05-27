package gov.va.api.health.vistafhirquery.service.config;

import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.AlternatePatientIdFilter;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.AlternatePatientIds;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.AlternatePatientIds.DisabledAlternatePatientIds;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.AlternatePatientIds.MappedAlternatePatientIds;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/** Allows configured alternate id replacement. */
@Configuration
@Slf4j
public class AlternatePatientIdConfig {

  /** Register the AlternatePatientIdFilter on any R4 path. */
  @Bean
  public FilterRegistrationBean<AlternatePatientIdFilter> alternatePatientIdFilter(
      @Autowired AlternatePatientIds alternatePatientIds) {
    var registration = new FilterRegistrationBean<AlternatePatientIdFilter>();
    /*
     * We want this filter to go first, before any other filter has a chance to see the request
     * parameters.
     */
    registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
    registration.setFilter(AlternatePatientIdFilter.of(alternatePatientIds));
    /*
     * Apply this filter to any R4 path, this includes the default `/r4/` path but must also include
     * any paths rewritten, e.g. `/vista-fhir-query/r4`. This filter will be applied before path
     * rewrite.
     */
    registration.addUrlPatterns("/r4/*", PathRewriteConfig.leadingPath() + "r4/*");
    return registration;
  }

  /** Produce an AlternatePatientIds implementation optimized for configuration properties. */
  @Bean
  public AlternatePatientIds alternatePatientIds(
      @Autowired AlternatePatientIdProperties properties) {
    if (properties.isEnabled()) {
      log.info("Alternate patient IDs are enabled: {}", properties.getId());
      return MappedAlternatePatientIds.builder()
          .patientIdParameters(properties.getParameters())
          .publicToPrivateIds(properties.getId())
          .build();
    }
    log.info("Alternate patient IDs are disabled");
    return new DisabledAlternatePatientIds();
  }
}
