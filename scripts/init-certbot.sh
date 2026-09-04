#!/usr/bin/env bash
# One-time bootstrap of the Let's Encrypt certificate on a fresh server.
# Run from the directory that contains docker-compose.prod.yml.
#
# Usage: ./scripts/init-certbot.sh you@example.com

set -euo pipefail

COMPOSE_FILE="docker-compose.prod.yml"
DOMAIN="kochbuch.io"
WWW_DOMAIN="www.kochbuch.io"
EMAIL="${1:?Usage: $0 <email>}"
CERT_DIR="nginx/certbot/conf"
LIVE_DIR="$CERT_DIR/live/$DOMAIN"

if [ ! -f "$COMPOSE_FILE" ]; then
  echo "ERROR: $COMPOSE_FILE not found in $(pwd). Run this from /srv/recipes-app." >&2
  exit 1
fi

# Guarantees a clean slate on every run. Without this, leftover containers from a previous
# (partial/failed) run of this script can reference a docker network that no longer exists —
# that's the "network ... not found" error when starting the certbot service further down.
echo "==> Resetting any previous state..."
docker compose -f "$COMPOSE_FILE" down --remove-orphans

echo "==> Creating temporary self-signed certificate so nginx can start..."
mkdir -p "$LIVE_DIR"
openssl req -x509 -nodes -newkey rsa:2048 -days 1 \
  -keyout "$LIVE_DIR/privkey.pem" \
  -out "$LIVE_DIR/fullchain.pem" \
  -subj "/CN=$DOMAIN" >/dev/null 2>&1

# nginx-unprivileged runs as a non-root user; it must be able to read the mount.
find "$CERT_DIR" -type d -exec chmod 755 {} \;
find "$CERT_DIR" -type f -exec chmod 644 {} \;

echo "==> Starting backend + frontend with the temporary certificate..."
docker compose -f "$COMPOSE_FILE" up -d backend frontend

echo "==> Waiting for frontend to answer on port 80..."
ok=0
for i in $(seq 1 30); do
  code=$(curl -s -o /dev/null -w "%{http_code}" "http://$DOMAIN/" || true)
  if [ "$code" = "301" ] || [ "$code" = "200" ]; then
    ok=1
    break
  fi
  sleep 2
done
if [ "$ok" -ne 1 ]; then
  echo "ERROR: frontend never answered on port 80. Aborting before touching certificates." >&2
  echo "Check: docker compose -f $COMPOSE_FILE logs frontend --tail 50" >&2
  exit 1
fi
echo "Frontend is reachable."

echo "==> Removing temporary certificate..."
rm -rf "$CERT_DIR/live" "$CERT_DIR/archive" "$CERT_DIR/renewal"

echo "==> Requesting real certificate from Let's Encrypt..."
# Plain "docker run" instead of "docker compose run": certbot only needs the
# shared webroot volume, not the compose project network, and joining that
# network is what breaks with a stale "network ... not found" error after
# the frontend container has restarted a few times.
docker run --rm \
  -v "$(pwd)/$CERT_DIR:/etc/letsencrypt" \
  -v "$(pwd)/nginx/certbot/www:/var/www/certbot" \
  certbot/certbot certonly --webroot -w /var/www/certbot \
  -d "$DOMAIN" -d "$WWW_DOMAIN" \
  --email "$EMAIL" --agree-tos --no-eff-email \
  --deploy-hook "chmod -R o+rX /etc/letsencrypt/live /etc/letsencrypt/archive"

echo "==> Restarting frontend with the real certificate..."
docker compose -f "$COMPOSE_FILE" restart frontend

echo "==> Starting the certbot renewal loop..."
docker compose -f "$COMPOSE_FILE" up -d certbot

echo "Done. Verify with: curl -I https://$DOMAIN/"
