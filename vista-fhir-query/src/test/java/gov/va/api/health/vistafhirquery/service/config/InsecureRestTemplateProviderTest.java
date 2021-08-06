package gov.va.api.health.vistafhirquery.service.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;

public class InsecureRestTemplateProviderTest {
  @Test
  void honorsTimeOutValues() {
    var readTimeout = Duration.ofSeconds(43);
    var connectionTimeout = Duration.ofSeconds(54);

    var rtb = mock(RestTemplateBuilder.class);
    when(rtb.setReadTimeout(any())).thenReturn(rtb);
    when(rtb.setConnectTimeout(any())).thenReturn(rtb);
    when(rtb.requestFactory(any(Supplier.class))).thenReturn(rtb);

    var rt = new InsecureRestTemplateProvider().restTemplate(rtb, connectionTimeout, readTimeout);

    verify(rtb).setReadTimeout(readTimeout);
    verify(rtb).setConnectTimeout(connectionTimeout);
  }
}
