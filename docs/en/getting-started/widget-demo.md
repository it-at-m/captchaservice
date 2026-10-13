# Widget Demo

The [`widget/`](https://github.com/it-at-m/captchaservice/tree/main/widget) directory contains a minimal Vue app with the [ALTCHA widget](https://altcha.org/docs/v2/widget-integration/). It talks directly to CaptchaService and lets you verify locally that challenge creation and solution verification work in practice — without curl or Postman.

## Prerequisites

### 1. Start stack and backend

From the repository root:

```bash
cd stack
podman compose up -d

cd ../backend
bash runLocalNoSecurity.sh
```

Confirm the backend is responding:

```bash
curl http://localhost:39146/actuator/health
```

### 2. Start the widget

```bash
cd widget
npm install
npm run dev
```

Open the URL from the Vite output in your browser (usually `http://localhost:5173/`).

### 3. Test the widget

1. The page shows the ALTCHA checkbox (German UI by default) and a **Weiter** button that starts disabled.
2. After clicking the checkbox, the widget requests a challenge from CaptchaService, solves the proof-of-work locally, and posts the result to `/verify`.
3. If server-side verification succeeds, the widget state becomes `verified` and the **Weiter** button is enabled.

If something goes wrong, use the browser developer tools:

- **Network** — watch for `POST /api/v1/captcha/challenge` and `POST /api/v1/captcha/verify`
- **Console** — with `debug: true` in the widget config, ALTCHA writes detailed logs

## Configuration

Defaults in `widget/src/config/captcha.ts` match the local backend profile (`application-local.yml`):

| Setting        | Default     | Backend configuration                  |
| -------------- | ----------- | -------------------------------------- |
| Site key       | `test`      | `captcha.sites.test`                   |
| Site secret    | `test`      | `captcha.sites.test.secret`            |
| Client address | `127.0.0.1` | `clientAddress` field on every request |

Override in `widget/.env.local`:

```env
VITE_CAPTCHA_SITE_KEY=test
VITE_CAPTCHA_SITE_SECRET=test
VITE_CAPTCHA_CLIENT_ADDRESS=127.0.0.1
```

Other sites (for example `loadtest`) are described under [Site Configuration](../configuration/sites.md).

## Integration overview

CaptchaService expects different request bodies than the ALTCHA widget sends by default. `widget/src/utils/captchaFetch.ts` adapts between them:

- **Challenge** — intercepts the widget fetch, then POSTs `{ siteKey, siteSecret, clientAddress }`. Extracts `{ challenge: … }` from the response.
- **Verify** — decodes the widget payload and POSTs `{ siteKey, siteSecret, clientAddress, payload: { challenge, solution } }`. Translates `{ valid: true }` to `{ verified: true }` for the widget.

This follows the same pattern as production [eAppointment / zmscitizenview](https://github.com/it-at-m/eappointment/blob/main/zmscitizenview/src/components/Appointment/ServiceFinder/AltchaCaptcha.vue), where a BFF adds credentials server-side. In this demo they live in the frontend — for local testing only.

## npm commands

```bash
cd widget
npm run lint   # Prettier, ESLint, vue-tsc
npm run fix    # auto-fix formatting and lint issues
npm run build  # production build to dist/
```

## Further reading

- [Create Challenge](../api/challenge.md) and [Verify Solution](../api/verify.md) — request and response reference
- [Monitoring](../operations/monitoring.md) — health checks and metrics while testing
