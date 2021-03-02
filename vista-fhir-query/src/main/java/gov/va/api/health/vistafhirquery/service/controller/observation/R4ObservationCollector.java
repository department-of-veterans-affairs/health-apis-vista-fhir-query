package gov.va.api.health.vistafhirquery.service.controller.observation;

import static gov.va.api.health.vistafhirquery.service.controller.observation.VitalVuidMapper.forLoinc;
import static java.util.stream.Collectors.toMap;

import gov.va.api.health.r4.api.resources.Observation;
import gov.va.api.lighthouse.vistalink.models.vprgetpatientdata.Labs;
import gov.va.api.lighthouse.vistalink.models.vprgetpatientdata.Vitals;
import gov.va.api.lighthouse.vistalink.models.vprgetpatientdata.VprGetPatientData;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Value
@Slf4j
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
    Map<String, String> vuidToLoinc =
        Arrays.stream(codes().split(",", -1))
            .flatMap(code -> vitalVuidMapper().mappings().stream().filter(forLoinc(code)))
            .collect(
                toMap(
                    VitalVuidMapper.VitalVuidMapping::vuid,
                    VitalVuidMapper.VitalVuidMapping::code));
    return AllowedObservationCodes.allowOnly(vuidToLoinc);
  }

  Stream<Observation> toFhir() {
    log.info("ToDo: Parallelize this.");
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
