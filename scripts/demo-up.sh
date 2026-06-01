#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(dirname "$SCRIPT_DIR")"
TERRAFORM_DIR="$ROOT_DIR/terraform"
K8S_DIR="$ROOT_DIR/k8s"

# ── Colors ──────────────────────────────────────────────────────────────────
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

log()  { echo -e "${GREEN}[✔]${NC} $*"; }
info() { echo -e "${YELLOW}[→]${NC} $*"; }
err()  { echo -e "${RED}[✘]${NC} $*" >&2; }

# ── Config (override via env vars) ──────────────────────────────────────────
DB_USERNAME="${DB_USERNAME:-beeradmin}"
DB_PASSWORD="${DB_PASSWORD:-BeerAdmin2026!}"
DB_NAME="${DB_NAME:-beercatalogue}"
K8S_CONTEXT="${K8S_CONTEXT:-rancher-desktop}"
BACKEND_IMAGE="${BACKEND_IMAGE:-gheinz/beer-catalogue-backend:latest}"
FRONTEND_IMAGE="${FRONTEND_IMAGE:-gheinz/beer-catalogue-frontend:latest}"

# ── 1. Terraform apply ───────────────────────────────────────────────────────
info "Provisioning AWS RDS with Terraform..."
cd "$TERRAFORM_DIR"

if [ ! -d ".terraform" ]; then
  info "Running terraform init..."
  terraform init -upgrade
fi

terraform apply -auto-approve

# ── 2. Read outputs ──────────────────────────────────────────────────────────
RDS_HOST=$(terraform output -raw rds_host)
RDS_ENDPOINT=$(terraform output -raw rds_endpoint)
DB_URL="jdbc:postgresql://${RDS_ENDPOINT}/${DB_NAME}?sslmode=require"

log "RDS endpoint: $RDS_ENDPOINT"
log "DB URL: $DB_URL"

# ── 3. Switch k8s context ────────────────────────────────────────────────────
info "Switching to Kubernetes context: $K8S_CONTEXT"
kubectl config use-context "$K8S_CONTEXT"

# ── 4. Create/update k8s Secret dynamically (no file with hardcoded values) ──
info "Applying Kubernetes Secret with RDS credentials..."
kubectl create secret generic beer-catalogue-db-secret \
  --namespace default \
  --from-literal=DB_URL="$DB_URL" \
  --from-literal=DB_USERNAME="$DB_USERNAME" \
  --from-literal=DB_PASSWORD="$DB_PASSWORD" \
  --dry-run=client -o yaml | kubectl apply -f -

log "Secret applied."

# ── 5. Apply deployment and service manifests ────────────────────────────────
info "Applying Deployment and Service manifests..."

# Inject images from env so the manifests stay image-agnostic
sed \
  -e "s|gheinz/beer-catalogue-backend:latest|${BACKEND_IMAGE}|g" \
  -e "s|gheinz/beer-catalogue-frontend:latest|${FRONTEND_IMAGE}|g" \
  "$K8S_DIR/deployment.yaml" | kubectl apply -f -

kubectl apply -f "$K8S_DIR/service.yaml"

# ── 6. Wait for pods to be ready ─────────────────────────────────────────────
info "Waiting for backend deployment to be ready (timeout: 120s)..."
kubectl rollout status deployment/beer-catalogue-backend --timeout=120s || {
  err "Backend not ready in time. Check logs with:"
  err "  kubectl logs -l app=beer-catalogue-backend --tail=30"
}

info "Waiting for frontend deployment to be ready (timeout=60s)..."
kubectl rollout status deployment/beer-catalogue-frontend --timeout=60s

# ── 7. Summary ───────────────────────────────────────────────────────────────
echo ""
log "======================================================"
log " Demo environment is UP"
log "======================================================"
echo ""
echo "  RDS Endpoint  : $RDS_ENDPOINT"
echo "  Backend (k8s) : kubectl port-forward svc/beer-catalogue-backend-svc 8080:8080"
echo "  Frontend      : http://localhost:30080"
echo "  Swagger UI    : http://localhost:8080/swagger-ui.html (after port-forward)"
echo ""
echo "  Tear down with: ./scripts/demo-down.sh"
echo ""
