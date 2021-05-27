package gov.va.api.health.vistafhirquery.tests;

import gov.va.api.health.sentinel.ServiceDefinition;
import java.util.Optional;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/** SystemDefinition. */
@Value
@Builder
public final class SystemDefinition {
  @NonNull ServiceDefinition internal;

  @NonNull ServiceDefinition r4;

  @NonNull TestIds publicIds;

  Optional<String> clientKey;
}
