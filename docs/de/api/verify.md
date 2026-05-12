# Lösung prüfen

**`POST /api/v1/captcha/verify`**

Verifiziert den Proof-of-Work-Payload, den ein ALTCHA-kompatibler Solver für eine zuvor über [`/challenge`](./challenge.md) bezogene Challenge erzeugt hat.

## Request-Body

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

| Feld                                                                          | Pflicht | Beschreibung                                                                       |
| ----------------------------------------------------------------------------- | ------- | ---------------------------------------------------------------------------------- |
| `siteKey`                                                                     | ja      | Gleicher Site-Key wie bei `/challenge`.                                            |
| `siteSecret`                                                                  | ja      | Gleiches Site-Geheimnis.                                                           |
| `clientAddress`                                                               | ja      | Quell-IP des Endnutzers.                                                           |
| `payload`                                                                     | ja      | Vom Client-Widget zurückgegebene ALTCHA-Lösung.                                    |
| `payload.number`                                                              | ja      | Nonce, den der Client gefunden hat und der den Proof-of-Work erfüllt.              |
| `payload.took`                                                                | nein    | Optionale Wall-Clock-Zeit des Clients für die Challenge. Hilfreich für Telemetrie. |
| `payload.salt`, `payload.challenge`, `payload.signature`, `payload.algorithm` | ja      | unverändert aus `/challenge`.                                                      |

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
- der Payload bereits öfter als `max-verifies-per-payload` verifiziert wurde,
- der Proof-of-Work-`number` die Challenge nicht tatsächlich löst.

Authentifizierungsfehler (falsches `siteKey` / `siteSecret`) oder fehlerhafte Requests führen stattdessen zu HTTP-Fehlerantworten — siehe [Fehlerantworten](./errors.md).
