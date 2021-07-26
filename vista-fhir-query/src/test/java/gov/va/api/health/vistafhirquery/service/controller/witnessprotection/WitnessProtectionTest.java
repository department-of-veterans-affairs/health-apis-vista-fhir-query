package gov.va.api.health.vistafhirquery.service.controller.witnessprotection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import gov.va.api.health.ids.client.IdEncoder.BadId;
import gov.va.api.health.vistafhirquery.service.controller.PatientTypeCoordinates;
import gov.va.api.health.vistafhirquery.service.controller.ProviderTypeCoordinates;
import gov.va.api.health.vistafhirquery.service.controller.ResourceExceptions.NotFound;
import org.junit.jupiter.api.Test;

class WitnessProtectionTest {
  @Test
  void toPatientTypeCoordinates() {
    assertThat(new FugaziWP().toPatientTypeCoordinates("fugazi:123+456+789"))
        .isEqualTo(PatientTypeCoordinates.fromString("123+456+789"));
    assertThatExceptionOfType(NotFound.class)
        .isThrownBy(() -> new FugaziWP().toPatientTypeCoordinates("cannot-parse"));
    assertThatExceptionOfType(NotFound.class)
        .isThrownBy(() -> new BadIdWP().toPatientTypeCoordinates("x"));
  }

  @Test
  void toProviderTypeCoordinates() {
    assertThat(new FugaziWP().toProviderTypeCoordinates("fugazi:123+456"))
        .isEqualTo(ProviderTypeCoordinates.fromString("123+456"));
    assertThatExceptionOfType(NotFound.class)
        .isThrownBy(() -> new FugaziWP().toProviderTypeCoordinates("cannot-parse"));
    assertThatExceptionOfType(NotFound.class)
        .isThrownBy(() -> new BadIdWP().toProviderTypeCoordinates("x"));
  }

  static class BadIdWP implements WitnessProtection {
    @Override
    public String toPrivateId(String publicId) {
      throw new BadId("fugazi");
    }
  }

  static class FugaziWP implements WitnessProtection {
    @Override
    public String toPrivateId(String publicId) {
      return publicId.replace("fugazi:", "");
    }
  }
}
