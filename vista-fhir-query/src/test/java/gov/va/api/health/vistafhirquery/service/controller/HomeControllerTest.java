package gov.va.api.health.vistafhirquery.service.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(
    controllers = {HomeController.class},
    properties = {
      "vista-fhir-query.public-web-exception-key=whatever",
      "alternate-patient-ids.enabled=false"
    })
@ContextConfiguration(classes = {HomeController.class, MinimalAppConfiguration.class})
public class HomeControllerTest {

  @Autowired private MockMvc mvc;
  @Autowired private HomeController hc;

  @Test
  public void r4OpenapiJson() {
    final String basePath = "/r4";
    final String expectedInfoTitle = "US Core R4";
    testOpenapiPath(basePath + "/openapi.json", expectedInfoTitle);
    testOpenapiPath("/r4-openapi.json", expectedInfoTitle);
    testOpenapiPath(basePath + "/", expectedInfoTitle);
  }

  @SneakyThrows
  private void testOpenapiPath(String path, String expectedInfoTitle) {
    mvc.perform(get(path))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.openapi", equalTo("3.0.1")))
        .andExpect(jsonPath("$.info.title", equalTo(expectedInfoTitle)));
  }
}
