# Lösung prüfen

**`POST /api/v1/captcha/verify`**

Verifiziert die Proof-of-Work-Payload, die ein ALTCHA-kompatibler Solver für eine zuvor über [`/challenge`](./challenge.md) bezogene Challenge erzeugt hat.

## Request-Body

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

| Feld                          | Pflicht | Beschreibung                                                                                               |
| ----------------------------- | ------- | ---------------------------------------------------------------------------------------------------------- |
| `siteKey`                     | ja      | Wie bei `/challenge`.                                                                                      |
| `siteSecret`                  | ja      | Wie bei `/challenge`.                                                                                      |
| `clientAddress`               | ja      | Quell-IP des Endnutzers.                                                                                   |
| `payload.challenge`           | ja      | Unverändert aus [`/challenge`](./challenge.md) — siehe [Response](./challenge.md#response).                |
| `payload.solution.counter`    | ja      | Per Brute Force gefundener Integer; `nonce` + `counter` an die KDF, bis `derivedKey` zu `keyPrefix` passt. |
| `payload.solution.derivedKey` | ja      | Kleinbuchstaben-Hex des abgeleiteten Schlüssels für `counter`; serverseitig erneut geprüft.                |
| `payload.solution.time`       | nein    | Lösungsdauer in ms (optional). Wird in `captcha.client.solve.time` erfasst, sofern gesetzt.                |

## Response

```jsonc
{
  "valid": true
}

// oder bei ungültiger Lösung

{
  "valid": false
}
```

Die Verifikation schlägt fehl (und liefert `valid: false`), wenn:

- die Challenge abgelaufen ist (`captcha.captcha-timeout-seconds`),
- die Signatur nicht passt (Challenge wurde manipuliert oder mit einem anderen HMAC-Schlüssel signiert),
- die Payload bereits `max-verifies-per-payload`-mal verifiziert wurde,
- der Proof-of-Work-`derivedKey` die Challenge nicht tatsächlich löst.

Authentifizierungsfehler (falsches `siteKey` / `siteSecret`) oder fehlerhafte Requests führen stattdessen zu HTTP-Fehlerantworten — siehe [Fehlerantworten](./errors.md).
