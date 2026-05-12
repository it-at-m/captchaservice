# Prerequisites

| Requirement               | Version                                              |
| ------------------------- | ---------------------------------------------------- |
| Java                      | 21 or later                                          |
| Maven                     | 3.8+                                                 |
| PostgreSQL                | 16+                                                  |
| Docker / Podman + Compose | recent stable (only for the local development stack) |

> The development stack under `stack/` brings up a PostgreSQL instance on the right port and version; you do not need to install PostgreSQL locally if you use the stack.

The service is built and published from a CI environment that uses the same toolchain. Refer to [`maven-node-build.yml`](https://github.com/it-at-m/captchaservice/blob/main/.github/workflows/maven-node-build.yml) for the exact versions used in CI.
