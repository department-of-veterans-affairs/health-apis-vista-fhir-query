package gov.va.api.health.vistafhirquery.tests;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public final class TestIds {
  @NonNull String observationVitalSign;
  @NonNull String observationLaboratory;
  @NonNull String patient;
  @Builder.Default String unknown = "5555555555555";
}
