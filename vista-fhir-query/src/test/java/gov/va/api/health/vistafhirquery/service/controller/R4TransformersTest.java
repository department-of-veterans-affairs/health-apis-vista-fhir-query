package gov.va.api.health.vistafhirquery.service.controller;

import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.toBigDecimal;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.valueOfValueOnlyXmlAttribute;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.vistalink.models.ValueOnlyXmlAttribute;
import java.math.BigDecimal;
import org.junit.Test;

public class R4TransformersTest {
  @Test
  public void bigDecimal() {
    assertThat(toBigDecimal(null)).isNull();
    assertThat(toBigDecimal("")).isNull();
    assertThat(toBigDecimal("hello")).isNull();
    assertThat(toBigDecimal("0.")).isNull();
    assertThat(toBigDecimal(".0")).isNull();
    assertThat(toBigDecimal(".")).isNull();
    assertThat(toBigDecimal("0.0.0")).isNull();
    assertThat(toBigDecimal("1")).isEqualTo(new BigDecimal("1"));
    assertThat(toBigDecimal("1.1")).isEqualTo(new BigDecimal("1.1"));
  }

  @Test
  public void value() {
    assertThat(valueOfValueOnlyXmlAttribute(null)).isNull();
    assertThat(valueOfValueOnlyXmlAttribute(ValueOnlyXmlAttribute.builder().build())).isNull();
    assertThat(valueOfValueOnlyXmlAttribute(ValueOnlyXmlAttribute.builder().value("").build()))
        .isEqualTo("");
    assertThat(valueOfValueOnlyXmlAttribute(ValueOnlyXmlAttribute.builder().value("value").build()))
        .isEqualTo("value");
  }
}
