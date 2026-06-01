# Beer Catalogue API

A full-stack application to manage a catalogue of beers and manufacturers. The backend exposes a REST API built with Java 21 and Spring Boot 3.3.5, secured with role-based access control. The frontend is a Next.js 14 app that consumes the API. Both run locally via Docker Compose or can be deployed to a Kubernetes cluster backed by an AWS RDS PostgreSQL database provisioned with Terraform.

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

Postman collection available in `beer-catalogue.postman_collection.json`.

## Cloud Deployment (AWS + Kubernetes)

Two scripts automate the full cloud setup end-to-end:

```bash
# Spin up everything: RDS on AWS + deploy to local k8s cluster
./scripts/demo-up.sh

# Tear down everything when done
./scripts/demo-down.sh
```

`demo-up.sh` does the following in order:
1. Runs `terraform apply` to provision an RDS PostgreSQL 16 instance on AWS (`eu-central-1`)
2. Reads the RDS endpoint from Terraform output
3. Creates a Kubernetes Secret with the database credentials (never written to disk)
4. Deploys backend and frontend to the local cluster (Rancher Desktop)
5. Waits for pods to be ready and prints the access URLs

`demo-down.sh` removes all Kubernetes resources and runs `terraform destroy`.

**Search & Pagination** — beers and manufacturers support `page`, `size`, `sort` query params. Beers also support filtering by `name`, `type`, `abv`, and `manufacturerId`.

**AWS-Hosted Database** — credentials are injected at deploy time via Kubernetes Secrets — nothing hardcoded in the manifests committed to the repo.
