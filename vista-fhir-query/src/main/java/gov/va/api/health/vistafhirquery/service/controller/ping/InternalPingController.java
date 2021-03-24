package gov.va.api.health.vistafhirquery.service.controller.ping;

import gov.va.api.health.vistafhirquery.service.config.VistaApiConfig;
import gov.va.api.health.vistafhirquery.service.controller.VistalinkApiClient;
import gov.va.api.lighthouse.charon.api.RpcInvocationResult;
import gov.va.api.lighthouse.charon.api.RpcResponse;
import gov.va.api.lighthouse.charon.models.xobvtestping.XobvTestPing;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = {"/internal"})
@AllArgsConstructor(onConstructor_ = {@Autowired, @NonNull})
public class InternalPingController {

  private final VistalinkApiClient vistalinkApiClient;

  private final VistaApiConfig vistaApiConfig;

  /** Ping All VistA sites for a given patient. */
  @SneakyThrows
  @GetMapping(value = "/ping/{icn}")
  public List<RpcInvocationResult> ping(@PathVariable("icn") String icn) {
    RpcResponse response =
        vistalinkApiClient.requestForPatient(
            icn,
            XobvTestPing.Request.builder()
                .context(Optional.ofNullable(vistaApiConfig.getApplicationProxyUserContext()))
                .build());
    return response.results();
  }
}
