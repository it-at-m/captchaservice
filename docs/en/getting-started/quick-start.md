# Quick Start

This walks you from a fresh clone to a running service that answers on `http://localhost:39146`.

## 1. Clone the repository

```bash
git clone https://github.com/it-at-m/captchaservice.git
cd captchaservice
```

## 2. Start the development stack

The repository ships a Compose stack that runs PostgreSQL (and any other infrastructure the service needs) on the right ports.

```bash
cd stack
{docker|podman} compose up -d
```

Use either `docker compose` or `podman compose` depending on what you have installed.

## 3. Build and run the application

```bash
cd captchaservice-backend
bash runLocal.sh
```

`runLocal.sh` wires up the local profile, picks up the credentials defined in the Compose stack, and starts the Spring Boot application.

## 4. Verify the service is running

```bash
curl http://localhost:39146/actuator/health
```

You should get a `200 OK` with a small JSON body whose top-level `status` field is `"UP"`. See [Monitoring](../operations/monitoring.md) for the rest of the management endpoints.

## Next steps

- [Environment Variables](../configuration/environment-variables.md) — flags you can override from the outside.
- [Site Configuration](../configuration/sites.md) — define your sites, secrets and difficulty maps.
- [Create Challenge](../api/challenge.md) and [Verify Solution](../api/verify.md) — the two endpoints clients call.
