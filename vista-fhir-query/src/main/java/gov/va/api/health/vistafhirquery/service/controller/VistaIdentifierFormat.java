package gov.va.api.health.vistafhirquery.service.controller;

public interface VistaIdentifierFormat {
  String tryPack(SegmentedVistaIdentifier vis);

  SegmentedVistaIdentifier unpack(String data);

  class FormatString implements VistaIdentifierFormat {
    private static SegmentedVistaIdentifier fromString(String data) {
      String[] segmentParts = data.split("\\+", -1);
      if (segmentParts.length != 3) {
        throw new IllegalArgumentException(
            "SegmentedVistaIdentifier are expected to have 3 parts "
                + "(e.g. patientIdTypeAndId+vistaSiteId+vistaRecordId).");
      }
      if (segmentParts[0].length() < 2) {
        throw new IllegalArgumentException(
            "The first and section of a SegmentedVistaIdentifier must contain "
                + "a type and an identifier value.");
      }
      return SegmentedVistaIdentifier.builder()
          .patientIdentifierType(
              SegmentedVistaIdentifier.PatientIdentifierType.fromAbbreviation(
                  segmentParts[0].charAt(0)))
          .patientIdentifier(segmentParts[0].substring(1))
          .vistaSiteId(segmentParts[1])
          .vistaRecordId(segmentParts[2])
          .build();
    }

    @Override
    public String tryPack(SegmentedVistaIdentifier vis) {
      return vis.toString();
    }

    @Override
    public SegmentedVistaIdentifier unpack(String data) {
      return fromString(data);
    }
  }
}
