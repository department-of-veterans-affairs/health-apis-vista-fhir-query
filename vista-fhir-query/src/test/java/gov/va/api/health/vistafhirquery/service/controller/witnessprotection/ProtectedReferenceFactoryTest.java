package gov.va.api.health.vistafhirquery.service.controller.witnessprotection;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.r4.api.elements.Reference;
import gov.va.api.health.r4.api.resources.Patient;
import gov.va.api.health.r4.api.resources.Resource;
import gov.va.api.health.vistafhirquery.service.config.LinkProperties;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ProtectedReferenceFactoryTest {

  @Getter
  private final ProtectedReferenceFactory prf =
      new ProtectedReferenceFactory(
          LinkProperties.builder()
              .publicUrl("http://awesome.com/fuego")
              .publicR4BasePath("r4")
              .defaultPageSize(99)
              .maxPageSize(99)
              .build());

  @Test
  void forReference() {
    assertThat(prf().forReference((Reference) null)).isEmpty();
    Reference ref = Reference.builder().reference("Patient/p1").build();
    Optional<ProtectedReference> actual = prf().forReference(ref);
    assertThat(actual).isNotEmpty();
    assertThat(actual.get().type()).isEqualTo("Patient");
    assertThat(actual.get().id()).isEqualTo("p1");
    actual.get().onUpdate().accept("newton");
    assertThat(ref.reference()).isEqualTo("http://awesome.com/fuego/r4/Patient/newton");
  }

  @Test
  void forResource() {
    var updatedValue = new AtomicReference<String>("not-updated");
    Resource resource = Patient.builder().id("p1").build();
    ProtectedReference actual = prf().forResource(resource, updatedValue::set);
    assertThat(actual.type()).isEqualTo("Patient");
    assertThat(actual.id()).isEqualTo("p1");
    actual.onUpdate().accept("newton");
    assertThat(updatedValue.get()).isEqualTo("newton");
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"Patient/p1", "/foo/whatever/Patient/p1", "https://whatever.com/Patient/p1"})
  void forUrisThatAreUnderstoodReferenceForTypeAndIdAreReturned(String uri) {
    var updatedValue = new AtomicReference<String>("not-updated");
    Optional<ProtectedReference> result =
        prf().forUri(uri, updatedValue::set, prf().replaceIdOnly());
    assertThat(result).isNotEmpty();
    assertThat(result.get().type()).isEqualTo("Patient");
    assertThat(result.get().id()).isEqualTo("p1");
    result.get().onUpdate().accept("newton");
    assertThat(updatedValue.get()).isEqualTo(uri.replace("p1", "newton"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "Patient", "/Patient", "Patient/"})
  void forUrisThatCannotBeUnderstoodEmptyIsReturned(String uri) {
    assertThat(prf().forUri(uri, s -> {}, prf().replaceIdOnly())).isEmpty();
  }
}
