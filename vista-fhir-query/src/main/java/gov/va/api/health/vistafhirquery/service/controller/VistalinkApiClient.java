package gov.va.api.health.vistafhirquery.service.controller;

import gov.va.api.lighthouse.charon.api.RpcRequest;
import gov.va.api.lighthouse.charon.api.RpcResponse;
import gov.va.api.lighthouse.charon.api.RpcVistaTargets;
import gov.va.api.lighthouse.charon.models.TypeSafeRpcRequest;
import java.util.List;

/** Client for using the Vistalink API. */
public interface VistalinkApiClient {
  RpcResponse makeRequest(RpcRequest request);

  default RpcResponse requestForPatient(String patient, TypeSafeRpcRequest rpcRequestDetails) {
    return requestForTarget(
        RpcVistaTargets.builder().forPatient(patient).build(), rpcRequestDetails);
  }

  RpcResponse requestForTarget(RpcVistaTargets target, TypeSafeRpcRequest rpcRequestDetails);

  default RpcResponse requestForVistaSite(String vistaSite, TypeSafeRpcRequest rpcRequestDetails) {
    return requestForTarget(
        RpcVistaTargets.builder().include(List.of(vistaSite)).build(), rpcRequestDetails);
  }
}
