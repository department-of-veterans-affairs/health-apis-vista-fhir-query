package gov.va.api.health.vistafhirquery.service.controller;

import gov.va.api.health.r4.api.bundle.AbstractBundle;
import gov.va.api.health.r4.api.bundle.AbstractEntry;
import gov.va.api.health.r4.api.resources.Resource;
import java.util.function.Supplier;
import lombok.Builder;
import lombok.Getter;

/** This structure represents the components needed to construct new bundles. */
@Getter
@Builder
public class R4Bundling<
    ResourceT extends Resource,
    EntryT extends AbstractEntry<ResourceT>,
    BundleT extends AbstractBundle<EntryT>> {

  /** How to create a new Bundle instance, typically a method reference to `new`. */
  private final Supplier<BundleT> newBundle;

  /** How to create a new Entry instance, typically a method reference to `new`. */
  private final Supplier<EntryT> newEntry;

  public static <R extends Resource, E extends AbstractEntry<R>, B extends AbstractBundle<E>>
      R4BundlingBuilder<R, E, B> newBundle(Supplier<B> newBundle) {
    return R4Bundling.<R, E, B>builder().newBundle(newBundle);
  }
}
