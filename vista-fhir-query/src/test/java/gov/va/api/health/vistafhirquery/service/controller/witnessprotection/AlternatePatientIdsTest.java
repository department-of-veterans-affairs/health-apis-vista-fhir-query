package gov.va.api.health.vistafhirquery.service.controller.witnessprotection;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.AlternatePatientIds.DisabledAlternatePatientIds;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.AlternatePatientIds.MappedAlternatePatientIds;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class AlternatePatientIdsTest {

  @Test
  void disableMapsNothing() {
    var f = new DisabledAlternatePatientIds();
    assertThat(f.patientIdParameters()).isEmpty();
    assertThat(f.toPrivateId("x")).isEqualTo("x");
    assertThat(f.toPublicId("x")).isEqualTo("x");
  }

  @Test
  void mappedLookupReturnsGivenValuesWhenAvailable() {
    var f =
        MappedAlternatePatientIds.builder()
            .patientIdParameters(List.of("a", "b"))
            .publicToPrivateIds(Map.of("111", "aaa", "222", "bbb"))
            .build();

    assertThat(f.toPrivateId("111")).isEqualTo("aaa");
    assertThat(f.toPublicId("aaa")).isEqualTo("111");
  }

  @Test
  void mappedLookupReturnsMappedValuesWhenAvailable() {
    var f =
        MappedAlternatePatientIds.builder()
            .patientIdParameters(List.of("a", "b"))
            .publicToPrivateIds(Map.of("111", "aaa", "222", "bbb"))
            .build();

    assertThat(f.toPrivateId("not-mapped")).isEqualTo("not-mapped");
    assertThat(f.toPublicId("not-mapped")).isEqualTo("not-mapped");
  }

  @Test
  void mappedRemembersParameters() {
    var f =
        MappedAlternatePatientIds.builder()
            .patientIdParameters(List.of("a", "b"))
            .publicToPrivateIds(Map.of("111", "aaa", "222", "bbb"))
            .build();
    assertThat(f.patientIdParameters()).containsExactlyInAnyOrder("a", "b");
    assertThat(f.isPatientIdParameter("a")).isTrue();
    assertThat(f.isPatientIdParameter("b")).isTrue();
    assertThat(f.isPatientIdParameter("x")).isFalse();
  }
}
