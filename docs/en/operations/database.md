# Database and Migrations

CaptchaService persists challenge state and invalidation records to PostgreSQL. Schema is managed using Flyway migrations under `src/main/resources/db/migration/`.

## Tables

- **`captcha_request`** — one row per challenge issued. Used by `DifficultyService` to compute adaptive difficulty and by `ExpiredDataService` to clean up after the challenge has timed out.
- **`invalidated_payload`** — solved payloads that have already been verified. Prevents replay of a single proof of work.

Both tables are cleaned up automatically by `ExpiredDataService` on a schedule, using the `captcha-timeout-seconds` and `source-address-window-seconds` configuration.

## Manual Flyway Operations

The Maven Flyway plugin lets you run migrations against a database from the command line:

```bash
# Run migrations manually
mvn flyway:migrate

# Check migration status
mvn flyway:info

# Validate migrations
mvn flyway:validate
```

In normal operation you should not have to run these — the service applies pending migrations on startup.

## Connecting to a Different Database

Override the standard Spring Boot data-source properties (see [Environment Variables](../configuration/environment-variables.md)):

```bash
export SPRING_DATASOURCE_URL="jdbc:postgresql://db.internal:5432/captcha"
export SPRING_DATASOURCE_USERNAME="captcha"
export SPRING_DATASOURCE_PASSWORD="..."
```

PostgreSQL 16 or later is recommended.
