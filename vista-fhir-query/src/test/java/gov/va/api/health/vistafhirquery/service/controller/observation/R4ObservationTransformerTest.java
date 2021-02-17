package gov.va.api.health.vistafhirquery.service.controller.observation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.lighthouse.vistalink.models.vprgetpatientdata.Labs;
import gov.va.api.lighthouse.vistalink.models.vprgetpatientdata.Vitals;
import gov.va.api.lighthouse.vistalink.models.vprgetpatientdata.VprGetPatientData;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class R4ObservationTransformerTest {

  private static VitalVuidMapper mapper;

  @BeforeAll
  static void _init() {
    VitalVuidMappingRepository repository = mock(VitalVuidMappingRepository.class);
    when(repository.findByCodingSystemId(eq((short) 11)))
        .thenReturn(ObservationVitalSamples.Datamart.create().mappingEntities());
    mapper = new VitalVuidMapper(repository);
  }

  @Test
  void combined() {
    var vprResp =
        VprGetPatientData.Response.Results.builder()
            .labs(Labs.builder().labResults(ObservationLabSamples.Vista.create().labs()).build())
            .vitals(
                Vitals.builder()
                    .vitalResults(ObservationVitalSamples.Vista.create().vitals())
                    .build())
            .build();
    var entry = Map.entry("673", vprResp);
    assertThat(
            R4ObservationTransformer.builder()
                .patientIcn("p1")
                .resultsEntry(entry)
                .vitalVuidMapper(mapper)
                .build()
                .toFhir())
        .containsExactly(
            ObservationVitalSamples.Fhir.create().bloodPressure(),
            ObservationVitalSamples.Fhir.create().weight(),
            ObservationLabSamples.Fhir.create().observation());
  }

  @Test
  void labToFhir() {
    assertThat(
            R4ObservationTransformer.builder()
                .patientIcn("p1")
                .resultsEntry(ObservationLabSamples.Vista.create().resultsByStation())
                .vitalVuidMapper(mapper)
                .build()
                .toFhir())
        .containsExactly(ObservationLabSamples.Fhir.create().observation());
  }

  @Test
  public void vitalToFhir() {
    assertThat(
            R4ObservationTransformer.builder()
                .patientIcn("p1")
                .resultsEntry(ObservationVitalSamples.Vista.create().resultsByStation())
                .vitalVuidMapper(mapper)
                .build()
                .toFhir()
                .collect(Collectors.toList()))
        .isEqualTo(ObservationVitalSamples.Fhir.create().observations());
  }
}
