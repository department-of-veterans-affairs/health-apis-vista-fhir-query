package gov.va.api.health.vistafhirquery.service.controller;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@SuppressWarnings("WeakerAccess")
@Controller
public class HomeController {
  private static final YAMLMapper MAPPER = new YAMLMapper();

  private final Resource r4Openapi;

  @Autowired
  public HomeController(@Value("classpath:/r4-openapi.json") Resource r4Openapi) {
    this.r4Openapi = r4Openapi;
  }

  /** The OpenAPI specific content. */
  @SuppressWarnings("WeakerAccess")
  public String openapiContent(Resource openapi) throws IOException {
    try (InputStream is = openapi.getInputStream()) {
      return StreamUtils.copyToString(is, Charset.defaultCharset());
    }
  }

  /**
   * Provide access to the R4 OpenAPI as JSON via RESTful interface. This is also used as the /
   * redirect.
   */
  @GetMapping(
      value = {"r4/", "r4/openapi.json", "r4-openapi.json"},
      produces = "application/json")
  @ResponseBody
  public Object r4OpenapiJson() throws IOException {
    return MAPPER.readValue(openapiContent(r4Openapi), Object.class);
  }
}
