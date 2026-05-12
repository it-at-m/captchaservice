# Releases

CaptchaService führt kein Changelog im Repository. Alle Releases werden auf GitHub und als Artefakte auf Maven Central veröffentlicht.

- **GitHub-Releases**: [github.com/it-at-m/captchaservice/releases](https://github.com/it-at-m/captchaservice/releases)
- **Maven Central**: Artefakte werden unter der Gruppe `de.muenchen.captchaservice` veröffentlicht.

Die neueste Release-Bezeichnung wird zusätzlich in der oberen Navigation neben dem Seitentitel angezeigt; sie wird live über die GitHub-Releases-API geladen.

## Maven-Koordinaten

```xml
<dependency>
    <groupId>de.muenchen.captchaservice</groupId>
    <artifactId>captchaservice-backend</artifactId>
    <version>${version.captchaservice}</version>
</dependency>
```

Auf eine explizite Version festlegen — die Version wegzulassen ist **nicht empfohlen**.

## Release-Prozess

Das Repository nutzt die zentralen Münchner CI-Templates zum Bauen und Veröffentlichen:

- **Build**: [`.github/workflows/maven-node-build.yml`](https://github.com/it-at-m/captchaservice/blob/main/.github/workflows/maven-node-build.yml)
- **Release**: [`.github/workflows/maven-release.yml`](https://github.com/it-at-m/captchaservice/blob/main/.github/workflows/maven-release.yml) und [`.github/workflows/npm-release.yml`](https://github.com/it-at-m/captchaservice/blob/main/.github/workflows/npm-release.yml) für ergänzende npm-Artefakte
- **Reusable Actions**: [`it-at-m/lhm_actions`](https://github.com/it-at-m/lhm_actions)

Release erzeugen:

1. In GitHub den Tab **Actions** öffnen und den passenden Release-Workflow auswählen.
2. Workflow starten. Er:
   - baut mit übersprungenen Tests via Maven-Profil `release`,
   - signiert die Artefakte und veröffentlicht sie über das Sonatype-Central-Publishing-Plugin auf Maven Central,
   - öffnet einen Pull Request mit der aktualisierten Snapshot-Version (sofern `use-pr` aktiviert ist).
