package gov.va.api.health.vistafhirquery.service.controller.coverage;

import gov.va.api.health.r4.api.resources.Coverage;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.ProtectedReference;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.ProtectedReferenceFactory;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.WitnessProtectionAgent;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Creates protected references for the Coverage resource. */
@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @NonNull})
public class R4CoverageWitnessProtectionAgent implements WitnessProtectionAgent<Coverage> {
  private final ProtectedReferenceFactory protectedReferenceFactory;

  @Override
  public Stream<ProtectedReference> referencesOf(Coverage resource) {
    Stream<ProtectedReference> referenceGroups =
        Stream.concat(
            resource.payor().stream()
                .map(p -> protectedReferenceFactory.forReference(p).orElse(null))
                .filter(Objects::nonNull),
            resource.coverageClass().stream()
                .map(
                    c -> {
                      int indexOfId = c.value().lastIndexOf('/') + 1;
                      if (c.value().length() <= indexOfId) {
                        throw new IllegalArgumentException("ID is malformed (appears to end with a /)");
                      }
                      return ProtectedReference.builder()
                          .type("InsurancePlan")
                          .id(c.value().substring(indexOfId))
                          .onUpdate(i -> c.value(c.value().substring(0, indexOfId) + i))
                          .build();
                    }));
    return Stream.concat(
        Stream.of(
            protectedReferenceFactory.forResource(resource, resource::id),
            protectedReferenceFactory.forReference(resource.beneficiary()).orElse(null)),
        referenceGroups);
  }
}
