package gov.va.api.health.vistafhirquery.service.controller;

import gov.va.api.lighthouse.vistalink.api.RpcDetails;
import gov.va.api.lighthouse.vistalink.api.RpcInvocationResult;
import gov.va.api.lighthouse.vistalink.api.RpcResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/* This class is temporary.
 * Eventually, controllers will be split based on the fhir resources they represent.
 * Therefore, no tests are necessary for this class. */
@RestController
@RequestMapping(value = {"/internal"})
@AllArgsConstructor(onConstructor_ = @Autowired)
public class VistaFhirQueryController {

  private final VistalinkApiClient vistalinkApiClient;

  /** Ping All VistA sites for a given patient. */
  @SneakyThrows
  @GetMapping(value = "/ping/{icn}")
  public List<RpcInvocationResult> ping(@PathVariable("icn") String icn) {
    RpcResponse response =
        vistalinkApiClient.requestForPatient(
            icn,
            RpcDetails.builder().name("XOBV TEST PING").context("XOBV VISTALINK TESTER").build());
    return response.results();
  }
}
