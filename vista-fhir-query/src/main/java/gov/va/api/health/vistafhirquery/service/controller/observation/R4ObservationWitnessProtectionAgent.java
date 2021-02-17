package gov.va.api.health.vistafhirquery.service.controller.observation;

import gov.va.api.health.r4.api.resources.Observation;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.ProtectedReference;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.WitnessProtectionAgent;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

@Component
public class R4ObservationWitnessProtectionAgent implements WitnessProtectionAgent<Observation> {

  @Override
  public Stream<ProtectedReference> referencesOf(Observation resource) {
    return Stream.of(
        ProtectedReference.forResource(resource, resource::id),
        ProtectedReference.forReference(resource.subject()).orElse(null));
  }
}
