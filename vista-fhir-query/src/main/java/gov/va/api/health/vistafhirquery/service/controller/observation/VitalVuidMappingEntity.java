package gov.va.api.health.vistafhirquery.service.controller.observation;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** Entity for the VitalVuidMapping table. */
@Data
@Entity
@Builder
@Table(schema = "app", name = "vw_Mapped_Values")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@IdClass(VitalVuidMappingCompositeId.class)
public class VitalVuidMappingEntity {

  /** The vuid value represention from VistA (e.g. 4500639). */
  @Id
  @EqualsAndHashCode.Include
  @Column(name = "SourceValue")
  private String sourceValue;

  /** Will be used to filter the results to those needed (e.g. 11). */
  @Id
  @EqualsAndHashCode.Include
  @Column(name = "CodingSystemID")
  private Short codingSystemId;

  /** The code representation for the given system (e.g. 29463-7). */
  @Id
  @EqualsAndHashCode.Include
  @Column(name = "Code")
  private String code;

  /** The fhir display string for the given code (e.g. "Body Weight"). */
  @Column(name = "Display")
  private String display;

  /** The system URI for the code (e.g. http://loinc.org). */
  @Id
  @EqualsAndHashCode.Include
  @Column(name = "URI")
  private String uri;
}
