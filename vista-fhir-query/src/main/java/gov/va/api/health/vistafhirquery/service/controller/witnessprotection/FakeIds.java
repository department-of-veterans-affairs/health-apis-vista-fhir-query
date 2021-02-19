package gov.va.api.health.vistafhirquery.service.controller.witnessprotection;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

public interface FakeIds {

  static String publicIdAttributeFor(String parameter) {
    return FakeIds.class.getName() + ".publicId." + parameter;
  }

  default boolean isPatientIdParameter(String parameter) {
    return patientIdParameters().contains(parameter);
  }

  List<String> patientIdParameters();

  String toPrivateId(String publicId);

  String toPublicId(String privateId);

  class DisabledFakeIds implements FakeIds {

    @Override
    public List<String> patientIdParameters() {
      return List.of();
    }

    @Override
    public String toPrivateId(String publicId) {
      return publicId;
    }

    @Override
    public String toPublicId(String privateId) {
      return privateId;
    }
  }

  @Slf4j
  class MappedFakeIds implements FakeIds {

    private final BiMap<String, String> publicToPrivateIds;

    @Getter private final List<String> patientIdParameters;

    @Builder
    public MappedFakeIds(List<String> patientIdParameters, Map<String, String> publicToPrivateIds) {
      this.patientIdParameters = List.copyOf(patientIdParameters);
      this.publicToPrivateIds = HashBiMap.create(publicToPrivateIds);
    }

    @Override
    public String toPrivateId(String publicId) {
      return publicToPrivateIds.getOrDefault(publicId, publicId);
    }

    @Override
    public String toPublicId(String privateId) {
      return publicToPrivateIds.inverse().getOrDefault(privateId, privateId);
    }
  }
}
