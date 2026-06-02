# Site-Konfiguration

CaptchaService ist mandantenfähig. Jede „Site“ hat ihren eigenen Schlüssel, ihr eigenes Geheimnis, optional eine IP-Allowlist und optional eine eigene Schwierigkeits-Map. Konfiguriert wird in `application.yml`:

```yaml
captcha:
  hmac-key: secret # HMAC-Schlüssel zum Signieren von Challenges
  captcha-timeout-seconds: 300 # Gültigkeitsdauer einer Challenge
  source-address-window-seconds: 3600 # Speicherdauer einer Quell-IP
  sites:
    site1: # Site-Key für site1
      site-secret: "secret1" # Site-Secret für site1
      max-verifies-per-payload: 1 # Wie oft eine Payload geprüft werden darf
      whitelisted_source-addresses:
        - "192.0.2.0/24" # erlaubter IP-Bereich
    site2:
      site-secret: "secret2"
      whitelisted_source-addresses:
        - "192.0.2.0/24" # erlaubter IP-Bereich
      difficulty-map:
        - min-visits: 1 # ab dem ersten Besuch…
          cost: 1000 # …Schwierigkeit 1 000
        - min-visits: 10 # ab dem 10. Besuch…
          cost: 10000 # …Schwierigkeit 10 000
```

## Felder

- **`captcha.hmac-key`** — globaler HMAC-Schlüssel zum Signieren jeder Challenge. Über die Umgebungsvariable [`CAPTCHA_HMAC_KEY`](./environment-variables.md) aus dem Secret-Manager injizieren.
- **`captcha.captcha-timeout-seconds`** — wie lange eine ausgestellte Challenge gültig bleibt, bevor sie bei der Verifikation abgelehnt wird.
- **`captcha.source-address-window-seconds`** — Beobachtungsfenster für die adaptive Schwierigkeit.
- **`captcha.sites.<key>.site-secret`** — site-spezifisches Geheimnis. Clients senden bei jedem Request `siteKey` + `siteSecret`.
- **`captcha.sites.<key>.max-verifies-per-payload`** — wie oft eine gelöste Payload geprüft werden darf, bevor sie entwertet ist. Fast immer `1`.
- **`captcha.sites.<key>.whitelisted_source-addresses`** — Liste von CIDR-Bereichen. Clients außerhalb der Liste werden abgelehnt (sofern das Feld gesetzt ist).
- **`captcha.sites.<key>.difficulty-map`** — adaptive Schwierigkeitsleiter. Jeder Eintrag bildet die Anzahl jüngster Besuche (`min-visits`) auf die Schwierigkeit der Challenge (`cost`) ab. Der Dienst wählt pro Request den höchsten passenden Eintrag, sodass viel laufende IPs teurere Challenges erhalten.

## Beispiel-Schwierigkeits-Map

Das obige Beispiel für `site2` gelesen:

| Jüngste Besuche im Fenster | Challenge `cost` |
| -------------------------- | ---------------- |
| 1 bis 9                    | 1 000            |
| 10 oder mehr               | 10 000           |

Ein höherer `cost`-Wert macht jeden Lösungsschritt auf dem Client aufwendiger (mehr Hash-Iterationen pro Versuch) — das Lösen dauert im Schnitt länger. Pro Site anhand des beobachteten Bot-Verhaltens nachjustieren.

## In ZMS / eAppointment

Im Setup für ZMS / eAppointment hält [`zmscitizenview`](https://github.com/it-at-m/eappointment/tree/main/zmscitizenview) den öffentlichen Site-Key, `zmscitizenapi` hält das Site-Secret. `whitelisted_source-addresses` und `difficulty-map` sind auf das Slot-Suchverhalten des öffentlichen Buchungs-Flows abgestimmt — Hintergründe stehen in der [Projektgeschichte](../overview/project-history.md).
