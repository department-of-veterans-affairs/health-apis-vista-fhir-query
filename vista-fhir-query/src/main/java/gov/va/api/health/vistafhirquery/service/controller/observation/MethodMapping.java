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

/** Maps vista qualifiers to Observation methods. */
public class MethodMapping {

  /** Converts qualifier vuids into codings for the Method CodeableConcept. */
  public static Coding toCoding(VistaQualifiers qualifier) {
    if (qualifier == null) {
      return null;
    }
    return Coding.builder()
        .system("http://snomed.info/sct")
        .display(qualifier.display())
        .code(qualifier.snomed())
        .build();
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
    ACTUAL("4711345", "258104002", "Measured (qualifier value)"),
    AFTER_EXERCISE("4711309", "255214003", "After exercise (qualifier value)"),
    AT_REST("4711313", "263678003", "At rest (qualifier value)"),
    AUSCULTATE("4711314", "37931006", "Auscultation (procedure)"),
    CALCULATED("4712397", "258090004", "Calculated (qualifier value)"),
    DRY("4711346", "445541000", "Dry body weight (observable entity)"),
    ESTIMATED("4711347", "414135002", "Estimated (qualifier value)"),
    ESTIMATED_BY_ARM_SPAN("4711348", "414135002", "Estimated (qualifier value)"),
    INVASIVE("4711325", "386341005", "Invasive hemodynamic monitoring (regime/therapy)"),
    NON_INVASIVE("4711335", "704042003", "Non-invasive cardiac output monitoring (regime/therapy)"),
    PALPATED("4711337", "113011001", "Palpation (procedure)"),
    ROOM_AIR("4711353", "15158005", "Air (substance)"),
    SPONTANEOUS("4711360", "241700002", "Spontaneous respiration (finding)"),
    STATED("4711363", "418799008", "Finding reported by subject or history provider (finding)"),
    TRANSTRACHEAL("4711368", "426129001", "Transtracheal oxygen catheter (physical object)"),
    WITH_ACTIVITY("4710817", "309604004", "During exercise (qualifier value)"),
    WITH_AMBULATION("4710818", "129006008", "Walking (observable entity)"),
    WITH_CAST_OR_BRACE("4710819", "303474004", "Does not remove prosthesis (finding)"),
    WITH_PROSTHESIS("4710820", "303474004", "Does not remove prosthesis (finding)"),
    WITHOUT_PROSTHESIS("4710821", "303473005", "Does remove prosthesis (finding)");

    @Getter private final String vuid;
    @Getter private final String snomed;
    @Getter private final String display;

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
