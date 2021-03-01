package gov.va.api.health.vistafhirquery.service.controller.observation;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import gov.va.api.health.r4.api.resources.Observation;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.AlternatePatientIds;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class ObservationResponseIncludesIcnHeaderAdviceTest {
  @Mock R4ObservationController controller;

  @Mock AlternatePatientIds alternatePatientIds;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(
                new R4ObservationResponseIncludesIcnHeaderAdvice(alternatePatientIds))
            .build();
  }

  @Test
  @SneakyThrows
  public void subjectNotPopulated() {
    when(controller.read("123")).thenReturn(Observation.builder().build());
    mockMvc
        .perform(get("/r4/Observation/123"))
        .andExpect(MockMvcResultMatchers.header().string("X-VA-INCLUDES-ICN", "NONE"));
  }

  @Test
  @SneakyThrows
  public void subjectPopulated() {
    when(controller.read("123")).thenReturn(ObservationVitalSamples.Fhir.create().weight());
    when(alternatePatientIds.toPublicId(eq("p1"))).thenReturn("p1");
    mockMvc
        .perform(get("/r4/Observation/123"))
        .andExpect(MockMvcResultMatchers.header().string("X-VA-INCLUDES-ICN", "p1"));
  }
}
