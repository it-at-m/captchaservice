# Voraussetzungen

| Anforderung               | Version                                                 |
| ------------------------- | ------------------------------------------------------- |
| Java                      | 21 oder neuer                                           |
| Maven                     | 3.8+                                                    |
| PostgreSQL                | 16+                                                     |
| Docker / Podman + Compose | aktuelle Stable (nur für den lokalen Entwicklungsstack) |

> Der Entwicklungsstack unter `stack/` startet eine PostgreSQL-Instanz auf dem passenden Port in der passenden Version. PostgreSQL muss daher nicht zusätzlich lokal installiert werden, wenn der Stack verwendet wird.

Der Dienst wird in einer CI-Umgebung gebaut und veröffentlicht, die dieselbe Toolchain nutzt. Die exakten Versionen aus der CI stehen in [`maven-node-build.yml`](https://github.com/it-at-m/captchaservice/blob/main/.github/workflows/maven-node-build.yml).
