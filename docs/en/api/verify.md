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
    "challenge": {
      "parameters": {
        "algorithm": "SHA-256",
        "nonce": "abc123...",
        "salt": "def456...",
        "cost": 1000,
        "keyLength": 32,
        "keyPrefix": "00",
        "keySignature": null,
        "memoryCost": null,
        "parallelism": null,
        "expiresAt": 1778916660,
        "data": null
      },
      "signature": "ghi789..."
    },
    "solution": {
      "counter": 12,
      "derivedKey": "00a1b2c3d4e5f6...",
      "time": 47
    }
  }
}
```

| Field                         | Required | Description                                                                                      |
| ----------------------------- | -------- | ------------------------------------------------------------------------------------------------ |
| `siteKey`                     | yes      | Same as on `/challenge`.                                                                         |
| `siteSecret`                  | yes      | Same as on `/challenge`.                                                                         |
| `clientAddress`               | yes      | End-user source IP.                                                                              |
| `payload.challenge`           | yes      | Unchanged from [`/challenge`](./challenge.md) — see [Response](./challenge.md#response).         |
| `payload.solution.counter`    | yes      | Brute-forced integer; `nonce` + `counter` fed to the KDF until `derivedKey` matches `keyPrefix`. |
| `payload.solution.derivedKey` | yes      | Lowercase hex of the derived key for `counter`; re-verified server-side.                         |
| `payload.solution.time`       | no       | Solve time in ms (optional). Recorded in `captcha.client.solve.time` when set.                   |

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
- The proof-of-work `derivedKey` does not actually solve the challenge.

Authentication errors (bad `siteKey` / `siteSecret`) or malformed requests return HTTP error responses instead — see [Error Responses](./errors.md).
