# Self review

Honest assessment of what worked, what is rough, and what was deliberately left out of this health insurance claims MVP.

---

## What went well

- **Adjudication flow** — The end-to-end path (submit → adjudicate → read) stayed fairly clean and easy to reason about.
- **Claim vs ClaimLine** — Splitting the header from service lines made **partial approvals** and mixed outcomes natural instead of forcing a single result on the whole claim.
- **Explanations** — The adjustment model makes API responses understandable: not just amounts and statuses, but *why* a line was denied or reduced.
- **Controlled scope** — Focused on a realistic health insurance slice instead of a generic insurance platform in a short timeframe.
- **Synchronous flow** — Keeping everything in-process simplified development, debugging, and manual testing during the assignment.
- **Swagger** — OpenAPI UI made it easy to exercise different claim scenarios without building a frontend.

---

## Strengths of the implementation

| Area | What works |
|------|------------|
| Layering | Controller, service, adjudicator, and policy logic have clear responsibilities |
| Outcome derivation | Claim `outcome` is computed from line statuses—not updated in multiple places by hand |
| API boundary | DTOs are used; JPA entities are not exposed directly |
| Adjudication code | Rules live in readable Java, not behind heavy abstraction |
| Realistic scenarios | Partial approval, reductions, exclusions, and denial explanations are supported |
| Tests | Coverage emphasizes adjudication behavior and outcomes, not only HTTP status codes |

---

## Weak areas / rough edges

- **Coverage rules** — Simplified compared to production insurance (no tiers, networks, bundling, etc.).
- **Policies** — Hardcoded in-memory; not manageable by an admin or external config.
- **Error handling** — Adequate for MVP (`404`, `409`, `400`, validation) but not exhaustive.
- **Post-approval** — No payment or remittance workflow after a line is approved.
- **Structure** — Package and naming could be tightened with another pass.
- **Scale** — Assumes small, synchronous load; not tuned for high volume or concurrency.

---

## Technical debt

| Item | Notes |
|------|--------|
| Policy storage | Should move to database or config service when plans need to change without deploy |
| Rule configuration | Coverage caps and exclusions are in code today |
| Audit trail | No history of who changed policy or why a claim was adjudicated a certain way |
| Concurrency | No optimistic locking or versioning on claim updates |
| Validation | Lightweight on purpose; production would need richer member/plan/date checks |
| Async / events | Skipped to keep the workflow small; would matter at higher volume |

---

## Features intentionally deferred

These were **out of scope** so the core workflow could be complete:

- Appeals workflow  
- Payment / remittance processing  
- Authentication / authorization  
- Annual deductible tracking  
- Prior authorization workflow  
- Fraud detection  
- Async / event-driven processing  
- Provider network validation  
- Eligibility verification  

---

## Future improvements

If this were extended beyond an assignment MVP:

1. **Appeals / reconsideration** — Challenge or reopen lines after initial adjudication.  
2. **Member benefits** — Deductible accumulators, copays, and plan limits.  
3. **Persistent policies** — Store plans and coverage rules in DB or configuration.  
4. **Richer coverage logic** — More realistic exclusions, bundling, and benefit tiers.  
5. **Audit / history** — Record adjudication inputs, outputs, and policy version used.  
6. **Async processing** — Queue adjudication when claim volume grows.  
7. **Security** — Authentication and role-based access for providers, members, and admins.  
8. **Eligibility & network** — Verify member active on service date and provider in network.

---

## Closing note

The implementation prioritizes **clarity and a believable insurance workflow** over enterprise completeness. The adjudication logic is intentionally straightforward so it can be explained and extended; the main gaps are operational concerns (policy admin, payments, security, scale) rather than the core submit → adjudicate → explain path.

**Related docs**

- [domain-model.md](./domain-model.md) — entities, states, adjudication flow  
- [decisions.md](./decisions.md) — why key design choices were made  
- [README.md](../README.md) — how to run and call the API
