package gov.va.api.health.vistafhirquery.service.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HttpRequestParametersTest {

  @Mock HttpServletRequest request;

  @Test
  void integer() {
    when(request.getParameter("x")).thenReturn("99");
    assertThat(HttpRequestParameters.integer(request, "x", 88)).isEqualTo(99);

    when(request.getParameter("x")).thenReturn(null);
    assertThat(HttpRequestParameters.integer(request, "x", 88)).isEqualTo(88);

    when(request.getParameter("x")).thenReturn("");
    assertThat(HttpRequestParameters.integer(request, "x", 88)).isEqualTo(88);

    when(request.getParameter("x")).thenReturn("nope");
    assertThat(HttpRequestParameters.integer(request, "x", 88)).isEqualTo(88);
  }
}
