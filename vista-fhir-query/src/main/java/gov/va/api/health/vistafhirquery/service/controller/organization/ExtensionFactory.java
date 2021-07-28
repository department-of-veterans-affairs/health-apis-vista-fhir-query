package gov.va.api.health.vistafhirquery.service.controller.organization;

import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import gov.va.api.health.r4.api.datatypes.Quantity;
import gov.va.api.health.r4.api.elements.Extension;
import gov.va.api.lighthouse.charon.models.lhslighthouserpcgateway.LhsLighthouseRpcGatewayResponse;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

import static java.util.Collections.singletonList;

@AllArgsConstructor(staticName = "of")
class ExtensionFactory {
  LhsLighthouseRpcGatewayResponse.FilemanEntry entry;
  Map<String, Boolean> yesNo;

  public Extension ofBoolean(String fieldNumber, String url) {
    var value = entry.internal(fieldNumber, yesNo);
    if (value.isEmpty()) {
      return null;
    }
    return Extension.builder().valueBoolean(value.get()).url(url).build();
  }

  public Extension ofCodeableConcept(String fieldNumber, String system, String url) {
    var value = entry.internal(fieldNumber);
    if (value.isEmpty()) {
      return null;
    }
    return Extension.builder()
        .valueCodeableConcept(
            CodeableConcept.builder()
                .coding(singletonList(Coding.builder().code(value.get()).system(system).build()))
                .build())
        .url(url)
        .build();
  }

  public Extension ofString(String fieldNumber, String url) {
    var value = entry.internal(fieldNumber);
    if (value.isEmpty()) {
      return null;
    }
    return Extension.builder().url(url).valueString(value.get()).build();
  }

  public Extension ofQuantity(String fieldNumber, String unit, String system) {
    var value = entry.internal(fieldNumber);
    if (value.isEmpty()) {
      return null;
    }
    BigDecimal quantity;
    try {
      quantity = new BigDecimal(value.get());
    } catch (NumberFormatException e) {
      return null;
    }
    return Extension.builder()
        .valueQuantity(Quantity.builder().value(quantity).unit(unit).system(system).build())
        .build();
  }
}
