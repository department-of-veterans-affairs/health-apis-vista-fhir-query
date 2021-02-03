package gov.va.api.health.vistafhirquery.service.controller.observation;

import gov.va.api.health.r4.api.resources.Observation;
import gov.va.api.lighthouse.vistalink.models.vprgetpatientdata.VprGetPatientData;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.NonNull;

@Builder
public class R4ObservationTransformer {
  @NonNull private final VprGetPatientData.Response.Results results;

  List<Observation> toFhir() {
    if (results.vitals() == null
        || results.vitals().vitalResults() == null
        || results.vitals().vitalResults().isEmpty()) {
      return null;
    }
    return results.vitals().vitalResults().stream()
        .flatMap(
            v ->
                VistaVitalToR4ObservationTransformer.builder()
                    .vistaVital(v)
                    .build()
                    .toFhir()
                    .stream())
        .collect(Collectors.toList());
  }
}
