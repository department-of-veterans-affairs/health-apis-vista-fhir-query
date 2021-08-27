package gov.va.api.health.vistafhirquery.service.controller.endpoint;

import gov.va.api.health.r4.api.bundle.AbstractEntry;
import gov.va.api.health.r4.api.resources.Endpoint;
import gov.va.api.lighthouse.talos.ResponseIncludesIcnHeaderAdvice;
import java.util.stream.Stream;
import lombok.experimental.Delegate;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class R4EndpointResponseIncludesIcnHeaderAdvice implements ResponseBodyAdvice<Object> {

  @Delegate private final ResponseBodyAdvice<Object> delegate;

  R4EndpointResponseIncludesIcnHeaderAdvice() {
    delegate =
        ResponseIncludesIcnHeaderAdvice.<Endpoint, Endpoint.Bundle>builder()
            .type(Endpoint.class)
            .bundleType(Endpoint.Bundle.class)
            .extractResources(bundle -> bundle.entry().stream().map(AbstractEntry::resource))
            .extractIcns(body -> Stream.empty())
            .build();
  }
}
