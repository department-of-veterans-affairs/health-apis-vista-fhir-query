package gov.va.api.health.vistafhirquery.service.controller.observation;

import gov.va.api.health.r4.api.resources.Observation;
import gov.va.api.health.vistafhirquery.service.config.LinkProperties;
import gov.va.api.health.vistafhirquery.service.controller.R4Bundler;
import gov.va.api.health.vistafhirquery.service.controller.R4Bundling;
import gov.va.api.health.vistafhirquery.service.controller.R4Transformation;
import gov.va.api.health.vistafhirquery.service.controller.ResourceExceptions;
import gov.va.api.health.vistafhirquery.service.controller.VistaIdentifierSegment;
import gov.va.api.health.vistafhirquery.service.controller.VistalinkApiClient;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.WitnessProtection;
import gov.va.api.lighthouse.vistalink.api.RpcResponse;
import gov.va.api.lighthouse.vistalink.models.vprgetpatientdata.Vitals;
import gov.va.api.lighthouse.vistalink.models.vprgetpatientdata.VprGetPatientData;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Request Mappings for Observation Profile using a VistA backend.
 *
 * @implSpec
 *     https://build.fhir.org/ig/HL7/US-Core-R4/StructureDefinition-us-core-observation-lab.html
 */
@Slf4j
@Validated
@RestController
@RequestMapping(
    value = "/r4/Observation",
    produces = {"application/json", "application/fhir+json"})
@AllArgsConstructor(onConstructor_ = @Autowired)
@Builder
public class R4ObservationController {
  private final VistalinkApiClient vistalinkApiClient;

  private final LinkProperties linkProperties;

  private final VitalVuidMapper vitalVuids;

  private final WitnessProtection witnessProtection;

  private VistaIdentifierSegment parseOrDie(String publicId) {
    try {
      return VistaIdentifierSegment.parse(witnessProtection.toPrivateId(publicId));
    } catch (IllegalArgumentException e) {
      throw new ResourceExceptions.NotFound(publicId);
    }
  }

  /** Read by publicId. */
  @SneakyThrows
  @GetMapping(value = {"/{publicId}"})
  public Observation read(@PathVariable("publicId") String publicId) {
    log.info("ToDo: Search By _id and identifier");

    VistaIdentifierSegment ids = parseOrDie(publicId);
    RpcResponse rpcResponse =
        vistalinkApiClient.requestForVistaSite(
            ids.vistaSiteId(),
            VprGetPatientData.Request.builder()
                .dfn(VprGetPatientData.Request.PatientId.forIcn(ids.patientIdentifier()))
                .type(Set.of(VprGetPatientData.Domains.vitals))
                .max(Optional.of("1"))
                .id(Optional.of(ids.vistaRecordId()))
                .build()
                .asDetails());
    VprGetPatientData.Response vprPatientData =
        VprGetPatientData.create().fromResults(rpcResponse.results());
    List<Observation> resources =
        transformation(ids.patientIdentifier()).toResource().apply(vprPatientData);
    if (resources.isEmpty()) {
      ResourceExceptions.NotFound.because("Identifier not found in VistA: " + publicId);
    }
    if (resources.size() != 1) {
      ResourceExceptions.ExpectationFailed.because(
          "Too many results returned. Expected 1 but found %d.", resources.size());
    }
    return resources.get(0);
  }

  /** Search for Observation records by Patient. */
  @SneakyThrows
  @GetMapping(params = {"patient"})
  public Observation.Bundle searchByPatient(
      @RequestParam(name = "patient") String patient,
      @RequestParam(name = "_count", required = false) @Min(0) Integer count) {
    int countValue = count == null ? linkProperties.getDefaultPageSize() : count;
    Map<String, String> parameters = Map.of("patient", patient, "_count", "" + countValue);
    // Default .max() value is 9999
    RpcResponse rpcResponse =
        vistalinkApiClient.requestForPatient(
            patient,
            VprGetPatientData.Request.builder()
                .dfn(VprGetPatientData.Request.PatientId.forIcn(patient))
                .type(Set.of(VprGetPatientData.Domains.vitals))
                .build()
                .asDetails());
    VprGetPatientData.Response vprPatientData =
        VprGetPatientData.create().fromResults(rpcResponse.results());
    return toBundle(parameters).apply(vprPatientData);
  }

  private R4Bundler<VprGetPatientData.Response, Observation, Observation.Entry, Observation.Bundle>
      toBundle(Map<String, String> parameters) {
    return R4Bundler.forTransformation(transformation(parameters.get("patient")))
        .bundling(
            R4Bundling.newBundle(Observation.Bundle::new)
                .newEntry(Observation.Entry::new)
                .linkProperties(linkProperties)
                .build())
        .resourceType("Observation")
        .parameters(parameters)
        .build();
  }

  private R4Transformation<VprGetPatientData.Response, Observation> transformation(
      String patientIdentifier) {
    return R4Transformation.<VprGetPatientData.Response, Observation>builder()
        .toResource(
            rpcResponse -> {
              // Filter out empty results
              Map<String, VprGetPatientData.Response.Results> filteredResults =
                  rpcResponse.resultsByStation().entrySet().stream()
                      .filter(
                          entry ->
                              entry.getValue().vitalStream().anyMatch(Vitals.Vital::isNotEmpty))
                      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
              if (filteredResults.isEmpty()) {
                return List.of();
              }
              // Parallel trasformation of VistA sites'
              log.info("{} vuid mappings found", vitalVuids.mappings().size());
              return filteredResults.entrySet().parallelStream()
                  .flatMap(
                      entry ->
                          R4ObservationTransformer.builder()
                              .patientIcn(patientIdentifier)
                              .resultsEntry(entry)
                              .vitalVuidMapper(vitalVuids)
                              .build()
                              .toFhir())
                  .collect(Collectors.toList());
            })
        .build();
  }
}
