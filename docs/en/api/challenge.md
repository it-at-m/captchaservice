# Create Challenge

**`POST /api/v1/captcha/challenge`**

Creates a new CAPTCHA challenge for the specified site. The client solves the returned proof-of-work and then submits the solved payload to [`/verify`](./verify.md).

## Request Body

```json
{
  "siteKey": "site1",
  "siteSecret": "secret1",
  "clientAddress": "192.168.1.100"
}
```

| Field           | Required | Description                                                                                          |
| --------------- | -------- | ---------------------------------------------------------------------------------------------------- |
| `siteKey`       | yes      | Key of the configured site (see [Site Configuration](../configuration/sites.md)).                    |
| `siteSecret`    | yes      | Matching site secret. Issued out-of-band to the calling backend.                                     |
| `clientAddress` | yes      | Source IP of the end user. Used by `SourceAddressService` for allow-list checks and difficulty calc. |

## Response

```json
{
  "algorithm": "SHA-256",
  "challenge": "abc123...",
  "maxNumber": 1000,
  "salt": "def456...",
  "signature": "ghi789..."
}
```

This is the standard ALTCHA challenge object, ready to be passed to the ALTCHA widget or any ALTCHA-compatible solver.

| Field       | Description                                                                                         |
| ----------- | --------------------------------------------------------------------------------------------------- |
| `algorithm` | Hash algorithm used to verify the proof of work (always `SHA-256`).                                 |
| `challenge` | Server-issued challenge string the client must solve.                                               |
| `maxNumber` | Upper bound for the proof-of-work search space. Larger = harder. Set via the site's difficulty map. |
| `salt`      | Salt used by ALTCHA to derive the challenge. Must be echoed back on `/verify`.                      |
| `signature` | HMAC-signed proof that this challenge was minted by this CaptchaService. Verified on `/verify`.     |

The challenge is valid for `captcha.captcha-timeout-seconds` (default `300`). After that, verification will return `valid: false`.
