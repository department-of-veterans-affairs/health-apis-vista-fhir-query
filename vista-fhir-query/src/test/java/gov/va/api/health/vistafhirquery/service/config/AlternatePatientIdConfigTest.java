package gov.va.api.health.vistafhirquery.service.config;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.AlternatePatientIds;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class AlternatePatientIdConfigTest {

  @Test
  void alternatePatientIds() {
    assertThat(
            new AlternatePatientIdConfig()
                .alternatePatientIds(
                    AlternatePatientIdProperties.builder()
                        .enabled(true)
                        .id(Map.of("1011537977V693883", "5000000347"))
                        .parameters(List.of("patient"))
                        .build()))
        .isEqualTo(
            AlternatePatientIds.MappedAlternatePatientIds.builder()
                .publicToPrivateIds(Map.of("1011537977V693883", "5000000347"))
                .patientIdParameters(List.of("patient"))
                .build());
    assertThat(
            new AlternatePatientIdConfig()
                .alternatePatientIds(AlternatePatientIdProperties.builder().enabled(false).build()))
        .isExactlyInstanceOf(AlternatePatientIds.DisabledAlternatePatientIds.class);
  }
}
