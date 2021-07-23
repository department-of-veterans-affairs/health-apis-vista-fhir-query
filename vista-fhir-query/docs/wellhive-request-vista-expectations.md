# General Assumptions

#### Http Method to API Action
- POST: write
- PUT: update
- GET: read

### Reading from Vista
#### Read RPC Output Format:
```
{
  "results": [
    {
      "file": "${fileNumber}",
      "ien": "${recordIen}",
      "fields": {
        "${fieldNumber}": {
          "in": "${internalValueRepresentation}",
          "ext": "${externalValueRepresentation}"
        }
      }
    }
  ]
}
```

### Writing to Vista

- Writing to Vista can be done sequentially, but when creating data it must be done in order so that the new iens can
  be pointed to when creating/updating the next record
    - Creates a failure condition for create/updates.
    - When Vista fails to write/update, the API will return a 400. Is there a way to identify what failed without 
      potentially leaking PII to the consumer?
- POST's with no params are new and shouldn't require anything special from the API and will create a brand-new
  insurance plan
- PUT's will require ID's and will need to only perform actions on a specific file (not create anything new)


#### Proposed Write RPC Input Format:
> Note: The ien field will only be used for updates to an existing record. When creating records, the ien field will
> be null.
```
{
  "${fileNumber}": [
    {
      "mode": "regexp=(CREATE|UPDATE)",
      "ien": "${recordIen}",
      "${fieldNumber}": "${internalValueRepresentation}"
    }
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
