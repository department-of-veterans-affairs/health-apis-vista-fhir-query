package gov.va.api.health.vistafhirquery.service.controller.observation;

import gov.va.api.health.vistafhirquery.service.controller.VistaIdentifierFormat;

import java.util.Map;

public class ObservationIdentifiers {
    public static Map<Character, VistaIdentifierFormat> FORMATS = Map.of('L', new FormatCompressedObservationLab());
}
