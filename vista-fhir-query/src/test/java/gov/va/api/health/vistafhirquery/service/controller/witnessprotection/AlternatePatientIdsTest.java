package gov.va.api.health.vistafhirquery.service.controller.witnessprotection;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.AlternatePatientIds.MappedAlternatePatientIds;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class AlternatePatientIdsTest {

  @Test
  void mappedLookup() {
    var f =
        MappedAlternatePatientIds.builder()
            .patientIdParameters(List.of("a", "b"))
            .publicToPrivateIds(Map.of("111", "aaa", "222", "bbb"))
            .build();

    assertThat(f.toPrivateId("111")).isEqualTo("aaa");
    assertThat(f.toPublicId("aaa")).isEqualTo("111");
  }
}
