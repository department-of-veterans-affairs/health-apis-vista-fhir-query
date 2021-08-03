package gov.va.api.health.vistafhirquery.tests.r4;

import gov.va.api.health.r4.api.bundle.AbstractBundle;

public class R4TestSupport {

  /** Return true if the bundle has at least one entry. */
  public static <T extends AbstractBundle<?>> boolean atLeastOneEntry(T bundle) {
    return !bundle.entry().isEmpty();
  }
}
