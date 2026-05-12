# Fehlerantworten

CaptchaService nutzt das übliche Spring-Boot-Fehlerschema. Die beiden häufigsten Antworten sind:

## 401 Unauthorized

Das Paar aus `siteKey` / `siteSecret` passt zu keiner konfigurierten Site.

```json
{
  "status": 401,
  "error": "Authentication Error"
}
```

Maßnahme: prüfen, ob der Aufrufer das Geheimnis für diese Umgebung verwendet — siehe [Site-Konfiguration](../configuration/sites.md).

## 400 Bad Request

Der Request-Body ist fehlerhaft (fehlende Pflichtfelder, ungültiges JSON, ungültige IP usw.).

```json
{
  "status": 400,
  "error": "Bad Request"
}
```

Maßnahme: Request mit [Challenge anlegen](./challenge.md) und [Lösung prüfen](./verify.md) abgleichen.

## Weitere Status-Codes

- **`500 Internal Server Error`** — unerwartete Ausnahme. Anwendungs-Logs prüfen und `/actuator/health` (siehe [Monitoring](../operations/monitoring.md)).
- **`503 Service Unavailable`** — meist ein Verbindungsproblem zur Datenbank. Mit `/actuator/health` (Datenbank-Health-Indicator) abgleichen.

## Erfolgreiche Verifikation mit Negativergebnis

Wichtig: `/verify` antwortet mit `200 OK` und `valid: false`, wenn der Proof-of-Work fachlich nicht passt — das ist eine fachliche Antwort, kein HTTP-Fehler. Die HTTP-Fehlerantworten oben werden nur für Authentifizierungsfehler und fehlerhafte Eingaben verwendet.
