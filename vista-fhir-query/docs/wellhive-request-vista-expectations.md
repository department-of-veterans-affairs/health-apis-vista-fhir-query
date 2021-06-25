# General Assumptions

- Http Method --> VistA Action
    - POST --> WRITE
    - PUT  --> UPDATE
    - GET  --> READ

- API ID's
    - vistaSite^fileNumber^ien
        - If using an RPC that can search by file and ien, patient id isn't needed
    - patientId^vistaSite^fileNumber^ien
        - If the RPC can't search by a file, the API will search by patient and filter results using the file/ien

- POST's with no params are new and shouldn't require anything special from the API and will create a brand new insurance plan
- PUT's will require ID's and will need to only perform actions on a specific file (not create anything new)
- GET's with `/{id}` will perform the above action(s)


- [Coverage](coverage.md)
- [CoverageEligibilityResponse](coverageEligibilityResponse.md)
- [InsurancePlan](insurancePlan.md)
- [Organization](organization.md)
- [Patient](patient.md)
- [RelatedPerson](relatedPerson.md)
