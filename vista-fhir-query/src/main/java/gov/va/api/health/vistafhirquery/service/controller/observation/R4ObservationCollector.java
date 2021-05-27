package gov.va.api.health.vistafhirquery.service.controller.observation;

import static gov.va.api.health.vistafhirquery.service.controller.observation.VitalVuidMapper.forLoinc;
import static java.util.stream.Collectors.toList;

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

/** Collects Observations, Vitals before Labs. */
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
    List<String> vuidCodes =
        loincCodes.stream()
            .flatMap(code -> vitalVuidMapper().mappings().stream().filter(forLoinc(code)))
            .map(VitalVuidMapper.VitalVuidMapping::vuid)
            .collect(toList());
    return AllowedObservationCodes.allowOnly(vuidCodes, loincCodes);
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
