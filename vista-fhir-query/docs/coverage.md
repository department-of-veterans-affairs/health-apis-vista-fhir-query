# Coverage --> Insurance Type File (#2.312)
`POST  /Coverage`
```
"rpc":{
  "name":"LHS LIGHTHOUSE RPC GATEWAY",
  "context":"LHS RPC CONTEXT",
  "parameters":[
     {
        "array":[
          "debugmode^1",
          "api^crud^coverage",
          "create^2.312^#.01^^4",
          .
          .
          .
        ]
     }
  ]
}
```
___
`PUT   /Coverage/{id}`
```
"rpc":{
  "name":"LHS LIGHTHOUSE RPC GATEWAY",
  "context":"LHS RPC CONTEXT",
  "parameters":[
     {
        "array":[
          "debugmode^1",
          "api^crud^coverage",
          "update^2.312^dfn^${dfn}"
          "update^2.312^#.01^^4",
          .
          .
          .
        ]
     }
  ]
}
```
___
`GET /Coverage/{id}`
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