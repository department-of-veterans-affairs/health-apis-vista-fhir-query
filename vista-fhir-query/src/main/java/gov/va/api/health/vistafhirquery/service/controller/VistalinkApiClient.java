package gov.va.api.health.vistafhirquery.service.controller;

import gov.va.api.lighthouse.charon.api.RpcResponse;
import gov.va.api.lighthouse.charon.models.TypeSafeRpcRequest;

public interface VistalinkApiClient {

  RpcResponse requestForPatient(String patient, TypeSafeRpcRequest rpcRequestDetails);

  RpcResponse requestForVistaSite(String vistaSite, TypeSafeRpcRequest rpcRequestDetails);
}
