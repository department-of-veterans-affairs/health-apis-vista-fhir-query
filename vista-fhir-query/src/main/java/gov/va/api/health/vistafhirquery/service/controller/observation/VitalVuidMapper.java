package gov.va.api.health.vistafhirquery.service.controller.observation;

import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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

  /** Get a stream of VitalVuidMappings using the cached repository method. */
  public VitalVuidMappingStream mappings() {
    return VitalVuidMappingStream.of(
        vuidMappings().stream()
            .filter(Objects::nonNull)
            .map(
                e ->
                    VitalVuidMapping.builder()
                        .vuid(e.sourceValue())
                        .code(e.code())
                        .system(e.uri())
                        .display(e.display())
                        .build()));
  }

  @Cacheable("vitalVuidMapping")
  public List<VitalVuidMappingEntity> vuidMappings() {
    return repository.findByCodingSystemId(Short.valueOf("11"));
  }

  @Data
  @Builder
  public static class VitalVuidMapping {
    String vuid;

    String system;

    String code;

    String display;
  }

  @AllArgsConstructor(staticName = "of")
  public static class VitalVuidMappingStream {
    @Delegate Stream<VitalVuidMapping> mappings;

    public VitalVuidMappingStream and(Predicate<VitalVuidMapping> condition) {
      mappings = mappings.filter(condition);
      return this;
    }

    public VitalVuidMappingStream lookup(Predicate<VitalVuidMapping> condition) {
      mappings = mappings.filter(condition);
      return this;
    }
  }
}
