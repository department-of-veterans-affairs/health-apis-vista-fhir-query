# CoverageEligibilityResponse --> Plan Coverage Limitations (#355.32)
`POST  /CoverageEligibilityResponse`
___
`GET   /CoverageEligibilityResponse?patient={ICN}`
- File 355.32 doesn't look to have any way to filter by patient
    - We will need to get insurance info by patient and get the data using the pointers
___
`GET   /CoverageEligibilityResponse?patient={ICN}&created=gt2021&insurer={org-reference}`
- `created` can be filtered using Date Entered (355.32-1.01)
    - If edits matter, filter using Date Last Edited (355.32-1.03)
- `insurer` can be filtered using Plan (355.32-.01)