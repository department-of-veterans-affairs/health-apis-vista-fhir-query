package gov.va.api.health.vistafhirquery.service.controller;

import gov.va.api.health.r4.api.bundle.AbstractBundle;
import gov.va.api.health.r4.api.bundle.AbstractEntry;
import gov.va.api.health.r4.api.bundle.BundleLink;
import gov.va.api.health.r4.api.resources.Resource;
import gov.va.api.lighthouse.vistalink.models.TypeSafeRpcResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public class R4Bundler<
        RpcResponseT extends TypeSafeRpcResponse,
        ResourceT extends Resource,
        EntryT extends AbstractEntry<ResourceT>,
        BundleT extends AbstractBundle<EntryT>>
    implements Function<RpcResponseT, BundleT> {
  String resourceType;

  Map<String, String> parameters;

  R4Transformation<RpcResponseT, ResourceT> transformation;

  R4Bundling<ResourceT, EntryT, BundleT> bundling;

  public static <RpcResponseT extends TypeSafeRpcResponse, ResourceT extends Resource>
      R4BundlerPart1<RpcResponseT, ResourceT> forTransformation(
          R4Transformation<RpcResponseT, ResourceT> transformation) {
    return R4BundlerPart1.<RpcResponseT, ResourceT>builder().transformation(transformation).build();
  }

  @Override
  public BundleT apply(RpcResponseT rpcResult) {
    log.info("ToDo: Replace parameters map and resourceType with ParamMappings class");
    log.info("ToDo: Determine total results better");
    log.info(
        "ToDo: We'll have to do special paging logic here because "
            + "vista gives us _ALL_ the results at once");
    List<ResourceT> resources = transformation.toResource().apply(rpcResult);
    BundleT bundle = bundling.newBundle().get();
    bundle.resourceType("Bundle");
    bundle.total(resources.size());
    bundle.link(toLinks());
    bundle.entry(resources.stream().map(this::toEntry).collect(Collectors.toList()));
    return bundle;
  }

  private EntryT toEntry(ResourceT resource) {
    EntryT entry = bundling.newEntry().get();
    entry.fullUrl(bundling.linkProperties().r4().readUrl(resource));
    entry.resource(resource);
    entry.search(AbstractEntry.Search.builder().mode(AbstractEntry.SearchMode.match).build());
    return entry;
  }

  private List<BundleLink> toLinks() {
    log.info("ToDo: Build bundle links dynamically");
    List<BundleLink> links = new ArrayList<>(5);
    links.add(
        BundleLink.builder()
            .relation(BundleLink.LinkRelation.self)
            .url(
                bundling.linkProperties().r4().resourceUrl(resourceType)
                    + "?"
                    + parameters.entrySet().stream()
                        .map(e -> String.join("=", e.getKey(), e.getValue()))
                        .collect(Collectors.joining("&")))
            .build());
    return links;
  }

  @Builder
  public static class R4BundlerPart1<V extends TypeSafeRpcResponse, R extends Resource> {
    private final R4Transformation<V, R> transformation;

    public <E extends AbstractEntry<R>, B extends AbstractBundle<E>>
        R4BundlerBuilder<V, R, E, B> bundling(R4Bundling<R, E, B> bundling) {
      return R4Bundler.<V, R, E, B>builder().transformation(transformation).bundling(bundling);
    }
  }
}
