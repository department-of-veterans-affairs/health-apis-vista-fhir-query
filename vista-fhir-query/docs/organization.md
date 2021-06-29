# Organization ---> Insurance Company File (#36)
`POST  /Organization`
___
`PUT   /Organization/{id}`
___
`GET   /Organization/{id}`
- RPC Request:
```
"rpc":{
  "name":"LHS LIGHTHOUSE RPC GATEWAY",
  "context":"LHS RPC CONTEXT",
  "parameters":[
     {
        "array":[
          "debugmode^1",
          "api^manifest^gets",
          "param^FILE^literal^${file-insCo-or-payor}",
          "param^IENS^literal^${file-ien}",
          "param^FIELDS^literal^${fields-depending-on-file}",
          "param^FLAGS^literal^NIER",
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
___
`GET   /Organization?type=pay`
- Payer (#365.12) may be better for this query than file #36
    - If that's not possible, what's the best way to verify a company in file #36 is a payor?
- RPC Request:
```
"rpc":{
  "name":"LHS LIGHTHOUSE RPC GATEWAY",
  "context":"LHS RPC CONTEXT",
  "parameters":[
     {
        "array":[
          "debugmode^1",
          "api^manifest^list",
          "param^FILE^literal^${file-insCo-or-payor}",
          "param^IENS^literal^",
          "param^FIELDS^literal^${fields-depending-on-file}",
          "param^FLAGS^literal^NIER",
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
