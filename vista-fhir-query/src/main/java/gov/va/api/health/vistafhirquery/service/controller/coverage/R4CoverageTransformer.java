package gov.va.api.health.vistafhirquery.service.controller.coverage;

import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.allBlank;
import static gov.va.api.health.vistafhirquery.service.controller.R4Transformers.isBlank;

import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import gov.va.api.health.r4.api.datatypes.Period;
import gov.va.api.health.r4.api.elements.Extension;
import gov.va.api.health.r4.api.elements.Reference;
import gov.va.api.health.r4.api.resources.Coverage;
import gov.va.api.lighthouse.charon.models.iblhsamcmsgetins.GetInsRpcResults;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;

@Builder
public class R4CoverageTransformer {
  GetInsRpcResults vista;

  private List<Coverage.CoverageClass> classes() {
    if (isBlank(vista.insTypeGroupPlan())) {
      return null;
    }
    return List.of(
        Coverage.CoverageClass.builder()
            .value(vista.insTypeGroupPlan().externalValueRepresentation())
            .type(
                CodeableConcept.builder()
                    .coding(
                        List.of(
                            Coding.builder()
                                .system("http://terminology.hl7.org/CodeSystem/coverage-class")
                                .code("group")
                                .build()))
                    .build())
            .build());
  }

  private List<Extension> extensions() {
    List<Extension> extensions = new ArrayList<>();
    if (!isBlank(vista.insTypePharmacyPersonCode())) {
      // ToDo update url
      // ToDo if parse int fails, don't add?
      extensions.add(
          Extension.builder()
              .url("http://va.gov/fhir/StructureDefinition/coverage-pharmacyPersonCode")
              .valueInteger(
                  Integer.parseInt(vista.insTypePharmacyPersonCode().externalValueRepresentation()))
              .build());
    }
    if (extensions.isEmpty()) {
      return null;
    }
    return extensions;
  }

  private Integer order() {
    if (isBlank(vista.insTypeCoordinationOfBenefits())) {
      return null;
    }
    // ToDo if number can't be parsed, log and return null?
    return Integer.parseInt(vista.insTypeCoordinationOfBenefits().externalValueRepresentation());
  }

  private List<Reference> payors() {
    if (isBlank(vista.insTypeInsuranceType())) {
      return null;
    }
    // ToDo this needs more parts that can be identified
    return List.of(
        Reference.builder()
            .reference("Coverage/" + vista.insTypeInsuranceType().internalValueRepresentation())
            .build());
  }

  private Period period() {
    Period period = Period.builder().build();
    if (!isBlank(vista.insTypeEffectiveDateOfPolicy())) {
      period.start(vista.insTypeEffectiveDateOfPolicy().externalValueRepresentation());
    }
    if (!isBlank(vista.insTypeInsuranceExpirationDate())) {
      period.end(vista.insTypeInsuranceExpirationDate().externalValueRepresentation());
    }
    if (allBlank(period.start(), period.end())) {
      return null;
    }
    return period;
  }

  private CodeableConcept relationship() {
    if (isBlank(vista.insTypePtRelationshipHipaa())) {
      return null;
    }
    // ToDo map code
    return CodeableConcept.builder()
        .coding(
            List.of(
                Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/subscriber-relationship")
                    .build()))
        .build();
  }

  private String subscriberId() {
    if (isBlank(vista.insTypeSubscriberId())) {
      return null;
    }
    return vista.insTypeSubscriberId().externalValueRepresentation();
  }

  /** Transform an RPC response to fhir. */
  public Coverage toFhir() {
    return Coverage.builder()
        .extension(extensions())
        .subscriberId(subscriberId())
        .relationship(relationship())
        .period(period())
        .payor(payors())
        .coverageClass(classes())
        .order(order())
        .build();
  }
}
