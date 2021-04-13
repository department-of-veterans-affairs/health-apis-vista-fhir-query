package gov.va.api.health.vistafhirquery.service.controller.observation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import gov.va.api.lighthouse.charon.models.vprgetpatientdata.Vitals;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class MethodMappingTest {
  static Stream<Arguments> toMethod() {
    return Stream.of(
        arguments("4711345", "258104002"),
        arguments("4711309", "255214003"),
        arguments("4711313", "263678003"),
        arguments("4711314", "37931006"),
        arguments("4712397", "258090004"),
        arguments("4711346", "445541000"),
        arguments("4711347", "414135002"),
        arguments("4711348", "414135002"),
        arguments("4711325", "386341005"),
        arguments("4711335", "704042003"),
        arguments("4711337", "113011001"),
        arguments("4711353", "15158005"),
        arguments("4711360", "241700002"),
        arguments("4711363", "418799008"),
        arguments("4711368", "426129001"),
        arguments("4710817", "309604004"),
        arguments("4710818", "129006008"),
        arguments("4710819", "303474004"),
        arguments("4710820", "303474004"),
        arguments("4710821", "303473005"));
  }

  @ParameterizedTest
  @MethodSource
  void toMethod(String vuid, String code) {
    assertThat(
            MethodMapping.toMethod(List.of(Vitals.Qualifier.builder().vuid(vuid).build()))
                .coding()
                .get(0)
                .code())
        .isEqualTo(code);
  }
}
