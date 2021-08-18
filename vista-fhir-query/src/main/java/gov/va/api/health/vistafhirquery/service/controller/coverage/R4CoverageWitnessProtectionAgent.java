package gov.va.api.health.vistafhirquery.service.controller.coverage;

import gov.va.api.health.r4.api.resources.Coverage;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.ProtectedReference;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.ProtectedReferenceFactory;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.WitnessProtectionAgent;
import java.util.Arrays;
import java.util.List;
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

  /** Get id from a string, removing the prefix. */
  public String getId(String id) {
    if (!id.contains("/")) {
      return id;
    }
    List<String> strings = Arrays.asList(id.split("/"));
    return strings.get(strings.size() - 1);
  }

  /** Get prefix of a string, if it has one. */
  public String getPrefix(String id) {
    if (!id.contains("/")) {
      return "";
    }
    List<String> strings = Arrays.asList(id.split("/"));
    StringBuilder prefix = new StringBuilder("/");
    for (int i = 0; i < strings.size() - 2; i++) {
      prefix.append(strings.get(i)).append("/");
    }
    return prefix.toString();
  }

  @Override
  public Stream<ProtectedReference> referencesOf(Coverage resource) {
    Stream<ProtectedReference> referenceGroups =
        Stream.concat(
            resource.payor().stream()
                .map(p -> protectedReferenceFactory.forReference(p).orElse(null))
                .filter(Objects::nonNull),
            resource.coverageClass().stream()
                .map(
                    c ->
                        ProtectedReference.builder()
                            .type("InsurancePlan")
                            .id(getId(c.value()))
                            .onUpdate(i -> c.value(getPrefix(c.value()) + i))
                            .build()));
    return Stream.concat(
        Stream.of(
            protectedReferenceFactory.forResource(resource, resource::id),
            protectedReferenceFactory.forReference(resource.beneficiary()).orElse(null)),
        referenceGroups);
  }
}
