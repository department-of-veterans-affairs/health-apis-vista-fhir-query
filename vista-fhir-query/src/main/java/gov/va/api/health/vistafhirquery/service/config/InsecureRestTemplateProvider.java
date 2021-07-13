package gov.va.api.health.vistafhirquery.service.config;

import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.function.Supplier;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/** Rest template without SSL. */
@Slf4j
@Component
public class InsecureRestTemplateProvider {
  private Supplier<ClientHttpRequestFactory> bufferingRequestFactory(HttpClient client) {
    return () ->
        new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
  }

  @SneakyThrows
  private CloseableHttpClient httpClientWithoutSsl() {
    return HttpClients.custom()
        .setSSLSocketFactory(
            new SSLConnectionSocketFactory(
                SSLContexts.custom()
                    .loadTrustMaterial(null, (X509Certificate[] chain, String authType) -> true)
                    .build()))
        .build();
  }

  /** Create RestTemplate with SSL disabled. */
  @Bean
  public RestTemplate restTemplate(
      @Autowired RestTemplateBuilder restTemplateBuilder,
      @Value("${rest-template.connection-timeout:#{null}}") Long connectionTimeout,
      @Value("${rest-template.read-timeout:#{null}}") Long readTimeout) {
    log.info("Creating RestTemplate using: {}", getClass().getSimpleName());
    if (connectionTimeout != null) {
      log.info("Setting connection timeout to {} seconds.", connectionTimeout);
      restTemplateBuilder =
          restTemplateBuilder.setReadTimeout(Duration.ofSeconds(connectionTimeout));
    }
    if (readTimeout != null) {
      log.info("Setting read timeout to {} seconds.", readTimeout);
      restTemplateBuilder = restTemplateBuilder.setReadTimeout(Duration.ofSeconds(readTimeout));
    }
    return restTemplateBuilder
        .requestFactory(bufferingRequestFactory(httpClientWithoutSsl()))
        .build();
  }
}
