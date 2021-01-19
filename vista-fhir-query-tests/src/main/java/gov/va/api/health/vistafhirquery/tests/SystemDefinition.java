package gov.va.api.health.vistafhirquery.tests;

import gov.va.api.health.sentinel.ServiceDefinition;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public final class SystemDefinition {
  @NonNull ServiceDefinition internal;

  @NonNull TestIds publicIds;
}
