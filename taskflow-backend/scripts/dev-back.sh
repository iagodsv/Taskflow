#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")"/.. && pwd)"
cd "$ROOT_DIR"

echo "[dev-back] Pasta do backend: $ROOT_DIR"

# 1) Subir Postgres via Docker Compose
if ! command -v docker >/dev/null 2>&1; then
  echo "[dev-back] ERRO: Docker não encontrado no PATH. Instale Docker Desktop e tente novamente." >&2
  exit 1
fi

echo "[dev-back] Subindo Postgres via docker compose..."
docker compose up -d || true

# 2) Liberar porta 8080 se estiver ocupada
if lsof -nP -iTCP:8080 -sTCP:LISTEN >/dev/null 2>&1; then
  echo "[dev-back] Porta 8080 ocupada. Encerrando processo..."
  PIDS=$(lsof -nP -iTCP:8080 -sTCP:LISTEN | awk 'NR>1 {print $2}' | sort -u)
  if [[ -n "$PIDS" ]]; then
    echo "$PIDS" | xargs -r kill
    sleep 2
  fi
fi

# 3) Iniciar aplicação Spring Boot
echo "[dev-back] Iniciando aplicação Spring Boot..."
./mvnw spring-boot:run &
APP_PID=$!
echo "[dev-back] PID da aplicação: $APP_PID"

cleanup() {
  echo "\n[dev-back] Encerrando aplicação (PID $APP_PID)..."
  kill "$APP_PID" 2>/dev/null || true
}
trap cleanup EXIT

# 4) Health check: aguardar até responder
HEALTH_URLS=(
  "http://localhost:8080/taskflow/public/ping"
  "http://localhost:8080/taskflow/v3/api-docs"
  "http://localhost:8080/taskflow/swagger-ui/index.html"
)

echo "[dev-back] Aguardando aplicação ficar pronta..."
MAX_TRIES=40
SLEEP_SECS=1
READY=false

for ((i=1; i<=MAX_TRIES; i++)); do
  ALL_OK=true
  for url in "${HEALTH_URLS[@]}"; do
    STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$url" || true)
    if [[ "$STATUS" != "200" ]]; then
      ALL_OK=false
      break
    fi
  done
  if $ALL_OK; then
    READY=true
    break
  fi
  sleep "$SLEEP_SECS"
done

if ! $READY; then
  echo "[dev-back] Aviso: aplicação não respondeu 200 em todos os endpoints de saúde após $((MAX_TRIES*SLEEP_SECS))s. Veja logs no terminal."
else
  echo "[dev-back] ✅ Aplicação pronta. Endpoints OK:"
  for url in "${HEALTH_URLS[@]}"; do
    echo " - $url"
  done
  echo "[dev-back] Pressione Ctrl+C para encerrar."
fi

# 5) Mantém script em primeiro plano para permitir trap EXIT
wait "$APP_PID"
