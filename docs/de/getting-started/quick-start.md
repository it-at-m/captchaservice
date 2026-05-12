# Schnellstart

Vom frischen Clone bis zu einem laufenden Dienst auf `http://localhost:39146`.

## 1. Repository klonen

```bash
git clone https://github.com/it-at-m/captchaservice.git
cd captchaservice
```

## 2. Entwicklungsstack starten

Das Repository liefert einen Compose-Stack, der PostgreSQL (und alles, was der Dienst sonst noch braucht) auf den richtigen Ports startet.

```bash
cd stack
{docker|podman} compose up -d
```

Je nach Installation `docker compose` oder `podman compose` verwenden.

## 3. Anwendung bauen und starten

```bash
cd captchaservice-backend
bash runLocal.sh
```

`runLocal.sh` setzt das lokale Profil, übernimmt die Zugangsdaten aus dem Compose-Stack und startet die Spring-Boot-Anwendung.

## 4. Funktion prüfen

```bash
curl http://localhost:39146/actuator/health
```

Erwartet wird `200 OK` mit einem kleinen JSON-Body, dessen Feld `status` `"UP"` enthält. Die übrigen Management-Endpunkte stehen unter [Monitoring](../operations/monitoring.md).

## Nächste Schritte

- [Umgebungsvariablen](../configuration/environment-variables.md) — Flags, die von außen überschrieben werden können.
- [Site-Konfiguration](../configuration/sites.md) — Sites, Geheimnisse und Schwierigkeits-Maps festlegen.
- [Challenge anlegen](../api/challenge.md) und [Lösung prüfen](../api/verify.md) — die beiden Endpunkte, die Clients aufrufen.
