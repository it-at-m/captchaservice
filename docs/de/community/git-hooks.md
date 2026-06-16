# Git-Hooks (Husky)

Dieses Repository nutzt [Husky](https://github.com/typicode/husky), um Git-Hooks aus dem Root-Verzeichnis `.husky/` auszuführen. Die Hooks laufen automatisch beim Commit und halten Commit-Messages, Doku-Formatierung und Java-Code-Stil konsistent.

Die Skripte liegen in [`.husky/`](https://github.com/it-at-m/captchaservice/tree/main/.husky) (`pre-commit`, `commit-msg`).

## Einrichtung

Hooks werden eingerichtet, wenn im **Repository-Root** ausgeführt wird:

```bash
npm install
```

Das `prepare`-Skript läuft automatisch und zeigt Git auf `.husky/`.

Für Doku-Arbeit einmalig die Docs-Abhängigkeiten installieren:

```bash
cd docs && npm install
```

## Hooks

### `pre-commit`

Leerer Platzhalter. Git führt immer zuerst `pre-commit`, dann `commit-msg` aus; die Prüfungen liegen in `commit-msg`, damit die **Commit-Message zuerst** validiert wird.

### `commit-msg`

Alle Prüfungen laufen in diesem Hook, in **Fail-Fast**-Reihenfolge:

1. **Commit-Message** — Subject-Zeile aus der Message-Datei, die Git an diesen Hook übergibt
2. **Doku-Formatierung** — Prettier-Check in `docs/`, wenn gestagte Dateien unter `docs/` liegen (`npm run format:check`)
3. **Java-Code-Stil** — Spotless-Check über Maven, wenn gestagte `*.java`-Dateien vorhanden sind

**Verhalten**

- Commit-Message und Doku-Checks **blockieren** den Commit bei Fehlern
- Spotless läuft nur bei gestagten Java-Dateien; wenn Maven lokal nicht installiert ist, wird Spotless mit einer Warnung übersprungen

**Commit-Message-Format** (Schritt 1):

```txt
type(PROJECT-123): commit message
type(PROJECT): commit message
```

Die Ticket-Nummer ist **optional** — `CAPTCHA-123` oder nur `CAPTCHA` (Großbuchstaben).

**Gültige Types:** `feat`, `fix`, `clean`, `chore`, `docs`

**Gültige Projekte:** `CAPTCHA`, `GH`

**Merge-Commits**

Gits Standard-Merge-Subjects (z. B. `Merge branch 'main' into feature-branch`) werden **automatisch akzeptiert**, damit `git merge` ohne Umbenennung der Message abgeschlossen werden kann.

## Fehlerbehebung

### Doku-Formatierung schlägt fehl

```bash
cd docs
npm run format
```

### Spotless schlägt fehl

```bash
mvn -f captchaservice-backend/pom.xml spotless:apply
```

### Hooks vorübergehend überspringen

```bash
git commit --no-verify -m "chore(CAPTCHA): emergency hotfix"
```

Sparsam nutzen — CI erzwingt dieselben Checks.

## Weiterführend

- [Mitwirken](./contributing.md)
- [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/)
