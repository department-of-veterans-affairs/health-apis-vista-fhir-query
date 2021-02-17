package gov.va.api.health.vistafhirquery.service.controller.observation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class VitalVuidMapperTest {
  private static final VitalVuidMappingRepository repository =
      mock(VitalVuidMappingRepository.class);

  @BeforeAll
  static void _init() {
    when(repository.findByCodingSystemId(eq((short) 11))).thenReturn(entities());
  }

  private static List<VitalVuidMappingEntity> entities() {
    return List.of(
        VitalVuidMappingEntity.builder()
            .sourceValue("1")
            .code("l1")
            .display("Loinc 1")
            .uri("http://loinc.org")
            .codingSystemId((short) 11)
            .build(),
        VitalVuidMappingEntity.builder()
            .sourceValue("2")
            .code("l2")
            .display("Loinc 2")
            .uri("http://loinc.org")
            .codingSystemId((short) 11)
            .build(),
        VitalVuidMappingEntity.builder()
            .sourceValue("1")
            .code("s1")
            .display("Snomed 1")
            .uri("http://snomed.org")
            .codingSystemId((short) 11)
            .build(),
        VitalVuidMappingEntity.builder()
            .sourceValue("2")
            .code("s2")
            .display("Snomed 2")
            .uri("http://snomed.org")
            .codingSystemId((short) 11)
            .build());
  }

  static Stream<Arguments> forSystem() {
    return Stream.of(
        Arguments.of("http://nope.org", List.of()),
        Arguments.of("http://loinc.org", List.of(loincMapping(1), loincMapping(2))),
        Arguments.of("http://snomed.org", List.of(snomedMapping(1), snomedMapping(2))));
  }

  static Stream<Arguments> forVuid() {
    return Stream.of(
        Arguments.of("0", List.of()),
        Arguments.of("1", List.of(loincMapping(1), snomedMapping(1))),
        Arguments.of("2", List.of(loincMapping(2), snomedMapping(2))));
  }

  private static VitalVuidMapper.VitalVuidMapping loincMapping(Integer value) {
    return VitalVuidMapper.VitalVuidMapping.builder()
        .vuid("" + value)
        .code("l" + value)
        .display("Loinc " + value)
        .system("http://loinc.org")
        .build();
  }

  private static VitalVuidMapper.VitalVuidMapping snomedMapping(Integer value) {
    return VitalVuidMapper.VitalVuidMapping.builder()
        .vuid("" + value)
        .code("s" + value)
        .display("Snomed " + value)
        .system("http://snomed.org")
        .build();
  }

  @Test
  void asCodeableConcept() {
    assertThat(
            mapper().mappings().stream()
                .filter(VitalVuidMapper.forSystem("http://loinc.org"))
                .filter(VitalVuidMapper.forVuid("1"))
                .map(VitalVuidMapper.asCodeableConcept())
                .collect(Collectors.toList()))
        .containsExactly(
            CodeableConcept.builder()
                .coding(
                    List.of(
                        Coding.builder()
                            .system("http://loinc.org")
                            .code("l1")
                            .display("Loinc 1")
                            .build()))
                .build());
  }

  @ParameterizedTest
  @MethodSource
  void forSystem(String system, List<VitalVuidMapper.VitalVuidMapping> expected) {
    assertThat(
            mapper().mappings().stream()
                .filter(VitalVuidMapper.forSystem(system))
                .collect(Collectors.toList()))
        .isEqualTo(expected);
  }

  @ParameterizedTest
  @MethodSource
  void forVuid(String vuid, List<VitalVuidMapper.VitalVuidMapping> expected) {
    assertThat(
            mapper().mappings().stream()
                .filter(VitalVuidMapper.forVuid(vuid))
                .collect(Collectors.toList()))
        .isEqualTo(expected);
  }

  private VitalVuidMapper mapper() {
    return new VitalVuidMapper(repository);
  }

  @Test
  void mappings() {
    assertThat(mapper().mappings())
        .containsExactlyInAnyOrder(
            loincMapping(1), loincMapping(2), snomedMapping(1), snomedMapping(2));
  }
}
