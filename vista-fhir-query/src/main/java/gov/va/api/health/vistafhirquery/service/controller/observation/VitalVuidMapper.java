package gov.va.api.health.vistafhirquery.service.controller.observation;

import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Data
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class VitalVuidMapper {
  private final VitalVuidMappingRepository repository;

  /** Map a VistaVitalMapping to an R4 CodeableConcept. */
  public static Function<VitalVuidMapping, CodeableConcept> asCodeableConcept() {
    return m -> {
      if (m == null || m.code() == null) {
        return null;
      }
      return CodeableConcept.builder()
          .coding(
              List.of(
                  Coding.builder().system(m.system()).code(m.code()).display(m.display()).build()))
          .build();
    };
  }

  public static Predicate<VitalVuidMapping> forSystem(@NonNull String systemUri) {
    return m -> systemUri.equals(m.system());
  }

  public static Predicate<VitalVuidMapping> forVuid(@NonNull String vuid) {
    return m -> vuid.equals(m.vuid());
  }

  /**
   * Retrieve Vital VUID mappings from a database, map them to a reusable format, and cache them.
   */
  // TODO renable @Cacheable("vitalVuidMapping")
  public List<VitalVuidMapping> mappings() {
    List<VitalVuidMappingEntity> vitalVuidEntities =
        repository.findByCodingSystemId(Short.valueOf("11"));
    return vitalVuidEntities.stream()
        .filter(Objects::nonNull)
        .map(
            e ->
                VitalVuidMapping.builder()
                    .vuid(e.sourceValue())
                    .code(e.code())
                    .system(e.uri())
                    .display(e.display())
                    .build())
        .collect(Collectors.toList());
  }

  @Data
  @Builder
  public static class VitalVuidMapping {
    String vuid;

    String system;

    String code;

    String display;
  }
}
