# Project History

## Context

**CaptchaService** was created as the bot-protection layer in front of the public ZMS / eAppointment APIs operated by the City of Munich (Landeshauptstadt München) — specifically [`zmscitizenview`](https://github.com/it-at-m/eappointment/tree/main/zmscitizenview) (the citizen-facing booking frontend) and [`zmscitizenapi`](https://github.com/it-at-m/eappointment/tree/main/zmscitizenapi) (the REST backend that serves available appointment slots).

Over the years the team went through several iterations to keep automated scrapers and booking bots away from those endpoints:

1. **A simple, self-developed CAPTCHA** — the first line of defense. Easy to operate, but the bot landscape kept moving and the in-house solution had to be hardened repeatedly.
2. **Commercial, proprietary CAPTCHA services** — worked well in production, but as closed-source offerings with external, GDPR-compliant EU hosting for a public administration they were ultimately not the best fit.
3. **[ALTCHA](https://altcha.org/)** — once a comparable, fully open-source proof-of-work library was found, we made the switch. ALTCHA is GDPR-compliant by design — no cookies, no tracking, no third-party calls —, produces the same kind of near-invisible client-side challenge, and is [made in Europe](https://altcha.org/). The code, the protocol, and the server can all be operated in-house under MIT.

CaptchaService is the small, multi-tenant Spring Boot service that now wraps ALTCHA.

## What it protects

In the eAppointment / ZMS stack, CaptchaService now sits in front of the slot-discovery endpoints exposed by `zmscitizenapi`. A challenge is requested when the citizen opens the booking flow in `zmscitizenview`; the verified payload is then required by `zmscitizenapi` before any reservation or scraping-like read is accepted.

The net effect:

- Automated bots can no longer cheaply enumerate available appointment slots in `zmscitizenapi`.
- Real users only see a near-invisible proof-of-work step in `zmscitizenview`.
- **No personal data leaves the City of Munich infrastructure.**

## Origin

CaptchaService was built at **it@M**, the IT service provider of the Landeshauptstadt München.

The repository is generated from [`it-at-m/refarch-templates`](https://github.com/it-at-m/refarch-templates), so it follows the same conventions, workflows, and code-of-conduct as the other Munich reference-architecture projects.

## Today

- **In production use**: in front of `zmscitizenapi` / `zmscitizenview` in the ZMS / eAppointment project.
- **Reusable**: any Spring Boot or non-Java frontend can call the service over HTTP, configure its own site, and use a dedicated difficulty map.
- **Open source**: published under MIT on [GitHub](https://github.com/it-at-m/captchaservice) and on [opensource.muenchen.de](https://opensource.muenchen.de/).
