# Create Challenge

**`POST /api/v1/captcha/challenge`**

Creates a new CAPTCHA challenge for the specified site. The client solves the returned proof-of-work and then submits the solution to [`/verify`](./verify.md).

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
  }
}
```

This is the standard ALTCHA challenge object, ready to be passed to the ALTCHA widget or any ALTCHA-compatible solver.

| Field                               | Description                                                                                                          |
| ----------------------------------- | -------------------------------------------------------------------------------------------------------------------- |
| `challenge`                         | ALTCHA challenge object. Pass it to the widget or solver unchanged.                                                  |
| `challenge.parameters`              | Proof-of-work parameters the client must satisfy.                                                                    |
| `challenge.parameters.algorithm`    | Key-derivation algorithm. CaptchaService uses `SHA-256` (iterative SHA-256).                                         |
| `challenge.parameters.nonce`        | Random server-issued nonce used as part of the proof-of-work input.                                                  |
| `challenge.parameters.salt`         | Random salt for key derivation. Must be echoed back on `/verify` unchanged.                                          |
| `challenge.parameters.cost`         | Work factor (KDF iterations). Larger = harder. Set via the site's difficulty map.                                    |
| `challenge.parameters.keyLength`    | Length of the derived key in bytes.                                                                                  |
| `challenge.parameters.keyPrefix`    | Hex prefix the derived key must start with.                                                                          |
| `challenge.parameters.keySignature` | Optional HMAC of the expected derived key (deterministic verification). Not used by CaptchaService (`null`).         |
| `challenge.parameters.memoryCost`   | Memory cost for memory-hard algorithms (e.g. Argon2). `null` for `SHA-256`.                                          |
| `challenge.parameters.parallelism`  | Parallelism for memory-hard algorithms. `null` for `SHA-256`.                                                        |
| `challenge.parameters.expiresAt`    | Unix timestamp (seconds) after which verification fails. Set from `captcha.captcha-timeout-seconds` (default `300`). |
| `challenge.parameters.data`         | Optional custom metadata attached to the challenge. `null` unless configured.                                        |
| `challenge.signature`               | HMAC signature over the parameters. Proves the challenge was minted by this CaptchaService. Verified on `/verify`.   |

The challenge is valid for `captcha.captcha-timeout-seconds` (default `300`). After that, verification will return `valid: false`.
