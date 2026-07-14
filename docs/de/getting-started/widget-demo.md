# Widget-Demo

Im Verzeichnis [`widget/`](https://github.com/it-at-m/captchaservice/tree/main/showcase/widget) liegt eine schlanke Vue-Anwendung mit dem [ALTCHA-Widget](https://altcha.org/docs/v2/widget-integration/). Sie spricht direkt mit dem CaptchaService und eignet sich, um lokal zu prüfen, ob das Anlegen von Challenges und das Prüfen von Lösungen in der Praxis funktionieren — ohne curl oder Postman.

## Voraussetzungen

### 1. Stack und Backend starten

Vom Repository-Root aus:

```bash
cd stack
{docker|podman} compose up -d
```

```bash
cd backend
bash runLocalNoSecurity.sh
```

Prüfen, ob das Backend antwortet:

```bash
curl http://localhost:39146/actuator/health
```

### 2. Widget starten

```bash
cd widget
npm install
npm run dev
```

Im Browser die URL aus der Vite-Ausgabe öffnen (in der Regel `http://localhost:5173/`).

### 3. Widget testen

1. Die Seite zeigt die ALTCHA-Checkbox (Standard: deutsche Oberfläche) und einen **Weiter**-Button, der zunächst deaktiviert ist.
2. Nach dem Anklicken der Checkbox fordert das Widget eine Challenge beim CaptchaService an, löst die Proof-of-Work lokal und sendet das Ergebnis an `/verify`.
3. War die serverseitige Prüfung erfolgreich, wechselt der Widget-Status auf `verified` und der Button **Weiter** wird freigegeben.

Bei Problemen helfen die Entwicklertools des Browsers weiter:

- **Netzwerk** — auf `POST /api/v1/captcha/challenge` und `POST /api/v1/captcha/verify` achten
- **Konsole** — bei `debug: true` in der Widget-Konfiguration schreibt ALTCHA ausführliche Protokolle

## Konfiguration

Die Standardwerte in `widget/src/config/captcha.ts` passen zum lokalen Backend-Profil (`application-local.yml`):

| Einstellung | Standard | Backend-Konfiguration |
| ----------- | -------- | --------------------- |
| Site-Key | `test` | `captcha.sites.test` |
| Site-Secret | `test` | `captcha.sites.test.secret` |
| Client-Adresse | `127.0.0.1` | Feld `clientAddress` in jedem Request |

Anpassungen sind in `widget/.env.local` möglich:

```env
VITE_CAPTCHA_SITE_KEY=test
VITE_CAPTCHA_SITE_SECRET=test
VITE_CAPTCHA_CLIENT_ADDRESS=127.0.0.1
```

Weitere Sites (z. B. `loadtest`) sind unter [Site-Konfiguration](../configuration/sites.md) beschrieben.

## Integration im Überblick

Der CaptchaService erwartet andere Request-Bodies als das ALTCHA-Widget standardmäßig sendet. In `widget/src/utils/captchaFetch.ts` wird das angepasst:

- **Challenge** — der Fetch des Widgets wird abgefangen; es folgt ein POST mit `{ siteKey, siteSecret, clientAddress }`. Aus der Antwort wird `{ challenge: … }` extrahiert.
- **Verify** — die Widget-Payload wird dekodiert und als POST `{ siteKey, siteSecret, clientAddress, payload: { challenge, solution } }` gesendet. `{ valid: true }` wird für das Widget in `{ verified: true }` überführt.

Das entspricht dem Muster aus dem produktiven [eAppointment / zmscitizenview](https://github.com/it-at-m/eappointment/blob/main/zmscitizenview/src/components/Appointment/ServiceFinder/AltchaCaptcha.vue), wo ein BFF Zugangsdaten serverseitig ergänzt. In dieser Demo liegen sie im Frontend — das ist nur für lokale Tests gedacht.

## npm-Befehle

```bash
cd widget
npm run lint   # Prettier, ESLint, vue-tsc
npm run fix    # Formatierung und Lint-Probleme automatisch beheben
npm run build  # Produktions-Build nach dist/
```

## Weiterführend

- [Challenge anfordern](../api/challenge.md) und [Lösung prüfen](../api/verify.md) — Referenz zu Request und Response
- [Monitoring](../operations/monitoring.md) — Health-Checks und Metriken beim Testen
