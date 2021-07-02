package gov.va.api.health.vistafhirquery.service.api;

import gov.va.api.health.r4.api.resources.Coverage;
import gov.va.api.health.r4.api.resources.OperationOutcome;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

public interface R4CoverageApi {

  @Operation(
      summary = "Coverage Read",
      description = "http://hl7.org/fhir/us/carin/StructureDefinition/carin-bb-coverage",
      tags = {"Coverage"})
  @GET
  @Path("Coverage/{id}")
  @ApiResponse(
      responseCode = "200",
      description = "Record found",
      content =
          @Content(
              mediaType = "application/fhir+json",
              schema = @Schema(implementation = Coverage.class)))
  @ApiResponse(
      responseCode = "400",
      description = "Bad request",
      content =
          @Content(
              mediaType = "application/fhir+json",
              schema = @Schema(implementation = OperationOutcome.class)))
  @ApiResponse(
      responseCode = "404",
      description = "Not found",
      content =
          @Content(
              mediaType = "application/fhir+json",
              schema = @Schema(implementation = OperationOutcome.class)))
  Coverage coverageRead(@Parameter(in = ParameterIn.PATH, name = "id", required = true) String id);

  @Operation(
      summary = "Coverage Search",
      description = "http://hl7.org/fhir/us/carin/StructureDefinition/carin-bb-coverage",
      tags = {"Coverage"})
  @GET
  @Path("Coverage")
  @ApiResponse(
      responseCode = "200",
      description = "Record found",
      content =
          @Content(
              mediaType = "application/fhir+json",
              schema = @Schema(implementation = Coverage.Bundle.class)))
  @ApiResponse(
      responseCode = "404",
      description = "Not found",
      content =
          @Content(
              mediaType = "application/fhir+json",
              schema = @Schema(implementation = OperationOutcome.class)))
  @ApiResponse(
      responseCode = "400",
      description = "Bad request",
      content =
          @Content(
              mediaType = "application/fhir+json",
              schema = @Schema(implementation = OperationOutcome.class)))
  Coverage.Bundle coverageSearch(
      HttpServletRequest request,
      @Parameter(in = ParameterIn.QUERY, name = "patient") String icn,
      @Parameter(in = ParameterIn.QUERY, name = "_count") @DefaultValue("30") Integer count);
}
