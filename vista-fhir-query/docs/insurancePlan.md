# InsurancePlan --> Group Insurance Plan (#355.3)
`POST  /InsurancePlan`
___
`PUT   /InsurancePlan/{id}`
___
`GET   /InsurancePlan/{id}`
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
          "param^FILE^literal^355.3",
          "param^IENS^literal^${ien}",
          "param^FIELDS^literal^.01;.05;.06;.07;.08;.09;.12;.14;.15;.16;.17;2.01;2.02;6.01;6.02;6.03",
          "param^FLAGS^literal^NIE"
        ]
     }
  ]
}
```
___
`GET   /InsurancePlan?identifier={group-plan-number}`
- Post-process using Group Number (field 2.02) in the response
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
          "param^FILE^literal^355.3",
          "param^IENS^literal^",
          "param^FIELDS^literal^@;.01IE;.05IE;.06IE;.07IE;.08IE;.09IE;.12IE;.14IE;.15IE;.16IE;.17IE;2.01IE;2.02IE;6.01IE;6.02IE;6.03IE",
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
