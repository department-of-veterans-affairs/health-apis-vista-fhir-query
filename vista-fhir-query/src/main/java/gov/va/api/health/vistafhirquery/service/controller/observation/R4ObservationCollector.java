package gov.va.api.health.vistafhirquery.service.controller.observation;

import static gov.va.api.health.vistafhirquery.service.controller.observation.VitalVuidMapper.forLoinc;
import static java.util.stream.Collectors.toMap;

import gov.va.api.health.r4.api.resources.Observation;
import gov.va.api.lighthouse.charon.models.vprgetpatientdata.Labs;
import gov.va.api.lighthouse.charon.models.vprgetpatientdata.Vitals;
import gov.va.api.lighthouse.charon.models.vprgetpatientdata.VprGetPatientData;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class R4ObservationCollector {
  private final String patientIcn;

  private final VitalVuidMapper vitalVuidMapper;

  private final String codes;

  @NonNull private final Map.Entry<String, VprGetPatientData.Response.Results> resultsEntry;

  private AllowedObservationCodes allowedCodes() {
    if (codes() == null) {
      return AllowedObservationCodes.allowAll();
    }
    List<String> loincCodes = Arrays.asList(codes().split(",", -1));
    // Allowed Vital Codes
    Map<String, String> allowedCodes =
        loincCodes.stream()
            .flatMap(code -> vitalVuidMapper().mappings().stream().filter(forLoinc(code)))
            .collect(
                toMap(
                    VitalVuidMapper.VitalVuidMapping::vuid,
                    VitalVuidMapper.VitalVuidMapping::code));
    /* The assumption here is that the loinc values that didn't map to a vital vuid _should_ be
     * supported loinc codes irregardless. For example, the Lab loinc code 1-8 would not have
     * a mapping in the vital table, but should be included in the map of allowed
     * Observation codes. */
    // Allowed Lab Codes
    loincCodes.stream()
        .filter(code -> !allowedCodes.containsValue(code))
        .forEach(code -> allowedCodes.put(code, code));
    return AllowedObservationCodes.allowOnly(allowedCodes);
  }

  Stream<Observation> toFhir() {
    Stream<Observation> vitals =
        resultsEntry
            .getValue()
            .vitalStream()
            .filter(Vitals.Vital::isNotEmpty)
            .flatMap(
                vital ->
                    VistaVitalToR4ObservationTransformer.builder()
                        .patientIcn(patientIcn)
                        .vistaSiteId(resultsEntry.getKey())
                        .vuidMapper(vitalVuidMapper)
                        .vistaVital(vital)
                        .conditions(allowedCodes())
                        .build()
                        .conditionallyToFhir());
    Stream<Observation> labs =
        resultsEntry
            .getValue()
            .labStream()
            .filter(Labs.Lab::isNotEmpty)
            .flatMap(
                lab ->
                    VistaLabToR4ObservationTransformer.builder()
                        .patientIcn(patientIcn)
                        .vistaSiteId(resultsEntry.getKey())
                        .vistaLab(lab)
                        .conditions(allowedCodes())
                        .build()
                        .conditionallyToFhir());

    return Stream.concat(vitals, labs);
  }
}
