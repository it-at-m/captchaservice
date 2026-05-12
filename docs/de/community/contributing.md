# Mitwirken

Beiträge machen die Open-Source-Community zu einem inspirierenden Ort zum Lernen und Mitgestalten. Jeder Beitrag wird **sehr geschätzt**.

Wie konkret beigetragen werden kann, steht in der Münchner Open-Source-Beitragsleitlinie auf der [RefArch-Dokumentationsseite](https://refarch.oss.muenchen.de/contribute).

## Kurzpfad

1. Das Repository [`it-at-m/captchaservice`](https://github.com/it-at-m/captchaservice) auf GitHub forken.
2. Einen Feature-Branch erzeugen (`git checkout -b feature/your-improvement`).
3. Lokal `bash runLocal.sh` gegen den Compose-Stack ausführen (siehe [Schnellstart](../getting-started/quick-start.md)) und Änderungen prüfen.
4. Committen, pushen und einen Pull Request gegen `main` öffnen.

Die CI-Workflows ([`maven-node-build.yml`](https://github.com/it-at-m/captchaservice/blob/main/.github/workflows/maven-node-build.yml), [`actionlint.yml`](https://github.com/it-at-m/captchaservice/blob/main/.github/workflows/actionlint.yml), [`codeql.yml`](https://github.com/it-at-m/captchaservice/blob/main/.github/workflows/codeql.yml), [`dependency-review.yml`](https://github.com/it-at-m/captchaservice/blob/main/.github/workflows/dependency-review.yml), [`dockercompose-healthcheck.yml`](https://github.com/it-at-m/captchaservice/blob/main/.github/workflows/dockercompose-healthcheck.yml)) prüfen jeden Pull Request.

## Verhaltenskodex

Bitte lest und befolgt unseren [Verhaltenskodex](https://github.com/it-at-m/captchaservice/blob/main/.github/CODE_OF_CONDUCT.md).

## Lizenz

CaptchaService wird unter der [MIT-Lizenz](https://github.com/it-at-m/captchaservice/blob/main/LICENSE) veröffentlicht. Mit eurem Beitrag stimmt ihr zu, dass er ebenfalls unter dieser Lizenz steht.
