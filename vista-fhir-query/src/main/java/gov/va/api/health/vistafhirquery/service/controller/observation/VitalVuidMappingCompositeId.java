package gov.va.api.health.vistafhirquery.service.controller.observation;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** VitalVuidMappingCompositeId. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VitalVuidMappingCompositeId implements Serializable {

  private String sourceValue;

  private Short codingSystemId;

  private String code;

  private String uri;
}
