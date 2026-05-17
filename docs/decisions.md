# Design decisions

This document records the main assumptions and choices made while building the claims processing MVP. The goal was a **small, coherent health insurance workflow** that is easy to read, explain, and extend—not a full payer platform.

---

## 1. Health insurance scope (not a generic insurance framework)

**Decision:** Model a **health insurance claims** flow only.

**Why:** The assignment context pointed clearly at healthcare: diagnosis codes, providers, adjudication, and line-level reductions. A generic “any insurance type” design would have spread the model thin and pulled time away from getting adjudication and explanations right.

**Trade-off:** The code is optimized for medical-style claims (member, provider, plan, procedure lines). Auto, property, or life insurance would need different concepts later.

---

## 2. In-memory policies

**Decision:** Store coverage rules in `PolicyCatalog` (in-memory map), not in the database.

**Why:** The learning goal was **adjudication workflow**, domain modeling, and **explanation generation**—not policy administration CRUD. In-memory policies keep lookups fast and setup simple for demos and tests.

**Trade-off:** Plans cannot be changed at runtime without a code/deploy change. The `Policy` / `CoverageRule` structure is ready to move to a database or config service when needed.

---

## 3. Line-level adjudication

**Decision:** Adjudicate each `ClaimLine` independently, then roll up totals and claim outcome on `Claim`.

**Why:** Real claims often have **mixed results**: one service approved, another denied, another paid below the billed amount. Separate lines make **partial approval** natural and let each denial or reduction carry its own explanation.

**Trade-off:** Slightly more structure than a single amount on the claim header—but without lines, partial approval and per-service reasons would be awkward to model and expose in the API.

---

## 4. Minimal claim states

**Decision:** Use only `SUBMITTED` and `ADJUDICATED` at the claim level.

**Why:** The MVP implements **submit → adjudicate → read**. Extra states (e.g. `DRAFT`, `CLOSED`, `IN_REVIEW`) would add transitions and guards without being used end-to-end in this assignment.

**Trade-off:** Lifecycle features like appeals, reopen, or payment-settled would require new states and rules later. Line-level status (`PENDING`, `APPROVED`, `REDUCED`, `DENIED`) still captures adjudication detail.

---

## 5. No generic rule engine

**Decision:** Implement rules as plain Java in `Adjudicator` (lookup plan → check coverage → cap amount → attach adjustments).

**Why:** The adjudication path should stay **readable and interview-friendly**. A DSL, SpEL engine, or pluggable rule framework would add abstraction without being necessary for a few coverage checks.

**Trade-off:** New rules mean code changes, not configuration files. That is acceptable for MVP scope; extract methods or classes per rule when the rule set grows.

---

## 6. Swagger instead of a frontend

**Decision:** Expose the API with **springdoc OpenAPI / Swagger UI** only—no web UI.

**Why:** Swagger is enough to demonstrate submit, adjudicate, partial outcomes, and line explanations interactively, without frontend build time.

**Trade-off:** Non-technical demos rely on Swagger or cURL. A UI can be added later on the same API.

---

## 7. Intentionally out of scope

The following were **deferred** so the implemented path could stay complete and coherent:

| Area | Reason skipped |
|------|----------------|
| Appeals workflow | Wanted to focus on core adjudication flow first |
| Payment processing | Current scope ends at approval/denial decisions |
| Authentication / authorization | Not necessary for local assignment demo |
| Annual deductible tracking | Would require member-level usage tracking logic |
| Prior authorization | Adds another workflow before claim adjudication |
| Fraud detection | Separate concern from claim coverage processing |
| Async processing | Synchronous flow was enough for MVP scope |

**Intent:** Fully implement a **narrow vertical slice**—submit claim, adjudicate lines, return amounts and explanations—rather than partially touch many enterprise features.

---

## Summary

| Topic | Choice |
|-------|--------|
| Domain | Health insurance claims |
| Policies | In-memory catalog |
| Adjudication unit | Per `ClaimLine` |
| Claim workflow | `SUBMITTED` → `ADJUDICATED` |
| Rules | Java in `Adjudicator` |
| API exploration | Swagger UI |
| Scope | Core workflow only; enterprise features deferred |

For entity relationships and adjudication steps, see [domain-model.md](./domain-model.md).  
For running the API, see [README.md](../README.md).
