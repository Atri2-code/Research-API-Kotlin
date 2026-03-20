# Research API — Kotlin + Spring Boot

A RESTful microservice built with Kotlin and Spring Boot, exposing CRUD
endpoints for a research paper database backed by PostgreSQL.

Directly mirrors AlphaSights' backend engineering stack: Kotlin microservices
with Postgres, structured around clean separation of concerns
(controller → service → repository).

---

## Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/research` | Get all papers (sorted by date desc) |
| GET | `/api/research/{id}` | Get paper by ID |
| POST | `/api/research` | Create new paper |
| PUT | `/api/research/{id}` | Update existing paper |
| DELETE | `/api/research/{id}` | Delete paper |
| GET | `/api/research/search?q=` | Full-text search (title + author) |
| GET | `/api/research/filter` | Filter by topic, status, minCitations |
| GET | `/api/research/topic/{topic}` | Get papers by topic |
| GET | `/api/research/stats/topics` | Citation and paper count by topic |
| GET | `/api/research/stats/summary` | Overall summary statistics |

---

## Example requests

**Get all papers:**
```bash
curl http://localhost:8080/api/research
```

**Create a paper:**
```bash
curl -X POST http://localhost:8080/api/research \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Transformer Scaling Laws",
    "author": "Hoffmann, J.",
    "topic": "Technology",
    "publishedDate": "2024-03-01",
    "citations": 312,
    "status": "Published",
    "impact": "High"
  }'
```

**Search:**
```bash
curl "http://localhost:8080/api/research/search?q=machine+learning"
```

**Filter:**
```bash
curl "http://localhost:8080/api/research/filter?topic=Healthcare&status=Published&minCitations=100"
```

**Summary stats:**
```bash
curl http://localhost:8080/api/research/stats/summary
```

---

## Sample response

```json
{
  "success": true,
  "total": 15,
  "data": [
    {
      "id": 1,
      "title": "AI Safety and Alignment in Large Language Models",
      "author": "Chen, L.",
      "topic": "AI Safety",
      "publishedDate": "2024-03-15",
      "citations": 142,
      "status": "Published",
      "impact": "High",
      "abstract": null,
      "doi": null
    }
  ]
}
```

---

## How to run

### Option A — With PostgreSQL

**1. Create database:**
```sql
CREATE DATABASE research_db;
```

**2. Update credentials in `application.properties`**

**3. Run:**
```bash
./gradlew bootRun
```

### Option B — H2 in-memory (no Postgres needed)

In `application.properties`, comment out the PostgreSQL lines and
uncomment the H2 lines. Then:
```bash
./gradlew bootRun
```

The app seeds 15 sample papers on startup automatically.

---

## Project structure

```
src/main/kotlin/com/atrijahaldar/researchapi/
├── ResearchApiApplication.kt   ← Spring Boot entry point
├── ResearchPaper.kt            ← Entity, DTOs, Repository
├── ResearchPaperService.kt     ← Business logic, validation
├── ResearchPaperController.kt  ← REST endpoints, exception handler
└── DataSeeder.kt               ← Seed data on startup
```

## Architecture decisions

**Controller → Service → Repository pattern:** Separates HTTP concerns
from business logic from data access. Each layer has a single responsibility.

**DTOs over entity exposure:** `ResearchPaperRequest` and `ResearchPaperResponse`
decouple the API contract from the database schema — the entity can change
without breaking the API.

**Validation in service layer:** Business rules (valid statuses, non-negative
citations) live in the service, not the controller, so they apply regardless
of how the service is called.

**H2 fallback:** Allows running locally without a Postgres installation,
making the project easy for reviewers to run immediately.

---

## Tech stack

- **Kotlin** 1.9 + **Spring Boot** 3.2
- **Spring Data JPA** + **Hibernate**
- **PostgreSQL** (production) / **H2** (local dev)
- **Gradle** (Kotlin DSL)

---

## Connecting to the React frontend

This API is designed to serve the companion
[research-dashboard](https://github.com/Atri2-code/research-dashboard)
React frontend.

CORS is configured to allow requests from `http://localhost:3000`.

---

## Author

Atrija Haldar
[LinkedIn](https://www.linkedin.com/in/atrija-haldar-196a3b221/)
MSc Engineering, Technology and Business Management — University of Leeds
