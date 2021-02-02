package gov.va.api.health.vistafhirquery.service.controller;

import gov.va.api.lighthouse.vistalink.api.RpcDetails;
import gov.va.api.lighthouse.vistalink.api.RpcResponse;

public interface VistalinkApiClient {

  RpcResponse requestForPatient(String patient, RpcDetails rpcDetails);

  RpcResponse requestForVistaSite(String vistaSite, RpcDetails rpcDetails);
}
