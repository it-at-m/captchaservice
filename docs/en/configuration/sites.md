# Site Configuration

CaptchaService is multi-tenant. Each "site" has its own key, secret, optional IP allow-list, and optional difficulty map. Configure them in `application.yml`:

```yaml
captcha:
  hmac-key: secret # HMAC key for signing challenges
  captcha-timeout-seconds: 300 # How long a CAPTCHA challenge is valid
  source-address-window-seconds: 3600 # How long a source address is stored
  sites:
    site1: # Site key for site1
      site-secret: "secret1" # Site secret for site1
      max-verifies-per-payload: 1 # How many times a payload can be verified
      whitelisted_source-addresses:
        - "192.0.2.0/24" # Whitelisted IP address range
    site2:
      site-secret: "secret2"
      whitelisted_source-addresses:
        - "192.0.2.0/24" # Whitelisted IP address range
      difficulty-map:
        - min-visits: 1 # From the first visit on...
          max-number: 1000 # ...the difficulty is 1000
        - min-visits: 10 # From the 10th visit on...
          max-number: 10000 # ...the difficulty is 10000
```

## Fields

- **`captcha.hmac-key`** — global HMAC key used to sign every challenge. Move this into your secret manager via the [`CAPTCHA_HMAC_KEY`](./environment-variables.md) environment variable.
- **`captcha.captcha-timeout-seconds`** — how long an issued challenge stays valid before it is rejected on verification.
- **`captcha.source-address-window-seconds`** — observation window for adaptive difficulty.
- **`captcha.sites.<key>.site-secret`** — per-site secret. Clients send `siteKey` + `siteSecret` on every request.
- **`captcha.sites.<key>.max-verifies-per-payload`** — how many times a single solved payload can be verified before it is invalidated. Almost always `1`.
- **`captcha.sites.<key>.whitelisted_source-addresses`** — list of CIDR ranges. Clients from outside the list are rejected (when the field is present).
- **`captcha.sites.<key>.difficulty-map`** — adaptive difficulty ladder. Each entry maps a number of recent visits (`min-visits`) to a maximum challenge number (`max-number`). The service picks the highest matching entry per request, so heavy hitters get more expensive challenges.

## Difficulty Map Example

Reading the example above for `site2`:

| Recent visits in window | Challenge `maxNumber` |
| ----------------------- | --------------------- |
| 1 – 9                   | 1 000                 |
| 10 +                    | 10 000                |

Higher `maxNumber` means the client has to try more nonces on average to solve the proof of work. Tune per site based on observed bot traffic.

## In ZMS / eAppointment

For the ZMS / eAppointment deployment, [`zmscitizenview`](https://github.com/it-at-m/eappointment/tree/main/zmscitizenview) holds the public site key and `zmscitizenapi` keeps the site secret. The `whitelisted_source-addresses` list and `difficulty-map` are tuned to the slot-discovery traffic profile of the public booking flow — see [Project History](../overview/project-history.md) for background.
