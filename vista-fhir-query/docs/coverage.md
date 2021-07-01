# Coverage --> Insurance Type File (#2.312)
`POST  /Coverage`
___
`PUT   /Coverage/{id}`
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
          "param^FILE^literal^2.312",
          "param^IENS^literal^${ien}",
          "param^FIELDS^literal^.01;.18;.2;3;3.04;4.03;4.06;7.02;8",
          "param^FLAGS^literal^NIE"
        ]
     }
  ]
}
```
___
`GET   /Coverage?patient={ICN}`
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
          "param^IENS^literal^${dfn}",
          "param^FIELDS^literal^.3121*",
          "param^FLAGS^literal^NIE"
        ]
     }
  ]
}
```