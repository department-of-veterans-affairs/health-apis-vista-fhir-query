package gov.va.api.health.vistafhirquery.service.controller.observation;

import static gov.va.api.health.vistafhirquery.service.controller.RpcResponseVerifier.verifyAndReturnResults;

import gov.va.api.health.r4.api.resources.Observation;
import gov.va.api.health.vistafhirquery.service.config.LinkProperties;
import gov.va.api.health.vistafhirquery.service.controller.R4Bundler;
import gov.va.api.health.vistafhirquery.service.controller.ResourceExceptions.NotFound;
import gov.va.api.health.vistafhirquery.service.controller.VistaIdentifierSegment;
import gov.va.api.health.vistafhirquery.service.controller.VistalinkApiClient;
import gov.va.api.lighthouse.vistalink.api.RpcInvocationResult;
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
public class R4ObservationController {
  private final VistalinkApiClient vistalinkApiClient;

  private final LinkProperties linkProperties;

  private final R4Bundler bundler;

  private Observation.Bundle bundle(
      Map<String, String> parameters, List<Observation> observations) {
    return bundler.bundle(
        "Observation",
        parameters,
        linkProperties,
        observations,
        Observation.Entry::new,
        Observation.Bundle::new);
  }

  private Map<String, VprGetPatientData.Response.Results> filterForValidResults(
      RpcResponse rpcResponse) {
    List<RpcInvocationResult> invocationResults = verifyAndReturnResults(rpcResponse);
    return VprGetPatientData.create()
        .fromResults(invocationResults)
        .resultsByStation()
        .entrySet()
        .stream()
        .filter(entry -> entry.getValue().vitalStream().anyMatch(Vitals.Vital::isNotEmpty))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  /** Read by publicId. */
  @SneakyThrows
  @GetMapping(value = {"/{publicId}"})
  public Observation read(@PathVariable("publicId") String publicId) {
    log.info("ToDo: Search By _id and identifier");
    log.info("ToDo: PublicId to VistaIdentifier WitnessProtection");
    VistaIdentifierSegment ids = VistaIdentifierSegment.parse(publicId);
    RpcResponse rpcResponse =
        vistalinkApiClient.requestForVistaSite(
            ids.vistaSiteId(),
            VprGetPatientData.Request.builder()
                .dfn(";" + ids.patientIdentifier())
                .type(Set.of(VprGetPatientData.Domains.vitals))
                .max(Optional.of("1"))
                .id(Optional.of(ids.vistaRecordId()))
                .build()
                .asDetails());
    Map<String, VprGetPatientData.Response.Results> filteredResults =
        filterForValidResults(rpcResponse);
    if (filteredResults.isEmpty()) {
      NotFound.because("Identifier not found in VistA: " + publicId);
    }
    log.info("ToDo: VistaIdentifier to PublicId WitnessProtection");
    log.info("ToDo: Map to R4 Observation");
    return Observation.builder().id(publicId).build();
  }

  /** Search for Observation records by Patient. */
  @SneakyThrows
  @GetMapping(params = {"patient"})
  public Observation.Bundle searchByPatient(
      @RequestParam(name = "patient", required = true) String patient,
      @RequestParam(name = "_count", required = false) @Min(0) Integer count) {
    int countValue = count == null ? linkProperties.getDefaultPageSize() : count;
    Map<String, String> parameters = Map.of("patient", patient, "_count", "" + countValue);
    log.info("ToDo: Parameter Handling: page, patient, etc.");
    // Default .max() value is 9999
    RpcResponse rpcResponse =
        vistalinkApiClient.requestForPatient(
            patient,
            VprGetPatientData.Request.builder()
                .dfn(";" + patient)
                .type(Set.of(VprGetPatientData.Domains.vitals))
                .build()
                .asDetails());
    Map<String, VprGetPatientData.Response.Results> filteredResults =
        filterForValidResults(rpcResponse);
    if (filteredResults.isEmpty()) {
      return bundle(parameters, List.of());
    }
    log.info("ToDo: sort Results so we can confidently do paging");
    log.info("ToDo: VistA paging fanciness");
    log.info("ToDo: FHIR References WitnessProtection");
    log.info("ToDo: Map to R4 Observation");
    return bundle(parameters, List.of(Observation.builder().id("myPublicId").build()));
  }
}
