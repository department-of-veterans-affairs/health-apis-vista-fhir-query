package gov.va.api.health.vistafhirquery.service.controller.observation;

import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.toNewYorkFilemanDateString;

import gov.va.api.health.r4.api.resources.Observation;
import gov.va.api.health.vistafhirquery.service.controller.DateSearchBoundaries;
import gov.va.api.health.vistafhirquery.service.controller.R4Bundler;
import gov.va.api.health.vistafhirquery.service.controller.R4BundlerFactory;
import gov.va.api.health.vistafhirquery.service.controller.R4Bundling;
import gov.va.api.health.vistafhirquery.service.controller.R4Transformation;
import gov.va.api.health.vistafhirquery.service.controller.ResourceExceptions;
import gov.va.api.health.vistafhirquery.service.controller.VistaIdentifierSegment;
import gov.va.api.health.vistafhirquery.service.controller.VistalinkApiClient;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.WitnessProtection;
import gov.va.api.lighthouse.vistalink.api.RpcResponse;
import gov.va.api.lighthouse.vistalink.models.vprgetpatientdata.Labs;
import gov.va.api.lighthouse.vistalink.models.vprgetpatientdata.Vitals;
import gov.va.api.lighthouse.vistalink.models.vprgetpatientdata.VprGetPatientData;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
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

  @NonNull private final R4BundlerFactory bundlerFactory;

  @NonNull private final VistalinkApiClient vistalinkApiClient;

  @NonNull private final VitalVuidMapper vitalVuids;

  @NonNull private final WitnessProtection witnessProtection;

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
        transformation(ids.patientIdentifier(), null).toResource().apply(vprPatientData);
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
      @RequestParam(name = "date", required = false) @Size(max = 2) String[] date,
      @RequestParam(name = "code", required = false) String codeCsv,
      @RequestParam(name = "_count", required = false) @Min(0) Integer count,
      HttpServletRequest request) {
    // Default .max() value is 9999

    DateSearchBoundaries boundaries = DateSearchBoundaries.of(date);
    RpcResponse rpcResponse =
        vistalinkApiClient.requestForPatient(
            patient,
            VprGetPatientData.Request.builder()
                .dfn(VprGetPatientData.Request.PatientId.forIcn(patient))
                .type(Set.of(VprGetPatientData.Domains.vitals, VprGetPatientData.Domains.labs))
                .start(toNewYorkFilemanDateString(boundaries.start()))
                .stop(toNewYorkFilemanDateString(boundaries.stop()))
                .build()
                .asDetails());
    VprGetPatientData.Response vprPatientData =
        VprGetPatientData.create().fromResults(rpcResponse.results());
    return toBundle(request).apply(vprPatientData);
  }

  private R4Bundler<VprGetPatientData.Response, Observation, Observation.Entry, Observation.Bundle>
      toBundle(HttpServletRequest request) {
    return bundlerFactory
        .forTransformation(
            transformation(request.getParameter("patient"), request.getParameter("code")))
        .bundling(
            R4Bundling.newBundle(Observation.Bundle::new).newEntry(Observation.Entry::new).build())
        .resourceType("Observation")
        .request(request)
        .build();
  }

  private R4Transformation<VprGetPatientData.Response, Observation> transformation(
      String patientIdentifier, String codes) {
    return R4Transformation.<VprGetPatientData.Response, Observation>builder()
        .toResource(
            rpcResponse ->
                rpcResponse.resultsByStation().entrySet().parallelStream()
                    .filter(
                        entry ->
                            entry.getValue().vitalStream().anyMatch(Vitals.Vital::isNotEmpty)
                                || entry.getValue().labStream().anyMatch(Labs.Lab::isNotEmpty))
                    .flatMap(
                        entry ->
                            R4ObservationCollector.builder()
                                .patientIcn(patientIdentifier)
                                .resultsEntry(entry)
                                .vitalVuidMapper(vitalVuids)
                                .codes(codes)
                                .build()
                                .toFhir())
                    .collect(Collectors.toList()))
        .build();
  }
}
