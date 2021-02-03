package gov.va.api.health.vistafhirquery.service.config;

import gov.va.api.lighthouse.talos.PathRewriteFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class PathRewriteConfig {
  @Bean
  FilterRegistrationBean<PathRewriteFilter> pathRewriteFilter() {
    var registration = new FilterRegistrationBean<PathRewriteFilter>();
    PathRewriteFilter filter =
        PathRewriteFilter.builder().removeLeadingPath("/vista-fhir-query/").build();
    registration.setFilter(filter);
    registration.setOrder(Ordered.LOWEST_PRECEDENCE);
    registration.addUrlPatterns(filter.removeLeadingPathsAsUrlPatterns());
    return registration;
  }
}
