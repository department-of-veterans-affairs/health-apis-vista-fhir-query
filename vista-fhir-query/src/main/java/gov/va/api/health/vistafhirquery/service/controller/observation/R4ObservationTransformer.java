package gov.va.api.health.vistafhirquery.service.controller.observation;

import gov.va.api.health.r4.api.resources.Observation;
import gov.va.api.lighthouse.vistalink.models.vprgetpatientdata.VprGetPatientData;
import java.util.Map;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.NonNull;

@Builder
public class R4ObservationTransformer {
  @NonNull private final Map.Entry<String, VprGetPatientData.Response.Results> resultsEntry;

  Stream<Observation> toFhir() {
    return resultsEntry
        .getValue()
        .vitalStream()
        .flatMap(
            v -> VistaVitalToR4ObservationTransformer.builder().vistaVital(v).build().toFhir());
  }
}
