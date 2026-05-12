# Datenbank und Migrationen

CaptchaService speichert Challenge-Zustand und Entwertungseinträge in PostgreSQL. Das Schema wird per Flyway-Migrationen unter `src/main/resources/db/migration/` verwaltet.

## Tabellen

- **`captcha_request`** — eine Zeile pro ausgegebener Challenge. Wird vom `DifficultyService` zur Berechnung der adaptiven Schwierigkeit und vom `ExpiredDataService` zur Bereinigung nach Ablauf genutzt.
- **`invalidated_payload`** — bereits verifizierte gelöste Payloads. Verhindert das Wiederverwenden eines einzelnen Proof-of-Work.

Beide Tabellen werden vom `ExpiredDataService` automatisch und zeitgesteuert bereinigt, abhängig von `captcha-timeout-seconds` und `source-address-window-seconds`.

## Manuelle Flyway-Operationen

Mit dem Maven-Flyway-Plugin lassen sich Migrationen von der Kommandozeile gegen eine Datenbank ausführen:

```bash
# Migrationen manuell ausführen
mvn flyway:migrate

# Migrationsstatus prüfen
mvn flyway:info

# Migrationen validieren
mvn flyway:validate
```

Im Normalbetrieb sollte das nicht nötig sein — der Dienst wendet ausstehende Migrationen beim Start an.

## Verbindung zu einer anderen Datenbank

Die üblichen Spring-Boot-Datenquellen-Properties überschreiben (siehe [Umgebungsvariablen](../configuration/environment-variables.md)):

```bash
export SPRING_DATASOURCE_URL="jdbc:postgresql://db.internal:5432/captcha"
export SPRING_DATASOURCE_USERNAME="captcha"
export SPRING_DATASOURCE_PASSWORD="..."
```

Empfohlen wird PostgreSQL 16 oder neuer.
