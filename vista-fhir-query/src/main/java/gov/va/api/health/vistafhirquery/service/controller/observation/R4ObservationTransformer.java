package gov.va.api.health.vistafhirquery.service.controller.observation;

import gov.va.api.health.r4.api.resources.Observation;
import gov.va.api.lighthouse.vistalink.models.vprgetpatientdata.VprGetPatientData;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.NonNull;

@Builder
public class R4ObservationTransformer {
  @NonNull private final Map.Entry<String, VprGetPatientData.Response.Results> resultsEntry;

  List<Observation> toFhir() {
    return resultsEntry
        .getValue()
        .vitalStream()
        .flatMap(v -> VistaVitalToR4ObservationTransformer.builder().vistaVital(v).build().toFhir())
        .collect(Collectors.toList());
  }
}
