package gov.va.api.health.vistafhirquery.service.controller.observation;

import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import gov.va.api.lighthouse.charon.models.vprgetpatientdata.Vitals;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class MethodMapping {

  /** Converts qualifier vuids into codings for the Method CodeableConcept. */
  public static Coding toCoding(VistaQualifiers qualifier) {
    if (qualifier == null) {
      return null;
    }
    var coding = Coding.builder().system("http://snomed.info/sct");
    switch (qualifier) {
      case ACTUAL:
        return coding.code("258104002").display("Measured (qualifier value)").build();
      case AFTER_EXERCISE:
        return coding.code("255214003").display("After exercise (qualifier value)").build();
      case AT_REST:
        return coding.code("263678003").display("At rest (qualifier value)").build();
      case AUSCULTATE:
        return coding.code("37931006").display("Auscultation (procedure)").build();
      case CALCULATED:
        return coding.code("258090004").display("Calculated (qualifier value)").build();
      case DRY:
        return coding.code("445541000").display("Dry body weight (observable entity)").build();
      case ESTIMATED:
        // fallthrough
      case ESTIMATED_BY_ARM_SPAN:
        return coding.code("414135002").display("Estimated (qualifier value)").build();
      case INVASIVE:
        return coding
            .code("386341005")
            .display("Invasive hemodynamic monitoring (regime/therapy)")
            .build();
      case NON_INVASIVE:
        return coding
            .code("704042003")
            .display("Non-invasive cardiac output monitoring (regime/therapy)")
            .build();
      case PALPATED:
        return coding.code("113011001").display("Palpation (procedure)").build();
      case ROOM_AIR:
        return coding.code("15158005").display("Air (substance)").build();
      case SPONTANEOUS:
        return coding.code("241700002").display("Spontaneous respiration (finding)").build();
      case STATED:
        return coding
            .code("418799008")
            .display("Finding reported by subject or history provider (finding)")
            .build();
      case TRANSTRACHEAL:
        return coding
            .code("426129001")
            .display("Transtracheal oxygen catheter (physical object)")
            .build();
      case WITH_ACTIVITY:
        return coding.code("309604004").display("During exercise (qualifier value)").build();
      case WITH_AMBULATION:
        return coding.code("129006008").display("Walking (observable entity)").build();
      case WITH_CAST_OR_BRACE:
        // fallthrough
      case WITH_PROSTHESIS:
        return coding.code("303474004").display("Does not remove prosthesis (finding)").build();
      case WITHOUT_PROSTHESIS:
        return coding.code("303473005").display("Does remove prosthesis (finding)").build();
      default:
        throw new IllegalStateException("Unknown Vista Qualifier : " + qualifier);
    }
  }

  /** Iterates over the qualifiers to create the coding list for the Method CodeableConcept. */
  public static CodeableConcept toMethod(List<Vitals.Qualifier> qualifiers) {
    List<Coding> coding =
        qualifiers.stream()
            .filter(Objects::nonNull)
            .map(maybeQualifier -> VistaQualifiers.findByVuid(maybeQualifier.vuid()))
            .filter(Optional::isPresent)
            .map(qualifier -> toCoding(qualifier.get()))
            .collect(Collectors.toList());
    if (coding.isEmpty()) {
      return null;
    }
    return CodeableConcept.builder().coding(coding).build();
  }

  @AllArgsConstructor
  enum VistaQualifiers {
    ACTUAL("4711345"),
    AFTER_EXERCISE("4711309"),
    AT_REST("4711313"),
    AUSCULTATE("4711314"),
    CALCULATED("4712397"),
    DRY("4711346"),
    ESTIMATED("4711347"),
    ESTIMATED_BY_ARM_SPAN("4711348"),
    INVASIVE("4711325"),
    NON_INVASIVE("4711335"),
    PALPATED("4711337"),
    ROOM_AIR("4711353"),
    SPONTANEOUS("4711360"),
    STATED("4711363"),
    TRANSTRACHEAL("4711368"),
    WITH_ACTIVITY("4710817"),
    WITH_AMBULATION("4710818"),
    WITH_CAST_OR_BRACE("4710819"),
    WITH_PROSTHESIS("4710820"),
    WITHOUT_PROSTHESIS("4710821");

    @Getter private final String vuid;

    public static Optional<VistaQualifiers> findByVuid(String vuid) {
      for (VistaQualifiers qualifier : VistaQualifiers.values()) {
        if (qualifier.vuid().equals(vuid)) {
          return Optional.of(qualifier);
        }
      }
      return Optional.empty();
    }
  }
}
