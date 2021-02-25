package gov.va.api.health.vistafhirquery.service.controller.observation;

import static gov.va.api.health.vistafhirquery.service.controller.observation.VitalVuidMapper.forLoinc;

import gov.va.api.health.r4.api.resources.Observation;
import gov.va.api.lighthouse.vistalink.models.vprgetpatientdata.Labs;
import gov.va.api.lighthouse.vistalink.models.vprgetpatientdata.Vitals;
import gov.va.api.lighthouse.vistalink.models.vprgetpatientdata.VprGetPatientData;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
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

  private List<String> allowedVuids() {
    if (codes() == null) {
      return List.of();
    }
    return Arrays.stream(codes().split(",", -1))
        .flatMap(
            code -> {
              var mappings =
                  vitalVuidMapper().mappings().stream()
                      .filter(forLoinc(code))
                      .collect(Collectors.toList());
              if (mappings.isEmpty()) {
                return Stream.of(VitalVuidMapper.VitalVuidMapping.builder().vuid(code).build());
              }
              return mappings.stream();
            })
        .filter(Objects::nonNull)
        .map(VitalVuidMapper.VitalVuidMapping::vuid)
        .collect(Collectors.toList());
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
                        .conditions(ObservationConditions.of(allowedVuids()))
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
                        .conditions(ObservationConditions.of(allowedVuids()))
                        .build()
                        .conditionallyToFhir());

    return Stream.concat(vitals, labs);
  }
}
