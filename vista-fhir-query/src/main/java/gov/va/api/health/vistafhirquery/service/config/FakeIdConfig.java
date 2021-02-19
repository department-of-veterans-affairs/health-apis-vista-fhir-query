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

  @Bean
  public FilterRegistrationBean<FakeIdFilter> fakeId(@Autowired FakeIds fakeIds) {
    var registration = new FilterRegistrationBean<FakeIdFilter>();
    registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
    registration.setFilter(FakeIdFilter.of(fakeIds));
    registration.addUrlPatterns("/r4/*", PathRewriteConfig.leadingPath() + "r4/*");
    return registration;
  }

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
