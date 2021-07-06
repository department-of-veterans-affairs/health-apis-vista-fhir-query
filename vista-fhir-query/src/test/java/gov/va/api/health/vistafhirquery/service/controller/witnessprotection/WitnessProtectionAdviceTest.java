package gov.va.api.health.vistafhirquery.service.controller.witnessprotection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import gov.va.api.health.ids.api.IdentityService;
import gov.va.api.health.ids.api.Registration;
import gov.va.api.health.ids.api.ResourceIdentity;
import gov.va.api.health.r4.api.bundle.AbstractBundle;
import gov.va.api.health.r4.api.bundle.AbstractEntry;
import gov.va.api.health.r4.api.elements.Meta;
import gov.va.api.health.r4.api.resources.Resource;
import gov.va.api.health.vistafhirquery.service.config.LinkProperties;
import java.util.List;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WitnessProtectionAdviceTest {

  @Mock IdentityService identityService;

  @Test
  void bundleOfMixedResourcesIsProtected() {
    var f11 = FugaziOne.builder().id("private-f11").build();
    var f12 = FugaziOne.builder().id("private-f12").build();
    var f21 = FugaziTwo.builder().id("private-f21").build();
    var f22 = FugaziTwo.builder().id("private-f22").build();
    FugaziEntry ef11 = new FugaziEntry(f11);
    FugaziEntry ef12 = new FugaziEntry(f12);
    FugaziEntry ef21 = new FugaziEntry(f21);
    FugaziEntry ef22 = new FugaziEntry(f22);
    FugaziBundle bundle = new FugaziBundle(List.of(ef11, ef12, ef21, ef22));

    when(identityService.register(any()))
        .thenReturn(
            List.of(
                registration("FugaziOne", "f11"),
                registration("FugaziOne", "f12"),
                registration("FugaziTwo", "f21"),
                registration("FugaziTwo", "f22")));

    wp().protect(bundle);
    assertThat(f11.id()).isEqualTo("public-f11");
    assertThat(ef11.fullUrl()).isEqualTo("http://fugazi.com/fugazi/FugaziOne/public-f11");
    assertThat(f12.id()).isEqualTo("public-f12");
    assertThat(ef12.fullUrl()).isEqualTo("http://fugazi.com/fugazi/FugaziOne/public-f12");
    assertThat(f21.id()).isEqualTo("public-f21");
    assertThat(ef21.fullUrl()).isEqualTo("http://fugazi.com/fugazi/FugaziTwo/public-f21");
    assertThat(f22.id()).isEqualTo("public-f22");
    assertThat(ef22.fullUrl()).isEqualTo("http://fugazi.com/fugazi/FugaziTwo/public-f22");
  }

  private ResourceIdentity identity(String resource, String baseId) {
    return ResourceIdentity.builder()
        .system("VISTA")
        .resource(resource)
        .identifier("private-" + baseId)
        .build();
  }

  private LinkProperties linkProperties() {
    return LinkProperties.builder()
        .publicUrl("http://awesome.com/fuego")
        .publicR4BasePath("r4")
        .defaultPageSize(99)
        .maxPageSize(99)
        .build();
  }

  private Registration registration(String resource, String baseId) {
    return Registration.builder()
        .uuid("public-" + baseId)
        .resourceIdentities(List.of(identity(resource, baseId)))
        .build();
  }

  @Test
  void singleResourceIsProtected() {
    when(identityService.register(any())).thenReturn(List.of(registration("FugaziOne", "f1")));
    var f1 = FugaziOne.builder().id("private-f1").build();
    wp().protect(f1);
    assertThat(f1.id()).isEqualTo("public-f1");

    when(identityService.register(any())).thenReturn(List.of(registration("FugaziTwo", "f2")));
    var f2 = FugaziTwo.builder().id("private-f2").build();
    wp().protect(f2);
    assertThat(f2.id()).isEqualTo("public-f2");
  }

  @Test
  void toPrivateIdReturnsPrivateIdDuh() {
    when(identityService.lookup("public-f1")).thenReturn(List.of(identity("WHATEVER", "f1")));
    assertThat(wp().toPrivateId("public-f1")).isEqualTo("private-f1");
    assertThat(wp().toPrivateId("unknown-f1")).isEqualTo("unknown-f1");
  }

  @Test
  void unknownResourceTypeIsNotModified() {
    ProtectedReferenceFactory prf = new ProtectedReferenceFactory(linkProperties());
    var f1 = FugaziOne.builder().id("private-f1").build();
    var noF1 =
        WitnessProtectionAdvice.builder()
            .identityService(identityService)
            .availableAgents(List.of(FugaziTwoAgent.of(prf)))
            .alternatePatientIds(new AlternatePatientIds.DisabledAlternatePatientIds())
            .protectedReferenceFactory(prf)
            .build();
    noF1.protect(f1);
    assertThat(f1.id()).isEqualTo("private-f1");
  }

  @Test
  void unregisteredResourcesAreNotModified() {
    var f1 = FugaziOne.builder().id("private-f1").build();
    wp().protect(f1);
    assertThat(f1.id()).isEqualTo("private-f1");
  }

  private WitnessProtectionAdvice wp() {
    ProtectedReferenceFactory prf = new ProtectedReferenceFactory(linkProperties());
    return WitnessProtectionAdvice.builder()
        .protectedReferenceFactory(prf)
        .alternatePatientIds(new AlternatePatientIds.DisabledAlternatePatientIds())
        .identityService(identityService)
        .availableAgents(List.of(FugaziOneAgent.of(prf), FugaziTwoAgent.of(prf)))
        .build();
  }

  static class FugaziBundle extends AbstractBundle<FugaziEntry> {
    FugaziBundle(List<FugaziEntry> e) {
      entry(e);
    }
  }

  static class FugaziEntry extends AbstractEntry<Resource> {
    FugaziEntry(Resource r) {
      resource(r);
      fullUrl("http://fugazi.com/fugazi/" + r.getClass().getSimpleName() + "/" + r.id());
    }
  }

  @Data
  @Builder
  static class FugaziOne implements Resource {
    String id;
    String implicitRules;
    String language;
    Meta meta;
  }

  @AllArgsConstructor(staticName = "of")
  static class FugaziOneAgent implements WitnessProtectionAgent<FugaziOne> {

    ProtectedReferenceFactory prf;

    @Override
    public Stream<ProtectedReference> referencesOf(FugaziOne resource) {
      return Stream.of(prf.forResource(resource, resource::id));
    }
  }

  @Data
  @Builder
  static class FugaziTwo implements Resource {
    String id;
    String implicitRules;
    String language;
    Meta meta;
  }

  @AllArgsConstructor(staticName = "of")
  static class FugaziTwoAgent implements WitnessProtectionAgent<FugaziTwo> {

    ProtectedReferenceFactory prf;

    @Override
    public Stream<ProtectedReference> referencesOf(FugaziTwo resource) {
      return Stream.of(prf.forResource(resource, resource::id));
    }
  }
}
