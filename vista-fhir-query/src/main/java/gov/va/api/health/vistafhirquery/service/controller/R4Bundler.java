package gov.va.api.health.vistafhirquery.service.controller;

import static java.lang.String.join;
import static java.util.stream.Collectors.joining;

import gov.va.api.health.r4.api.bundle.AbstractBundle;
import gov.va.api.health.r4.api.bundle.AbstractEntry;
import gov.va.api.health.r4.api.bundle.BundleLink;
import gov.va.api.health.r4.api.resources.Resource;
import gov.va.api.health.vistafhirquery.service.config.LinkProperties;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.AlternatePatientIds;
import gov.va.api.lighthouse.charon.models.TypeSafeRpcResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

/** Provides standard mapping from a TypeSafeRpcResponse to a FHIR bundle. */
@Slf4j
@Builder
public class R4Bundler<
        RpcResponseT extends TypeSafeRpcResponse,
        ResourceT extends Resource,
        EntryT extends AbstractEntry<ResourceT>,
        BundleT extends AbstractBundle<EntryT>>
    implements Function<RpcResponseT, BundleT> {
  private final String resourceType;

  private final AlternatePatientIds alternatePatientIds;

  private final LinkProperties linkProperties;

  private final HttpServletRequest request;

  /** The transformation process that will be applied to the results. */
  private final R4Transformation<RpcResponseT, ResourceT> transformation;

  /** The bundling configuration that will be used to create the actual bundle. */
  private final R4Bundling<ResourceT, EntryT, BundleT> bundling;

  @Override
  public BundleT apply(RpcResponseT rpcResult) {
    List<ResourceT> resources = transformation.toResource().apply(rpcResult);
    BundleT bundle = bundling.newBundle().get();
    bundle.resourceType("Bundle");
    bundle.type(AbstractBundle.BundleType.searchset);
    bundle.total(resources.size());
    bundle.link(toLinks());
    int page = HttpRequestParameters.integer(request, "page", 1);
    if (page <= 0) {
      throw ResourceExceptions.BadSearchParameters.because("page value must be greater than 0");
    }
    int count =
        HttpRequestParameters.integer(request, "_count", linkProperties.getDefaultPageSize());
    if (count < 0) {
      throw ResourceExceptions.BadSearchParameters.because(
          "count value must be greater than or equal to 0");
    }
    if (resources.size() > count) {
      resources = resources.subList(0, count);
    }
    bundle.entry(resources.stream().map(this::toEntry).collect(Collectors.toList()));
    return bundle;
  }

  private String parameter(String name, String value) {
    log.info("{}={} : {}", name, value, alternatePatientIds.patientIdParameters());
    if (alternatePatientIds.isPatientIdParameter(name)) {
      value = alternatePatientIds.toPublicId(value);
      log.info("{} ==> {}", name, value);
    }
    return join("=", name, value);
  }

  private String request() {
    return request.getParameterMap().entrySet().stream()
        .flatMap(e -> Stream.of(e.getValue()).map(value -> parameter(e.getKey(), value)))
        .collect(joining("&"));
  }

  private EntryT toEntry(ResourceT resource) {
    EntryT entry = bundling.newEntry().get();
    entry.fullUrl(linkProperties.r4().readUrl(resource));
    entry.resource(resource);
    entry.search(AbstractEntry.Search.builder().mode(AbstractEntry.SearchMode.match).build());
    return entry;
  }

  /** Create R4 BundleLinks. */
  private List<BundleLink> toLinks() {
    List<BundleLink> links = new ArrayList<>(5);
    links.add(
        BundleLink.builder()
            .relation(BundleLink.LinkRelation.self)
            .url(linkProperties.r4().resourceUrl(resourceType) + "?" + request())
            .build());
    return links;
  }
}
