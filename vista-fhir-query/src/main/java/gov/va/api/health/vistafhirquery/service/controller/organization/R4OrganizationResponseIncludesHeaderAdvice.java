package gov.va.api.health.vistafhirquery.service.controller.organization;

import gov.va.api.health.r4.api.bundle.AbstractEntry;
import gov.va.api.health.r4.api.resources.Organization;
import gov.va.api.lighthouse.talos.ResponseIncludesIcnHeaderAdvice;
import lombok.experimental.Delegate;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.stream.Stream;

@ControllerAdvice
public class R4OrganizationResponseIncludesHeaderAdvice implements ResponseBodyAdvice<Object> {

  @Delegate private final ResponseBodyAdvice<Object> delegate;

  R4OrganizationResponseIncludesHeaderAdvice() {
    delegate =
        ResponseIncludesIcnHeaderAdvice.<Organization, Organization.Bundle>builder()
            .type(Organization.class)
            .bundleType(Organization.Bundle.class)
            .extractResources(bundle -> bundle.entry().stream().map(AbstractEntry::resource))
            .extractIcns(body -> Stream.empty())
            .build();
  }
}
