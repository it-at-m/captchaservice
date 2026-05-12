# Verify Solution

**`POST /api/v1/captcha/verify`**

Verifies the proof-of-work payload produced by an ALTCHA-compatible solver for a challenge previously obtained from [`/challenge`](./challenge.md).

## Request Body

```json
{
  "siteKey": "site1",
  "siteSecret": "secret1",
  "clientAddress": "192.168.1.100",
  "payload": {
    "algorithm": "SHA-256",
    "challenge": "abc123...",
    "number": 542,
    "salt": "def456...",
    "signature": "ghi789...",
    "took": 4400
  }
}
```

| Field                                                                         | Required | Description                                                                       |
| ----------------------------------------------------------------------------- | -------- | --------------------------------------------------------------------------------- |
| `siteKey`                                                                     | yes      | Same site key as on `/challenge`.                                                 |
| `siteSecret`                                                                  | yes      | Same site secret.                                                                 |
| `clientAddress`                                                               | yes      | Source IP of the end user.                                                        |
| `payload`                                                                     | yes      | ALTCHA solution returned by the client widget.                                    |
| `payload.number`                                                              | yes      | The nonce the client found that satisfies the proof of work.                      |
| `payload.took`                                                                | no       | Optional wall-clock time the client spent on the challenge. Useful for telemetry. |
| `payload.salt`, `payload.challenge`, `payload.signature`, `payload.algorithm` | yes      | Echoed back from `/challenge` unchanged.                                          |

## Response

```jsonc
{
  "valid": true
}

// or if the solution is invalid

{
  "valid": false
}
```

Verification fails (and returns `valid: false`) when:

- The challenge has expired (`captcha.captcha-timeout-seconds`).
- The signature does not match (challenge was tampered with or signed by a different HMAC key).
- The payload has already been verified more than `max-verifies-per-payload` times.
- The proof-of-work `number` does not actually solve the challenge.

Authentication errors (bad `siteKey` / `siteSecret`) or malformed requests return HTTP error responses instead — see [Error Responses](./errors.md).
