# Challenge anlegen

**`POST /api/v1/captcha/challenge`**

Erzeugt eine neue CAPTCHA-Challenge für die angegebene Site. Der Client löst den zurückgegebenen Proof-of-Work und schickt den gelösten Payload anschließend an [`/verify`](./verify.md).

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
  "algorithm": "SHA-256",
  "challenge": "abc123...",
  "maxNumber": 1000,
  "salt": "def456...",
  "signature": "ghi789..."
}
```

Das ist das übliche ALTCHA-Challenge-Objekt, das sich direkt an das ALTCHA-Widget oder einen kompatiblen Solver weitergeben lässt.

| Feld        | Beschreibung                                                                                                |
| ----------- | ----------------------------------------------------------------------------------------------------------- |
| `algorithm` | Hash-Algorithmus zur Verifikation des Proof-of-Work (immer `SHA-256`).                                      |
| `challenge` | Serverseitig ausgegebener Challenge-String, den der Client lösen muss.                                      |
| `maxNumber` | Obergrenze für den Suchraum des Proof-of-Work. Größer = schwerer. Wird über die Schwierigkeits-Map gesetzt. |
| `salt`      | Von ALTCHA benutztes Salt zur Ableitung der Challenge. Muss bei `/verify` unverändert mitgeschickt werden.  |
| `signature` | HMAC-signierter Beweis, dass diese Challenge von diesem CaptchaService stammt. Wird bei `/verify` geprüft.  |

Die Challenge ist `captcha.captcha-timeout-seconds` lang gültig (Default `300`). Danach liefert die Verifikation `valid: false`.
