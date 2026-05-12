# Monitoring

CaptchaService ships with the standard Spring Boot Actuator surface. Configure exposure (`management.endpoints.web.exposure.include`) according to your deployment policy.

## Health Checks

- **Liveness**: `GET /actuator/health/liveness`
- **Readiness**: `GET /actuator/health/readiness`
- **Overall Health**: `GET /actuator/health`

Both Liveness and Readiness are split out so that orchestrators (Kubernetes, Nomad, …) can probe the right concern.

## Application Information

- **Info**: `GET /actuator/info`
- **Metrics**: `GET /actuator/metrics`

## Prometheus Metrics

Prometheus-formatted metrics are available at:

```text
GET /actuator/prometheus
```

The endpoint exposes:

- Application metrics (HTTP request timings, status code counters, …).
- JVM metrics (heap, GC, threads, …).
- Database connection-pool metrics.
- Custom CAPTCHA metrics (challenges issued, verifications, invalidated payloads, …).

## Recommended Dashboards

When you deploy CaptchaService behind a public endpoint (such as the `zmscitizenapi` slot-discovery routes — see [Project History](../overview/project-history.md)), the most useful signals are usually:

- `captcha_requests_total` per site and per outcome (`valid` / `invalid`).
- Verification latency.
- Number of active rows in `captcha_request` and `invalidated_payload`.
- Adaptive difficulty distribution per site.

The first three are exported out of the box; the last one is derived from request labels.
