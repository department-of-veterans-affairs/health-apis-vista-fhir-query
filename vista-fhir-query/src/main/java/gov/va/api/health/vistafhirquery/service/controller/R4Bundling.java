package gov.va.api.health.vistafhirquery.service.controller;

import gov.va.api.health.r4.api.bundle.AbstractBundle;
import gov.va.api.health.r4.api.bundle.AbstractEntry;
import gov.va.api.health.r4.api.resources.Resource;
import gov.va.api.health.vistafhirquery.service.config.LinkProperties;
import java.util.function.Supplier;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class R4Bundling<
    ResourceT extends Resource,
    EntryT extends AbstractEntry<ResourceT>,
    BundleT extends AbstractBundle<EntryT>> {

  private final Supplier<BundleT> newBundle;

  private final Supplier<EntryT> newEntry;

  private final LinkProperties linkProperties;

  public static <R extends Resource, E extends AbstractEntry<R>, B extends AbstractBundle<E>>
      R4BundlingBuilder<R, E, B> newBundle(Supplier<B> newBundle) {
    return R4Bundling.<R, E, B>builder().newBundle(newBundle);
  }
}
