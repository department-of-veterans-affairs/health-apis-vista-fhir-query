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
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

/** Filter for supporting alternate ids. */
@AllArgsConstructor(staticName = "of")
public class AlternatePatientIdFilter extends OncePerRequestFilter {

  private final AlternatePatientIds alternatePatientIds;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    Map<String, String[]> newParameters = null;

    for (String parameter : alternatePatientIds.patientIdParameters()) {
      var publicId = request.getParameter(parameter);
      if (publicId == null) {
        continue;
      }
      var privateId = alternatePatientIds.toPrivateId(publicId);
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

  static class OverrideParametersHttpServletRequestWrapper extends HttpServletRequestWrapper {

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
