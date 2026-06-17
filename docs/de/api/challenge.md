# Challenge anlegen

**`POST /api/v1/captcha/challenge`**

Erzeugt eine neue CAPTCHA-Challenge für die angegebene Site. Der Client löst den zurückgegebenen Proof-of-Work und schickt die Lösung anschließend an [`/verify`](./verify.md).

## Request-Body

```json
{
  "siteKey": "site1",
  "siteSecret": "secret1",
  "clientAddress": "192.168.1.100"
}
```

| Feld            | Pflicht | Beschreibung                                                                                              |
| --------------- | ------- | --------------------------------------------------------------------------------------------------------- |
| `siteKey`       | ja      | Schlüssel der konfigurierten Site (siehe [Site-Konfiguration](../configuration/sites.md)).                |
| `siteSecret`    | ja      | Passendes Site-Geheimnis. Wird out-of-band an das aufrufende Backend ausgegeben.                          |
| `clientAddress` | ja      | Quell-IP des Endnutzers. Wird vom `SourceAddressService` für Allowlist-Prüfung und Schwierigkeit genutzt. |

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

Das ist das übliche ALTCHA-Challenge-Objekt, das sich direkt an das ALTCHA-Widget oder einen kompatiblen Solver weitergeben lässt.

| Feld                                | Beschreibung                                                                                                                            |
| ----------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------- |
| `challenge`                         | ALTCHA-Challenge-Objekt. Unverändert an Widget oder Solver weitergeben.                                                                 |
| `challenge.parameters`              | Proof-of-Work-Parameter, die der Client erfüllen muss.                                                                                  |
| `challenge.parameters.algorithm`    | Key-Derivation-Algorithmus. CaptchaService nutzt `SHA-256` (iteratives SHA-256).                                                        |
| `challenge.parameters.nonce`        | Zufälliger, serverseitig ausgegebener Nonce als Teil der Proof-of-Work-Eingabe.                                                         |
| `challenge.parameters.salt`         | Zufälliges Salt für die Schlüsselableitung. Muss bei `/verify` unverändert mitgeschickt werden.                                         |
| `challenge.parameters.cost`         | Arbeitsfaktor (KDF-Iterationen). Größer = schwerer. Wird über die Schwierigkeits-Map gesetzt.                                           |
| `challenge.parameters.keyLength`    | Länge des abgeleiteten Schlüssels in Bytes.                                                                                             |
| `challenge.parameters.keyPrefix`    | Hex-Präfix, mit dem der abgeleitete Schlüssel beginnen muss.                                                                            |
| `challenge.parameters.keySignature` | Optionales HMAC des erwarteten abgeleiteten Schlüssels (deterministische Verifikation). Wird von CaptchaService nicht genutzt (`null`). |
| `challenge.parameters.memoryCost`   | Speicherkosten für speicherharte Algorithmen (z. B. Argon2). Bei `SHA-256` `null`.                                                      |
| `challenge.parameters.parallelism`  | Parallelität für speicherharte Algorithmen. Bei `SHA-256` `null`.                                                                       |
| `challenge.parameters.expiresAt`    | Unix-Zeitstempel (Sekunden), nach dem die Verifikation fehlschlägt. Entspricht `captcha.captcha-timeout-seconds` (Default `300`).       |
| `challenge.parameters.data`         | Optionale benutzerdefinierte Metadaten an der Challenge. `null`, sofern nicht konfiguriert.                                             |
| `challenge.signature`               | HMAC-Signatur über die Parameter. Belegt, dass die Challenge von diesem CaptchaService stammt. Wird bei `/verify` geprüft.              |

Die Challenge ist `captcha.captcha-timeout-seconds` lang gültig (Default `300`). Danach liefert die Verifikation `valid: false`.
