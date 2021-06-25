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

# Coverage --> Insurance Type File (#2.312)
POST  /Coverage
PUT   /Coverage/{id}
GET   /Coverage?patient={ICN}
- Insurance Type is a sub-file of the Patient file
    - Will likely need to get a DFN to search file #2 to get results

# CoverageEligibilityResponse --> Plan Coverage Limitations (#355.32)
POST  /CoverageEligibilityResponse
GET   /CoverageEligibilityResponse?patient={ICN}
- File 355.32 doesn't look to have any way to filter by patient
    - We will need to get insurance info by patient and get the data using the pointers
GET   /CoverageEligibilityResponse?patient={ICN}&created=gt2021&insurer={org-reference}
- `created` can be filtered using Date Entered (355.32-1.01)
    - If edits matter, filter using Date Last Edited (355.32-1.03)
- `insurer` can be filtered using Plan (355.32-.01)

# InsurancePlan --> Group Insurance Plan (#355.3) 
POST  /InsurancePlan
PUT   /InsurancePlan/{id}
GET   /InsurancePlan/{id}
GET   /InsurancePlan?identifier={group-plan-number}
- Filter using ien (the plan is populated using the group plan file)?
- Filter using Group Number (355.3-2.02)?

# Organization ---> Insurance Company File (#36)
POST  /Organization
PUT   /Organization/{id}
GET   /Organization/{id}
GET   /Organization?type=pay
- Payer (#365.12) may be better for this query than file #36
    - If that's not possible, what's the best way to verify a company in file #36 is a payor?

# Patient --> Patient (#2), Insurance Type (#2.312)
> Fairly sure these are coming from CDW??? Else, the spreadsheet has multiple files going to the patient resource.
GET   /Patient/{ICN}
- Will likely need to get a DFN to search file #2
GET   /Patient?identifier=http://hl7.org/fhir/sid/us-ssn%7C{SSN}
- Search using #2-.09
    - The SSN _should_ be unique globally

# RelatedPerson --> ???
> My guess here is patient, but I'm not sure yet how to determine a patient's related other patients. Jay only maps one
field to RelatedPerson.
POST  /RelatedPerson
GET   /RelatedPerson/{id}
GET   /RelatedPerson?patient={ICN}
