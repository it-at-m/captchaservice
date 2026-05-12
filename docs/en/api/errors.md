# Error Responses

CaptchaService follows the standard Spring Boot error envelope. The two responses you will see most often are:

## 401 Unauthorized

The `siteKey` / `siteSecret` pair does not match any configured site.

```json
{
  "status": 401,
  "error": "Authentication Error"
}
```

Action: confirm the caller is using the secret for that environment — see [Site Configuration](../configuration/sites.md).

## 400 Bad Request

The request body is malformed (missing required fields, invalid JSON, invalid IP, etc.).

```json
{
  "status": 400,
  "error": "Bad Request"
}
```

Action: check the request against [Create Challenge](./challenge.md) and [Verify Solution](./verify.md).

## Other Status Codes

- **`500 Internal Server Error`** — unexpected exception. Inspect the application logs and `/actuator/health` (see [Monitoring](../operations/monitoring.md)).
- **`503 Service Unavailable`** — typically a database connectivity issue. Compare against `/actuator/health` (the database health indicator).

## Successful Verification Result

Note that `/verify` returns `200 OK` with a `valid: false` body for a **failed proof of work** — that is a domain answer, not an HTTP error. The HTTP error responses above are only used for authentication failures and malformed input.
