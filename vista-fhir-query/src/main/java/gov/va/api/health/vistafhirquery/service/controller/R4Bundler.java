package gov.va.api.health.vistafhirquery.service.controller;

import gov.va.api.health.r4.api.bundle.AbstractBundle;
import gov.va.api.health.r4.api.bundle.AbstractEntry;
import gov.va.api.health.r4.api.bundle.BundleLink;
import gov.va.api.health.r4.api.resources.Resource;
import gov.va.api.health.vistafhirquery.service.config.LinkProperties;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * The bundler is capable of producing type specific bundles for resources. It leverages supporting
 * helper functions in a provided context to create new instances of specific bundle and entry
 * types. Paging links are supported via an injectable PageLinks.
 */
@Service
public class R4Bundler {
  // ToDo ParameterMapping fanciness will replace parameters map and resourceType
  /**
   * Return new bundle, filled with entries created from the resources.
   *
   * @param linkProperties Configurable link defaults and url creation methods
   * @param resources The FHIR resources to compose the bundle
   * @param newEntry Used to create new instances for entries, one for each resource
   * @param newBundle Used to create a new instance of the bundle (called once)
   */
  public <R extends Resource, E extends AbstractEntry<R>, B extends AbstractBundle<E>> B bundle(
      String resourceType,
      Map<String, String> parameters,
      LinkProperties linkProperties,
      List<R> resources,
      Supplier<E> newEntry,
      Supplier<B> newBundle) {
    B bundle = newBundle.get();
    bundle.resourceType("Bundle");
    // ToDo this needs to do better
    bundle.total(resources.size());
    // ToDo this needs to do waaay better
    bundle.link(
        List.of(
            BundleLink.builder()
                .relation(BundleLink.LinkRelation.self)
                .url(
                    linkProperties.r4().resourceUrl(resourceType)
                        + "?"
                        + parameters.entrySet().stream()
                            .map(e -> String.join("=", e.getKey(), e.getValue()))
                            .collect(Collectors.joining("&")))
                .build()));
    bundle.entry(
        resources.stream()
            .map(r -> entry(r, linkProperties, newEntry))
            .collect(Collectors.toList()));
    return bundle;
  }

  /**
   * Return new entry.
   *
   * @param resource The FHIR resources to compose the bundle
   * @param linkProperties Configurable link defaults and url creation methods
   * @param newEntry Used to create new instances for entries, one for each resource
   */
  public <R extends Resource, E extends AbstractEntry<R>> E entry(
      R resource, LinkProperties linkProperties, Supplier<E> newEntry) {
    E entry = newEntry.get();
    entry.fullUrl(linkProperties.r4().readUrl(resource));
    entry.resource(resource);
    entry.search(AbstractEntry.Search.builder().mode(AbstractEntry.SearchMode.match).build());
    return entry;
  }
}
