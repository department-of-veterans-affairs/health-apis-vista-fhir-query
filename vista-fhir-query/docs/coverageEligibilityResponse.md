# CoverageEligibilityResponse --> Plan Coverage Limitations (#355.32)
`POST  /CoverageEligibilityResponse`
___
`GET   /CoverageEligibilityResponse?patient={ICN}`
1. Get the InsuranceType File (#2.312) by patient
- RpcRequest:
```
"rpc":{
  "name":"LHS LIGHTHOUSE RPC GATEWAY",
  "context":"LHS RPC CONTEXT",
  "parameters":[
     {
        "array":[
          "debugmode^1",
          "api^manifest^gets",
          "param^FILE^literal^2",
          "param^IENS^literal^${patientDfn}",
          "param^FIELDS^literal^.3121*",
          "param^FLAGS^literal^NIE"
        ]
     }
  ]
}
```
2. Collect InsuranceCompany File (#355.3) IENs from response (field .18)
3. List the Plan Coverage Limitations File (#355.32)
- RpcRequest:
```
"rpc":{
  "name":"LHS LIGHTHOUSE RPC GATEWAY",
  "context":"LHS RPC CONTEXT",
  "parameters":[
     {
        "array":[
          "debugmode^1",
          "api^manifest^list",
          "param^FILE^literal^355.32",
          "param^IENS^literal^",
          "param^FIELDS^literal^@;.01IE;.02IE;.03IE;.04IE;2IE",
          "param^FLAGS^literal^",
          "param^NUMBER^literal^",
          "param^FROM^literal^",
          "param^PART^literal^",
          "param^INDEX^literal^",
          "param^SCREEN^literal^",
          "param^ID^literal^"
        ]
     }
  ]
}
```
4. Filter the Plan Coverage Limitations using field (.01) and the Insurance plan iens from step 2
___
`GET   /CoverageEligibilityResponse?patient={ICN}&created=gt2021&insurer={org-reference}`
1. Perform all steps for search by patient
2. Post-process filtering:
    - `created` can be filtered using Date Entered (355.32-1.01)
        - If edits matter, filter using Date Last Edited (355.32-1.03)
    - `insurer` can be filtered using Plan (355.32-.01)
