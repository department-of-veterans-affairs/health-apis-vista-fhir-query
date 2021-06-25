# Patient --> Patient (#2), Insurance Type (#2.312)
> Fairly sure these are coming from CDW??? Else, the spreadsheet has multiple files going to the patient resource.

`GET   /Patient/{ICN}`
- Will likely need to get a DFN to search file #2
___
`GET   /Patient?identifier=http://hl7.org/fhir/sid/us-ssn%7C{SSN}`
- Search using #2-.09
    - The SSN _should_ be unique globally