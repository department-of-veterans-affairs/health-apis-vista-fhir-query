package gov.va.api.health.vistafhirquery.service.controller.organization;

import gov.va.api.health.r4.api.resources.Organization;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
public class R4OrganizationResponseIncludesIcnHeaderAdviceTest {

  @Mock R4OrganizationController controller;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new R4OrganizationResponseIncludesHeaderAdvice())
            .build();
  }

  @Test
  @SneakyThrows
  void organizationResourceIsPatientAgnostic() {
    when(controller.organizationRead("o1"))
        .thenReturn(Organization.builder().id("123").active(false).build());
    mockMvc
        .perform(get("/r4/Organization/o1"))
        .andExpect(MockMvcResultMatchers.header().string("X-VA-INCLUDES-ICN", "NONE"));
  }
}
