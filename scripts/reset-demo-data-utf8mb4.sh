#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SQL_DIR="$ROOT_DIR/sql"
DB_HOST="${DB_HOST:-127.0.0.1}"
DB_PORT="${DB_PORT:-3306}"
DB_NAME="${DB_NAME:-employment_system}"
DB_USER="${DB_USER:-root}"
DB_PASSWORD="${DB_PASSWORD:-root123456}"
MYSQL_CONTAINER="${MYSQL_CONTAINER:-employment-system-mysql}"

run_with_mysql_client() {
  mysql \
    --default-character-set=utf8mb4 \
    -h"${DB_HOST}" \
    -P"${DB_PORT}" \
    -u"${DB_USER}" \
    -p"${DB_PASSWORD}" <<SQL
SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
SOURCE ${SQL_DIR}/init.sql;
SOURCE ${SQL_DIR}/002-business-schema.sql;
SOURCE ${SQL_DIR}/003-demo-data.sql;
SQL
}

run_with_docker_exec() {
  {
    printf 'SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;\n'
    cat "${SQL_DIR}/init.sql"
    printf '\n'
    cat "${SQL_DIR}/002-business-schema.sql"
    printf '\n'
    cat "${SQL_DIR}/003-demo-data.sql"
    printf '\n'
  } | docker exec -i "${MYSQL_CONTAINER}" mysql \
    --default-character-set=utf8mb4 \
    -u"${DB_USER}" \
    -p"${DB_PASSWORD}"
}

if command -v mysql >/dev/null 2>&1; then
  run_with_mysql_client
  exit 0
fi

if command -v docker >/dev/null 2>&1; then
  docker exec "${MYSQL_CONTAINER}" true >/dev/null 2>&1 || {
    echo "mysql client not found, and container ${MYSQL_CONTAINER} is not reachable." >&2
    echo "Run the SQL files in ${SQL_DIR} manually with a utf8mb4 client." >&2
    exit 1
  }
  run_with_docker_exec
  exit 0
fi

echo "Neither mysql nor docker is available in PATH." >&2
echo "Run ${SQL_DIR}/init.sql, ${SQL_DIR}/002-business-schema.sql, and ${SQL_DIR}/003-demo-data.sql with utf8mb4 enabled." >&2
exit 1
