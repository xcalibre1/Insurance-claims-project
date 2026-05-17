# Health Insurance Claims Processing — MVP

## Project overview

A Spring Boot API for **health insurance claim** intake and adjudication. Members and providers submit claims with service lines; the system applies plan coverage rules, supports partial line-level approval, calculates approved amounts, and returns denial/reduction explanations.

---

## Features

- Submit claims with multiple service lines
- Adjudicate claims line by line (approved / reduced / denied)
- Partial claim outcomes when lines differ
- Coded adjustments explaining denials and reductions
- Claim lifecycle: `SUBMITTED` → `ADJUDICATED`
- Query claims by id or status
- OpenAPI (Swagger) documentation
- In-memory H2 database for local development

---

## Tech stack

| Layer | Technology |
|--------|------------|
| Runtime | Java 21 |
| Framework | Spring Boot 4.0 |
| Persistence | Spring Data JPA, H2 |
| API docs | springdoc-openAPI 3 |
| Build | Gradle |
| Utilities | Lombok, Jakarta Validation |

---

## How to run

**Prerequisites:** JDK 21+

```bash
./gradlew bootRun
```

API base URL: **http://localhost:8080**

```bash
./gradlew test
```

---

## Swagger URL

| Resource | URL |
|----------|-----|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |

---

## H2 console

1. Start the application.
2. Open **http://localhost:8080/h2-console**
3. Connect with:

| Setting | Value |
|---------|--------|
| JDBC URL | `jdbc:h2:mem:claims` |
| User Name | `sa` |
| Password | *(empty)* |

Data is in-memory and cleared when the app stops.

---

## API endpoints

Base path: `/api/v1/claims`

| Method | Path | Status | Description |
|--------|------|--------|-------------|
| `POST` | `/api/v1/claims` | `201` | Submit a claim |
| `POST` | `/api/v1/claims/{id}/adjudicate` | `200` | Adjudicate a submitted claim |
| `GET` | `/api/v1/claims/{id}` | `200` | Get claim by UUID |
| `GET` | `/api/v1/claims?status={status}` | `200` | List claims (`SUBMITTED` or `ADJUDICATED`) |

Common errors: `404` not found, `409` invalid state, `400` validation / unknown plan.

**Default plan for testing:** `PLAN-BASIC` (procedure codes: `99213`, `99214`, `office-visit`, `experimental`).

---

## Example cURL requests

### Submit claim

```bash
curl -s -X POST http://localhost:8080/api/v1/claims \
  -H "Content-Type: application/json" \
  -d '{
    "memberId": "M-1001",
    "providerId": "P-2001",
    "planId": "PLAN-BASIC",
    "serviceFrom": "2026-05-01",
    "serviceTo": "2026-05-01",
    "lines": [{
      "lineNumber": 1,
      "procedureCode": "99213",
      "diagnosisCode": "office visit",
      "serviceDate": "2026-05-01",
      "units": 1,
      "billedAmount": 150.00
    }]
  }'
```

### Adjudicate claim

```bash
curl -s -X POST http://localhost:8080/api/v1/claims/{claimId}/adjudicate
```

### Get claim by id

```bash
curl -s http://localhost:8080/api/v1/claims/{claimId}
```

### List claims by status

```bash
curl -s "http://localhost:8080/api/v1/claims?status=SUBMITTED"
```

### Full flow (submit → adjudicate → get)

```bash
CLAIM_ID=$(curl -s -X POST http://localhost:8080/api/v1/claims \
  -H "Content-Type: application/json" \
  -d '{
    "memberId": "M-1",
    "providerId": "P-1",
    "planId": "PLAN-BASIC",
    "serviceFrom": "2026-05-01",
    "serviceTo": "2026-05-01",
    "lines": [{
      "lineNumber": 1,
      "procedureCode": "99213",
      "serviceDate": "2026-05-01",
      "units": 1,
      "billedAmount": 150.00
    }]
  }' | jq -r '.id')

curl -s -X POST "http://localhost:8080/api/v1/claims/$CLAIM_ID/adjudicate" | jq

curl -s "http://localhost:8080/api/v1/claims/$CLAIM_ID" | jq
```
