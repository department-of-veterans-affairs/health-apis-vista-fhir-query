package gov.va.api.health.vistafhirquery.service.controller.observation;

import gov.va.api.health.r4.api.resources.Observation;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.ProtectedReference;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.ProtectedReferenceFactory;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.WitnessProtectionAgent;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Creates protected references for the Observation resource. */
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class R4ObservationWitnessProtectionAgent implements WitnessProtectionAgent<Observation> {

  @NonNull private final ProtectedReferenceFactory protectedReferenceFactory;

  @Override
  public Stream<ProtectedReference> referencesOf(Observation resource) {
    return Stream.of(
        protectedReferenceFactory.forResource(resource, resource::id),
        protectedReferenceFactory.forReference(resource.subject()).orElse(null));
  }
}
