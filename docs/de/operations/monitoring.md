# Monitoring

CaptchaService liefert die übliche Spring-Boot-Actuator-Oberfläche mit. Die Freigabe (`management.endpoints.web.exposure.include`) sollte entsprechend der jeweiligen Deployment-Policy konfiguriert werden.

## Health-Checks

- **Liveness**: `GET /actuator/health/liveness`
- **Readiness**: `GET /actuator/health/readiness`
- **Gesamtstatus**: `GET /actuator/health`

Liveness und Readiness sind getrennt, damit Orchestratoren (Kubernetes, Nomad, …) das jeweils richtige Signal abfragen können.

## Anwendungsinformationen

- **Info**: `GET /actuator/info`
- **Metriken**: `GET /actuator/metrics`

## Prometheus-Metriken

Prometheus-formatierte Metriken stehen unter:

```text
GET /actuator/prometheus
```

Der Endpunkt liefert:

- Anwendungs-Metriken (HTTP-Antwortzeiten, Statuscode-Zähler, …),
- JVM-Metriken (Heap, GC, Threads, …),
- Metriken zum Datenbank-Connection-Pool,
- eigene CAPTCHA-Metriken (ausgegebene Challenges, Verifikationen, entwertete Payloads, …).

## Empfehlungen für Dashboards

Wenn CaptchaService vor einem öffentlichen Endpunkt steht (etwa vor den Slot-Suchrouten von `zmscitizenapi` — siehe [Projektgeschichte](../overview/project-history.md)), sind erfahrungsgemäß folgende Signale am nützlichsten:

- `captcha_requests_total` pro Site und Ergebnis (`valid` / `invalid`),
- Verifikationsdauer,
- Zeilen in `captcha_request` und `invalidated_payload`,
- Verteilung der adaptiven Schwierigkeit pro Site.

Die ersten drei werden out-of-the-box exportiert; das letzte ergibt sich aus den Request-Labels.
