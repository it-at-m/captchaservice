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
cd backend
bash runLocal.sh
```

`runLocal.sh` setzt das lokale Profil, übernimmt die Zugangsdaten aus dem Compose-Stack und startet die Spring-Boot-Anwendung.

## 4. Funktion prüfen

```bash
curl http://localhost:39146/actuator/health
```

Erwartet wird `200 OK` mit einem kleinen JSON-Body, dessen Feld `status` `"UP"` enthält. Die übrigen Management-Endpunkte stehen unter [Monitoring](../operations/monitoring.md).

## 5. Im Browser testen (optional)

Challenge-Anlage und Lösungsprüfung lassen sich im Browser mit der [Widget-Demo](./widget-demo.md) nachvollziehen. Dafür werden der laufende Stack, das Backend und die Vue-App unter `widget/` gemeinsam genutzt.

## Nächste Schritte

- [Widget-Demo](./widget-demo.md) — CaptchaService per ALTCHA-Widget im Browser testen
- [Umgebungsvariablen](../configuration/environment-variables.md) — Flags, die von außen überschrieben werden können.
- [Site-Konfiguration](../configuration/sites.md) — Sites, Geheimnisse und Schwierigkeits-Maps festlegen.
- [Challenge anlegen](../api/challenge.md) und [Lösung prüfen](../api/verify.md) — die beiden Endpunkte, die Clients aufrufen.
