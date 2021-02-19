package gov.va.api.health.vistafhirquery.service.controller.witnessprotection;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.FakeIds.MappedFakeIds;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class FakeIdsTest {

  @Test
  void mappedLookup() {
    var f =
        MappedFakeIds.builder()
            .patientIdParameters(List.of("a", "b"))
            .publicToPrivateIds(Map.of("111", "aaa", "222", "bbb"))
            .build();

    assertThat(f.toPrivateId("111")).isEqualTo("aaa");
    assertThat(f.toPublicId("aaa")).isEqualTo("111");
  }
}
