package gov.va.api.health.vistafhirquery.service.controller.coverage;

import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.getReferenceId;

import gov.va.api.health.r4.api.bundle.AbstractEntry;
import gov.va.api.health.r4.api.resources.Coverage;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.AlternatePatientIds;
import gov.va.api.lighthouse.talos.ResponseIncludesIcnHeaderAdvice;
import lombok.experimental.Delegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Intercept all RequestMapping payloads of Type Coverage.class or Coverage.Bundle.class. Extract
 * ICN(s) from these payloads with the provided function. This will lead to populating the
 * X-VA-INCLUDES-ICN header.
 */
@ControllerAdvice
public class R4CoverageResponseIncludesIcnHeaderAdvice implements ResponseBodyAdvice<Object> {
  @Delegate private final ResponseBodyAdvice<Object> delegate;

  R4CoverageResponseIncludesIcnHeaderAdvice(@Autowired AlternatePatientIds alternatePatientIds) {
    delegate =
        ResponseIncludesIcnHeaderAdvice.<Coverage, Coverage.Bundle>builder()
            .type(Coverage.class)
            .bundleType(Coverage.Bundle.class)
            .extractResources(bundle -> bundle.entry().stream().map(AbstractEntry::resource))
            .extractIcns(
                resource ->
                    getReferenceId(resource.beneficiary())
                        .map(alternatePatientIds::toPublicId)
                        .stream())
            .build();
  }
}
