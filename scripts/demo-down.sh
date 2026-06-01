#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(dirname "$SCRIPT_DIR")"
TERRAFORM_DIR="$ROOT_DIR/terraform"

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

log()  { echo -e "${GREEN}[✔]${NC} $*"; }
info() { echo -e "${YELLOW}[→]${NC} $*"; }

K8S_CONTEXT="${K8S_CONTEXT:-rancher-desktop}"

# ── 1. Remove k8s resources ──────────────────────────────────────────────────
info "Switching to Kubernetes context: $K8S_CONTEXT"
kubectl config use-context "$K8S_CONTEXT"

info "Removing Kubernetes resources..."
kubectl delete deployment beer-catalogue-backend beer-catalogue-frontend --ignore-not-found
kubectl delete service beer-catalogue-backend-svc beer-catalogue-frontend-svc --ignore-not-found
kubectl delete secret beer-catalogue-db-secret --ignore-not-found
log "Kubernetes resources removed."

# ── 2. Terraform destroy ──────────────────────────────────────────────────────
info "Destroying AWS infrastructure with Terraform..."
cd "$TERRAFORM_DIR"
terraform destroy -auto-approve
log "AWS resources destroyed."

echo ""
log "======================================================"
log " Demo environment is DOWN — \$0 running in AWS"
log "======================================================"
