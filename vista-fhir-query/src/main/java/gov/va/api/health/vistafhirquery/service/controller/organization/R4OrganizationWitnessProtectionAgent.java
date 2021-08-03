package gov.va.api.health.vistafhirquery.service.controller.organization;

import gov.va.api.health.r4.api.resources.Organization;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.ProtectedReference;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.ProtectedReferenceFactory;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.WitnessProtectionAgent;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Creates protected references for the Organization resource. */
@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @NonNull})
public class R4OrganizationWitnessProtectionAgent implements WitnessProtectionAgent<Organization> {
  private final ProtectedReferenceFactory protectedReferenceFactory;

  @Override
  public Stream<ProtectedReference> referencesOf(Organization resource) {
    return Stream.of(protectedReferenceFactory.forResource(resource, resource::id));
  }
}
