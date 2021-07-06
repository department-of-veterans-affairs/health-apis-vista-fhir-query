# General Assumptions

- Http Method --> VistA Action
    - POST --> WRITE
    - PUT  --> UPDATE
    - GET  --> READ

- API ID's
    - `vistaSite^fileNumber^ien`
        - If using an RPC that can search by file and ien, patient id isn't needed
    - `patientId^vistaSite^fileNumber^ien`
        - If the RPC can't search by a file, the API will search by patient and filter results using the file/ien

- GET's with `/{id}` will perform the above action(s)
- POST's with no params are new and shouldn't require anything special from the API and will create a brand new insurance plan
- PUT's will require ID's and will need to only perform actions on a specific file (not create anything new)

Proposed RPC Output Formats:
```
{
  "results": [
--- START
    {
      "file": "",
      "ien": "",
      "fields": {
        "${field-number}": {
	  "in": "",
	  "ext": ""
	}
      }
    }
--- or
    {
      "file": "",
      "ien": "",
      "fields": {
        "${field-number}": "${internal}^${external}"
      }
    }
--- or
    {
      "file": "",
      "ien": "",
      "internal": { ... },
      "external": { ... }
    }
--- END
  ]
}

```


## Resources/Mappings
- [Coverage](coverage.md)
- [CoverageEligibilityResponse](coverageEligibilityResponse.md)
- [InsurancePlan](insurancePlan.md)
- [Organization](organization.md)
- [Patient](patient.md)
- [RelatedPerson](relatedPerson.md)
