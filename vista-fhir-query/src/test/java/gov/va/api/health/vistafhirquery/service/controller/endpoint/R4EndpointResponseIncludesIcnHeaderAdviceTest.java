package gov.va.api.health.vistafhirquery.service.controller.endpoint;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import gov.va.api.health.r4.api.resources.Endpoint;
import java.util.List;
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
public class R4EndpointResponseIncludesIcnHeaderAdviceTest {

  @Mock R4EndpointController controller;

  private MockMvc mockMvc;

  @Test
  @SneakyThrows
  void endpointResourceIsPatientAgnostic() {
    when(controller.endpointSearch(null))
        .thenReturn(
            Endpoint.Bundle.builder()
                .entry(
                    List.of(
                        Endpoint.Entry.builder()
                            .resource(EndpointSamples.R4.create().endpoint("123"))
                            .build()))
                .build());
    mockMvc
        .perform(get("/r4/Endpoint"))
        .andExpect(MockMvcResultMatchers.header().string("X-VA-INCLUDES-ICN", "NONE"));
  }

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new R4EndpointResponseIncludesIcnHeaderAdvice())
            .build();
  }
}
