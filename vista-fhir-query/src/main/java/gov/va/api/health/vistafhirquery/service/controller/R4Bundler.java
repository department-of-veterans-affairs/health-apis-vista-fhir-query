package gov.va.api.health.vistafhirquery.service.controller;

import static java.lang.String.join;
import static java.util.stream.Collectors.joining;

import gov.va.api.health.r4.api.bundle.AbstractBundle;
import gov.va.api.health.r4.api.bundle.AbstractEntry;
import gov.va.api.health.r4.api.bundle.BundleLink;
import gov.va.api.health.r4.api.resources.Resource;
import gov.va.api.health.vistafhirquery.service.config.LinkProperties;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.FakeIds;
import gov.va.api.lighthouse.vistalink.models.TypeSafeRpcResponse;
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

  private final FakeIds fakeIds;

  private final LinkProperties linkProperties;

  private final HttpServletRequest parameters;

  /** The transformation process that will be applied to the results. */
  private final R4Transformation<RpcResponseT, ResourceT> transformation;

  /** The bundling configuration that will be used to create the actual bundle. */
  private final R4Bundling<ResourceT, EntryT, BundleT> bundling;

  @Override
  public BundleT apply(RpcResponseT rpcResult) {
    log.info("ToDo: Determine total results better");
    log.info(
        "ToDo: We'll have to do special paging logic here because "
            + "vista gives us _ALL_ the results at once");
    List<ResourceT> resources = transformation.toResource().apply(rpcResult);
    BundleT bundle = bundling.newBundle().get();
    bundle.resourceType("Bundle");
    bundle.type(AbstractBundle.BundleType.searchset);
    bundle.total(resources.size());
    bundle.link(toLinks());
    log.info("ToDo: better count handling");
    String countParam = parameters.getParameter("_count");
    int count =
        countParam != null ? Integer.parseInt(countParam) : linkProperties.getDefaultPageSize();
    if (resources.size() > count) {
      resources = resources.subList(0, count);
    }
    bundle.entry(resources.stream().map(this::toEntry).collect(Collectors.toList()));
    return bundle;
  }

  private String parameter(String name, String value) {
    log.info("{}={} : {}", name, value, fakeIds.patientIdParameters());
    if (fakeIds.isPatientIdParameter(name)) {
      value = fakeIds.toPublicId(value);
      log.info("{} ==> {}", name, value);
    }
    return join("=", name, value);
  }

  private String parameters() {
    return parameters.getParameterMap().entrySet().stream()
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
    log.info("ToDo: Build bundle links dynamically");
    List<BundleLink> links = new ArrayList<>(5);
    links.add(
        BundleLink.builder()
            .relation(BundleLink.LinkRelation.self)
            .url(linkProperties.r4().resourceUrl(resourceType) + "?" + parameters())
            .build());
    return links;
  }
}
