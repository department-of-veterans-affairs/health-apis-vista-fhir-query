package gov.va.api.health.vistafhirquery.service.controller.witnessprotection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.AlternatePatientIdFilter.OverrideParametersHttpServletRequestWrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@ExtendWith(MockitoExtension.class)
class AlternatePatientIdFilterTest {
  @Mock AlternatePatientIds ids;
  @Mock FilterChain filterChain;

  AlternatePatientIdFilter filter() {
    return AlternatePatientIdFilter.of(ids);
  }

  @Test
  @SneakyThrows
  void onlyPatientIdParametersAreModified() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setParameter("patient", "public1");
    request.setParameter("also-patient", "public2");
    request.setParameter("not-patient", "public1");

    when(ids.patientIdParameters()).thenReturn(List.of("patient", "also-patient"));
    when(ids.toPrivateId("public1")).thenReturn("private1");
    when(ids.toPrivateId("public2")).thenReturn("private2");

    filter().doFilterInternal(request, new MockHttpServletResponse(), filterChain);

    ArgumentCaptor<HttpServletRequest> captor = ArgumentCaptor.forClass(HttpServletRequest.class);
    verify(filterChain).doFilter(captor.capture(), Mockito.any());
    assertThat(captor.getValue().getParameter("patient")).isEqualTo("private1");
    assertThat(captor.getValue().getParameter("also-patient")).isEqualTo("private2");
    assertThat(captor.getValue().getParameter("not-patient")).isEqualTo("public1");
  }

  @Test
  void overrideParametersHttpServletRequestWrapperOverridesParameterOperations() {
    var original = new MockHttpServletRequest();
    original.setParameter("a", "a1");
    original.setParameter("b", "b1", "b2");
    original.setParameter("c", "c1");

    var overParameters =
        Map.of(
            "a", new String[] {"newA1"},
            "b", new String[] {"newB1", "newB2"},
            "x", new String[] {"x1"});
    var over = new OverrideParametersHttpServletRequestWrapper(original, overParameters);

    assertThat(over.getParameter("a")).isEqualTo("newA1");
    assertThat(over.getParameter("b")).isEqualTo("newB1"); // must be first value
    assertThat(over.getParameter("c")).isNull();
    assertThat(over.getParameter("x")).isEqualTo("x1");
    assertThat(over.getParameterValues("a")).containsExactlyInAnyOrder("newA1");
    assertThat(over.getParameterValues("b")).containsExactlyInAnyOrder("newB1", "newB2");
    assertThat(over.getParameterMap()).isNotSameAs(overParameters);
    assertThat(over.getParameterMap()).isEqualTo(overParameters);
    assertThatExceptionOfType(UnsupportedOperationException.class)
        .isThrownBy(() -> over.getParameterMap().put("not", new String[] {"allowed"}));

    var enumerator = over.getParameterNames();
    var names = new ArrayList<String>(3);
    while (enumerator.hasMoreElements()) {
      names.add(enumerator.nextElement());
    }
    assertThat(names).containsExactlyInAnyOrder("a", "b", "x");
  }

  @Test
  @SneakyThrows
  void parametersAreNotChangedIfPublicAndPrivateIdsAreTheSame() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setParameter("patient", "public1");
    when(ids.patientIdParameters()).thenReturn(List.of("patient", "also-patient"));
    when(ids.toPrivateId("public1")).thenReturn("public1");

    filter().doFilterInternal(request, new MockHttpServletResponse(), filterChain);

    ArgumentCaptor<HttpServletRequest> captor = ArgumentCaptor.forClass(HttpServletRequest.class);
    verify(filterChain).doFilter(captor.capture(), Mockito.any());
    assertThat(captor.getValue()).isEqualTo(request);
    assertThat(captor.getValue().getParameter("patient")).isEqualTo("public1");
  }
}
