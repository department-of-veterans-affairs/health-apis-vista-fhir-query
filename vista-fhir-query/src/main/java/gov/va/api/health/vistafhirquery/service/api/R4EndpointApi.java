package gov.va.api.health.vistafhirquery.service.api;

import gov.va.api.health.r4.api.resources.Endpoint;
import gov.va.api.health.r4.api.resources.OperationOutcome;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

public interface R4EndpointApi {
  @Operation(
      summary = "Endpoint Search",
      description = "https://hl7.org/fhir/R4/endpoint.html",
      tags = {"Endpoint"})
  @GET
  @Path("Endpoint")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Record found",
        content = {
          @Content(
              schema = @Schema(implementation = Endpoint.Bundle.class),
              mediaType = "application/fhir+json")
        }),
    @ApiResponse(
        responseCode = "400",
        description = "Bad request",
        content = {
          @Content(
              mediaType = "application/fhir+json",
              schema = @Schema(implementation = OperationOutcome.class))
        }),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = {
          @Content(
              mediaType = "application/fhir+json",
              schema = @Schema(implementation = OperationOutcome.class))
        }),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden",
        content = {
          @Content(
              mediaType = "application/fhir+json",
              schema = @Schema(implementation = OperationOutcome.class))
        }),
    @ApiResponse(
        responseCode = "404",
        description = "Not found",
        content = {
          @Content(
              mediaType = "application/fhir+json",
              schema = @Schema(implementation = OperationOutcome.class))
        }),
    @ApiResponse(
        responseCode = "500",
        description = "Server Error",
        content = {
          @Content(
              mediaType = "application/fhir+json",
              schema = @Schema(implementation = OperationOutcome.class))
        })
  })
  Endpoint.Bundle endpointSearch(
      @Parameter(
              in = ParameterIn.QUERY,
              name = "status",
              description = "The status of the endpoint.",
              example = "active")
          String status);
}
