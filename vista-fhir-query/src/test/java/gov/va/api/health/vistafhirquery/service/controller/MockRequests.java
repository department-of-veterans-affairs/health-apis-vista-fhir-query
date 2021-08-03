package gov.va.api.health.vistafhirquery.service.controller;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.util.UriComponentsBuilder;

@UtilityClass
public class MockRequests {
  @SneakyThrows
  public static String json(Object o) {
    return JacksonConfig.createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(o);
  }

  public static MockHttpServletRequest requestFromUri(String uri) {
    var u = UriComponentsBuilder.fromUriString(uri).build();
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI(u.getPath());
    request.setRemoteHost(u.getHost());
    request.setProtocol(u.getScheme());
    request.setServerPort(u.getPort());
    u.getQueryParams()
        .entrySet()
        .forEach(e -> request.addParameter(e.getKey(), e.getValue().toArray(new String[0])));
    return request;
  }
}
