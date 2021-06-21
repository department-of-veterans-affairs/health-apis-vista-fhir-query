package gov.va.api.health.vistafhirquery.service.controller;

import gov.va.api.health.ids.api.IdentityService;
import gov.va.api.health.ids.api.Registration;
import gov.va.api.health.ids.api.ResourceIdentity;
import gov.va.api.health.vistafhirquery.service.controller.witnessprotection.ProtectedReference;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.stereotype.Component;

/** Endpoint for converting ids between public and private. */
@Component
@Endpoint(id = "ids")
@AllArgsConstructor(onConstructor_ = @Autowired)
public class IdsEndpoint {

  private final IdentityService identityService;

  @ReadOperation
  public List<ResourceIdentity> decode(@Selector String publicId) {
    return identityService.lookup(publicId);
  }

  /** Convert to an encoded ID using the VISTA system. */
  @ReadOperation
  public List<Registration> encode(@Selector String resource, @Selector String privateId) {
    return identityService.register(
        List.of(
            ResourceIdentity.builder()
                .system(ProtectedReference.VISTA_SYSTEM)
                .resource(resource)
                .identifier(privateId)
                .build()));
  }
}
