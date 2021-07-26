package gov.va.api.health.vistafhirquery.service.controller;

import static gov.va.api.health.vistafhirquery.service.controller.R4Controllers.patientTypeCoordinatesOrDie;
import static gov.va.api.health.vistafhirquery.service.controller.R4Controllers.verifyAndGetResult;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.WitnessProtection;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class R4ControllersTest {
  @Mock WitnessProtection witnessProtection;

  @Test
  void patientTypeCoordinatesOrDieUnusableIdReturnsNotFound() {
    when(witnessProtection.toPrivateId("garbage")).thenReturn("garbage");
    assertThatExceptionOfType(ResourceExceptions.NotFound.class)
        .isThrownBy(() -> patientTypeCoordinatesOrDie(witnessProtection, "garbage"));
  }

  @Test
  void patientTypeCoordinatesOrDieUsableIdReturnsIdSegment() {
    when(witnessProtection.toPrivateId("patCoord")).thenReturn("p1+123+456");
    var expected = PatientTypeCoordinates.builder().icn("p1").siteId("123").recordId("456").build();
    assertThat(patientTypeCoordinatesOrDie(witnessProtection, "patCoord")).isEqualTo(expected);
  }

  @Test
  void verifyAndGetResultMoreThanOneResultThrowsExpectationFailed() {
    assertThatExceptionOfType(ResourceExceptions.ExpectationFailed.class)
        .isThrownBy(() -> verifyAndGetResult(List.of("1", "2"), "publicId"));
  }

  @Test
  void verifyAndGetResultNoResultsThrowsNotFound() {
    assertThatExceptionOfType(ResourceExceptions.NotFound.class)
        .isThrownBy(() -> verifyAndGetResult(List.of(), "publicId"));
  }

  @Test
  void verifyAndGetResultOneResultReturnsResult() {
    assertThat(verifyAndGetResult(List.of("1"), "publicId")).isEqualTo("1");
  }
}
