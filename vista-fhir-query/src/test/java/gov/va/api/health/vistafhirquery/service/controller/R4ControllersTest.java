package gov.va.api.health.vistafhirquery.service.controller;

import static gov.va.api.health.vistafhirquery.service.controller.R4Controllers.parseOrDie;
import static gov.va.api.health.vistafhirquery.service.controller.R4Controllers.verifyAndGetResult;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.WitnessProtection;
import gov.va.api.lighthouse.charon.models.vprgetpatientdata.VprGetPatientData;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class R4ControllersTest {
  @Mock WitnessProtection witnessProtection;

  @Test
  void parseOrDieUnusableIdReturnsNotFound() {
    when(witnessProtection.toPrivateId("garbage")).thenReturn("garbage");
    assertThatExceptionOfType(ResourceExceptions.NotFound.class)
        .isThrownBy(() -> parseOrDie(witnessProtection, "garbage"));
  }

  @Test
  void parseOrDieUsableIdReturnsIdSegment() {
    // So good! So good! So good!
    when(witnessProtection.toPrivateId("sweetCaroline")).thenReturn("sNp1+123+V456");
    var expected =
        SegmentedVistaIdentifier.builder()
            .patientIdentifierType(SegmentedVistaIdentifier.PatientIdentifierType.NATIONAL_ICN)
            .patientIdentifier("p1")
            .vistaSiteId("123")
            .vprRpcDomain(VprGetPatientData.Domains.vitals)
            .vistaRecordId("456")
            .build();
    assertThat(parseOrDie(witnessProtection, "sweetCaroline")).isEqualTo(expected);
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
