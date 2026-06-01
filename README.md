# Beer Catalogue API

REST API to manage a catalogue of beers and manufacturers, built with Java 21 and Spring Boot 3.3.5.

## Setup

**Requirements:** Java 21, Maven 3.9+, Docker

### Run with Docker (recommended)

```bash
docker compose up --build
```

- API: http://localhost:8080
- Frontend: http://localhost:3000
- Swagger UI: http://localhost:8080/swagger-ui.html

### Run locally

```bash
cd backend
mvn spring-boot:run
```

Uses H2 in-memory database by default (dev profile). No extra setup needed.

### Run tests

```bash
cd backend
mvn test
```

## Design Decisions

- **Layered architecture**: Controller → Service → Repository. Each layer has a single responsibility and is independently testable.
- **DTOs over entities**: Request/response objects are separate from JPA entities to avoid leaking persistence details to the API contract.
- **Two Spring profiles**: `dev` uses H2 for easy local development; `prod` connects to PostgreSQL via environment variables, so no credentials are hardcoded.
- **Role-based security**: Spring Security with three roles — `ANONYMOUS` (read-only), `MANUFACTURER` (manage own beers), `ADMIN` (full access). HTTP Basic auth for simplicity.
- **Pagination and search**: All listing endpoints accept `page`, `size`, and `sort` parameters via Spring Data `Pageable`. Beers support filtering by `name`, `type`, `abv`, and `manufacturerId`.

## Test Credentials

| Username | Password | Role | Access |
|----------|----------|------|--------|
| admin | admin | ADMIN | full access |
| manufacturer | manufacturer | MANUFACTURER | manage own beers |
| _(no auth)_ | | ANONYMOUS | read-only |

API collection (Bruno/Postman) available in the `Beer Catalogue API/` folder.

## Cloud Features

**Search & Pagination** — beers and manufacturers support `page`, `size`, `sort` query params. Beers also support filtering by `name`, `type`, `abv`, and `manufacturerId`.

**Cloud Deployment** — Kubernetes manifests in `k8s/` deploy both services to a local cluster (Rancher Desktop). Run `./scripts/demo-up.sh` to provision everything end-to-end.

**AWS-Hosted Database** — Terraform config in `terraform/` provisions an RDS PostgreSQL 16 instance on AWS (`eu-central-1`). Credentials are injected at deploy time via Kubernetes Secrets — nothing hardcoded in the manifests committed to the repo.
