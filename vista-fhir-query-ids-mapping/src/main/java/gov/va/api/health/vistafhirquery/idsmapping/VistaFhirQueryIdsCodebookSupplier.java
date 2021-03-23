package gov.va.api.health.vistafhirquery.idsmapping;

import gov.va.api.health.ids.client.EncryptingIdEncoder.Codebook;
import gov.va.api.health.ids.client.EncryptingIdEncoder.Codebook.Mapping;
import gov.va.api.health.ids.client.EncryptingIdEncoder.CodebookSupplier;
import java.util.List;

/** Shared mapping to be used by Vista-Fhir-Query. */
public class VistaFhirQueryIdsCodebookSupplier implements CodebookSupplier {
  @Override
  public Codebook get() {
    return Codebook.builder()
        .map(
            List.of(
                /* Systems */
                Mapping.of("VISTA", "V"),
                Mapping.of("UNKNOWN", "U"),
                /* Resources*/
                Mapping.of("Observation", "OB"),
                Mapping.of("Patient", "PA")))
        .build();
  }
}
