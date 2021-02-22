package gov.va.api.health.vistafhirquery.service.controller.witnessprotection;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@AllArgsConstructor(staticName = "of")
public class FakeIdFilter extends OncePerRequestFilter {

  private final FakeIds fakeIds;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    Map<String, String[]> newParameters = null;

    for (String parameter : fakeIds.patientIdParameters()) {
      var publicId = request.getParameter(parameter);
      if (publicId == null) {
        continue;
      }
      var privateId = fakeIds.toPrivateId(publicId);
      if (publicId.equals(privateId)) {
        continue;
      }
      if (newParameters == null) {
        newParameters = new HashMap<>(request.getParameterMap());
      }
      newParameters.put(parameter, new String[] {privateId});
    }

    if (newParameters != null) {
      request = new OverrideParametersHttpServletRequestWrapper(request, newParameters);
    }

    filterChain.doFilter(request, response);
  }

  private static class OverrideParametersHttpServletRequestWrapper
      extends HttpServletRequestWrapper {

    private final Map<String, String[]> parameters;

    public OverrideParametersHttpServletRequestWrapper(
        HttpServletRequest request, Map<String, String[]> parameters) {
      super(request);
      this.parameters = parameters;
    }

    @Override
    public String getParameter(String name) {
      String[] values = parameters.get(name);
      if (values == null || values.length == 0) {
        return null;
      }
      return values[0];
    }

    @Override
    public Map<String, String[]> getParameterMap() {
      return Collections.unmodifiableMap(parameters);
    }

    @Override
    public Enumeration<String> getParameterNames() {
      return Collections.enumeration(parameters.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
      return parameters.get(name);
    }
  }
}
