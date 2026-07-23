# Environment Variables

CaptchaService is configured via standard Spring Boot mechanisms (`application.yml`, environment variables, command-line flags). The variables below are the ones you will typically want to override per environment.

| Variable                                | Description                     | Default                                           |
| --------------------------------------- | ------------------------------- | ------------------------------------------------- |
| `SPRING_DATASOURCE_URL`                 | PostgreSQL connection URL       | `jdbc:postgresql://localhost:5432/captchaservice` |
| `SPRING_DATASOURCE_USERNAME`            | Database username               | -                                                 |
| `SPRING_DATASOURCE_PASSWORD`            | Database password               | -                                                 |
| `HIKARI_MAXIMUM_POOL_SIZE`              | JDBC connection pool size       | `20`                                              |
| `HIKARI_MINIMUM_IDLE`                   | Minimum idle pool connections   | `5`                                               |
| `HIKARI_CONNECTION_TIMEOUT`             | Wait for a free connection (ms) | `5000`                                            |
| `HIKARI_LEAK_DETECTION_THRESHOLD`       | Log connection leaks after (ms) | `60000`                                           |
| `CAPTCHA_HMAC_KEY`                      | HMAC key for challenge signing  | -                                                 |
| `CAPTCHA_CAPTCHA_TIMEOUT_SECONDS`       | Challenge validity period       | `300`                                             |
| `CAPTCHA_SOURCE_ADDRESS_WINDOW_SECONDS` | Source address tracking window  | `3600`                                            |

## Notes

- **`CAPTCHA_HMAC_KEY` is mandatory.** Generate a long, random value (e.g. `openssl rand -base64 48`) and store it in your secret manager. Rotating it invalidates every issued challenge.
- **Database credentials** are also mandatory in production. The local Compose stack injects sane defaults so you can run end-to-end locally without configuring anything else.
- The two `CAPTCHA_*` time-window variables control how long an issued challenge is valid and how long a source address contributes to the adaptive difficulty calculation. The defaults (5 minutes / 1 hour) are a good starting point.
- Under high concurrent challenge/verify traffic, raise `HIKARI_MAXIMUM_POOL_SIZE` if `hikaricp_connections_pending` / connection timeouts appear. A short `HIKARI_CONNECTION_TIMEOUT` fails fast instead of blocking servlet threads for 30 seconds.

See [Site Configuration](./sites.md) for the per-site keys, secrets and difficulty maps.
