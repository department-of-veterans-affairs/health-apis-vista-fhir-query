package gov.va.api.health.vistafhirquery.service.controller.witnessprotection;

/** Interface for translating publicId to a privateId. */
public interface WitnessProtection {

  String toPrivateId(String publicId);
}
