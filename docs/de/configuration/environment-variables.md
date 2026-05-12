# Umgebungsvariablen

CaptchaService wird über die üblichen Spring-Boot-Mechanismen konfiguriert (`application.yml`, Umgebungsvariablen, Kommandozeilen-Flags). Die folgenden Variablen werden typischerweise pro Umgebung überschrieben.

| Variable                                | Beschreibung                                  | Default                                           |
| --------------------------------------- | --------------------------------------------- | ------------------------------------------------- |
| `SPRING_DATASOURCE_URL`                 | PostgreSQL-Verbindungs-URL                    | `jdbc:postgresql://localhost:5432/captchaservice` |
| `SPRING_DATASOURCE_USERNAME`            | Datenbank-Benutzername                        | -                                                 |
| `SPRING_DATASOURCE_PASSWORD`            | Datenbank-Passwort                            | -                                                 |
| `CAPTCHA_HMAC_KEY`                      | HMAC-Schlüssel zum Signieren von Challenges   | -                                                 |
| `CAPTCHA_CAPTCHA_TIMEOUT_SECONDS`       | Gültigkeitsdauer einer Challenge in Sekunden  | `300`                                             |
| `CAPTCHA_SOURCE_ADDRESS_WINDOW_SECONDS` | Beobachtungsfenster für Quell-IPs in Sekunden | `3600`                                            |

## Hinweise

- **`CAPTCHA_HMAC_KEY` ist Pflicht.** Ein langer, zufälliger Wert (z. B. `openssl rand -base64 48`) im Secret-Manager ablegen. Wird er rotiert, werden alle bereits ausgestellten Challenges ungültig.
- **Datenbank-Zugangsdaten** sind in der Produktion ebenfalls Pflicht. Der lokale Compose-Stack setzt sinnvolle Defaults, damit die Anwendung lokal ohne weitere Konfiguration startet.
- Die beiden `CAPTCHA_*`-Zeitfenster steuern, wie lange eine ausgestellte Challenge gültig ist und wie lange eine Quell-IP in die adaptive Schwierigkeit einfließt. Die Defaults (5 Minuten / 1 Stunde) sind ein guter Startpunkt.

Die Site-spezifischen Schlüssel, Geheimnisse und Schwierigkeits-Maps stehen in der [Site-Konfiguration](./sites.md).
