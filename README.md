# Beer Catalogue API

A full-stack application to manage a catalogue of beers and manufacturers. The backend exposes a REST API built with Java 21 and Spring Boot 3.3.5, secured with role-based access control. The frontend is a Next.js 14 app that consumes the API. Both run locally via Docker Compose or can be deployed to a Kubernetes cluster backed by an AWS RDS PostgreSQL database provisioned with Terraform.

## Setup

**Requirements:** Java 21, Maven 3.9+, Docker

There are two ways to run the project depending on what you want to test:

### Option 1 — Local (dev profile, H2 in-memory database)

No external dependencies needed. Spring Boot starts with the `dev` profile, creates all tables automatically, and seeds sample data (manufacturers, beers, users) on first run.

```bash
docker compose up --build
```

- API: http://localhost:8080
- Frontend: http://localhost:3000
- Swagger UI: http://localhost:8080/swagger-ui.html

### Option 2 — Cloud (prod profile, AWS RDS PostgreSQL)

Requires AWS CLI configured with a `haufe` profile and Rancher Desktop running locally.

```bash
./scripts/demo-up.sh
```

This provisions the RDS instance on AWS with Terraform, then deploys both services to the local Kubernetes cluster using the `prod` Spring profile connected to the real database.

### Run locally without Docker

```bash
cd backend
mvn spring-boot:run
```

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
| admin | admin123 | ADMIN | full access |
| heineken_user | heineken123 | MANUFACTURER | manage Heineken beers only |
| guinness_user | guinness123 | MANUFACTURER | manage Guinness beers only |
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
