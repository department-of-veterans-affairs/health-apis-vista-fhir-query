# Organization ---> Insurance Company File (#36)
POST  /Organization
PUT   /Organization/{id}
GET   /Organization/{id}
GET   /Organization?type=pay
- Payer (#365.12) may be better for this query than file #36
    - If that's not possible, what's the best way to verify a company in file #36 is a payor?