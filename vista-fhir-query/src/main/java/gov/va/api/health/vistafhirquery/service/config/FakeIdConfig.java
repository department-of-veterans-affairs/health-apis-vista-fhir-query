package gov.va.api.health.vistafhirquery.service.config;

import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.FakeIdFilter;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.FakeIds;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
@Slf4j
public class FakeIdConfig {

  /** Register the FakeIdFilter on any R4 path. */
  @Bean
  public FilterRegistrationBean<FakeIdFilter> fakeId(@Autowired FakeIds fakeIds) {
    var registration = new FilterRegistrationBean<FakeIdFilter>();
    /*
     * We want this filter to go first, before any other filter has a chance to see the request
     * parameters.
     */
    registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
    registration.setFilter(FakeIdFilter.of(fakeIds));
    /*
     * Apply this filter to any R4 path, this includes the default `/r4/` path but must also include
     * any paths rewritten, e.g. `/vista-fhir-query/r4`. This filter will be applied before path
     * rewrite.
     */
    registration.addUrlPatterns("/r4/*", PathRewriteConfig.leadingPath() + "r4/*");
    return registration;
  }

  /** Produce a FakeIds implementation optimized for configuration properties. */
  @Bean
  public FakeIds fakeIds(@Autowired FakeIdProperties properties) {
    if (properties.isEnabled()) {
      log.info("Fake IDs are enabled: {}", properties.getId());
      return FakeIds.MappedFakeIds.builder()
          .patientIdParameters(properties.getParameters())
          .publicToPrivateIds(properties.getId())
          .build();
    }
    log.info("Fake IDs are disabled");
    return new FakeIds.DisabledFakeIds();
  }
}
