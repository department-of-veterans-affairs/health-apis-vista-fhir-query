package gov.va.api.health.vistafhirquery.service.controller.endpoint;

import static java.util.stream.Collectors.toList;

import gov.va.api.health.r4.api.bundle.AbstractBundle;
import gov.va.api.health.r4.api.bundle.AbstractEntry;
import gov.va.api.health.r4.api.bundle.BundleLink;
import gov.va.api.health.r4.api.resources.Endpoint;
import gov.va.api.health.vistafhirquery.service.config.LinkProperties;
import gov.va.api.lighthouse.charon.api.RpcPrincipalLookup;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
    value = {"/r4/Endpoint"},
    produces = {"application/json", "application/fhir+json"})
@AllArgsConstructor(onConstructor_ = {@Autowired, @NonNull})
public class R4EndpointController {
  private final LinkProperties linkProperties;

  private RpcPrincipalLookup rpcPrincipalLookup;

  /** Return a bundle of all endpoints. */
  @GetMapping
  public Endpoint.Bundle getAllEndpoints() {
    Set<String> stations = stations("LHS LIGHTHOUSE RPC GATEWAY");
    List<Endpoint.Entry> endpoints =
        stations.stream()
            .sorted()
            .map(
                site ->
                    Endpoint.Entry.builder()
                        .fullUrl(linkProperties.getPublicUrl() + "/" + site + "/r4")
                        .resource(
                            R4EndpointTransformer.builder()
                                .site(site)
                                .linkProperties(linkProperties)
                                .build()
                                .toEndpoint())
                        .search(
                            AbstractEntry.Search.builder()
                                .mode(AbstractEntry.SearchMode.match)
                                .build())
                        .build())
            .collect(toList());
    return toBundle(endpoints);
  }

  private Set<String> stations(String rpcName) {
    return rpcPrincipalLookup.findByName(rpcName).keySet();
  }

  private Endpoint.Bundle toBundle(List<Endpoint.Entry> entries) {
    return Endpoint.Bundle.builder()
        .resourceType("Bundle")
        .type(AbstractBundle.BundleType.searchset)
        .total(entries.size())
        .link(
            List.of(
                BundleLink.builder()
                    .relation(BundleLink.LinkRelation.self)
                    .url(linkProperties.r4().resourceUrl("Endpoint"))
                    .build()))
        .entry(entries)
        .build();
  }
}
