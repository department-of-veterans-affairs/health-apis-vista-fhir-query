package gov.va.api.health.vistafhirquery.service.controller.organization;

import gov.va.api.health.vistafhirquery.service.controller.RecordCoordinates;

public class OrganizationCoordinates {

  public static RecordCoordinates insuranceCompany(String ien) {
    return RecordCoordinates.builder().file("36").ien(ien).build();
  }

  public static RecordCoordinates payor(String ien) {
    return RecordCoordinates.builder().file("365.12").ien(ien).build();
  }
}
