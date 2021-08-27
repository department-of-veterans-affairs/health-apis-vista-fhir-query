package gov.va.api.health.vistafhirquery.service.controller.observation;

import static gov.va.api.health.vistafhirquery.service.controller.observation.R4ObservationController.VISTA_INCLUDE_HEADER;

import gov.va.api.health.r4.api.resources.Observation;
import gov.va.api.health.vistafhirquery.service.config.LinkProperties;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(produces = {"application/json", "application/fhir+json"})
@AllArgsConstructor(onConstructor_ = {@Autowired, @NonNull})
@Builder
@Slf4j
public class R4ObservationControllerV1 {

  R4ObservationController hack;

  @SneakyThrows
  @RequestMapping("/site/{site}/r4/Observation")
  Observation.Bundle search(
      @PathVariable(name = "site") String site,
      @RequestParam(name = "category", required = false) String categoryCsv,
      @RequestParam(name = "code", required = false) String codeCsv,
      @RequestParam(name = "date", required = false) @Size(max = 2) String[] date,
      @RequestParam(name = "patient") String patient,
      HttpServletRequest request) {
    LinkProperties.HACK_PUBLIC_URL.set("site/" + site);
    return hack.searchByPatient(
        categoryCsv, codeCsv, date, patient, new OverrideHeadersHttpServletRequest(request, site));
  }

  private static class OverrideHeadersHttpServletRequest extends HttpServletRequestWrapper {

    private final String site;

    public OverrideHeadersHttpServletRequest(HttpServletRequest request, String site) {
      super(request);
      this.site = site;
    }

    @Override
    public String getHeader(String name) {
      if (name == VISTA_INCLUDE_HEADER) {
        return site;
      }
      return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
      Set<String> names = new HashSet<>(Collections.list(super.getHeaderNames()));
      names.add(VISTA_INCLUDE_HEADER);
      return Collections.enumeration(names);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
      if (name == VISTA_INCLUDE_HEADER) {
        return Collections.enumeration(List.of(site));
      }
      return super.getHeaders(name);
    }
  }
}
