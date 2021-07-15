# Patient --> Patient (#2), Insurance Type (#2.312)
> Fairly sure these are coming from CDW??? Else, the spreadsheet has multiple files going to the patient resource.

`GET   /Patient/{ICN}`
- Will need to get a DFN to search file #2
___
`GET   /Patient?identifier=http://hl7.org/fhir/sid/us-ssn%7C{SSN}`
- Use MPI for ssn to icn conversion?
