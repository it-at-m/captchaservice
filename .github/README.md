<div id="top"></div>

<!-- PROJECT SHIELDS -->
[![Made with love by it@M][made-with-love-shield]][itm-opensource]
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]

---

# CaptchaService

## Documentation

- Published developer handbook (GitHub Pages root): [https://it-at-m.github.io/captchaservice/](https://it-at-m.github.io/captchaservice/)

## About CaptchaService

<a href="https://altcha.org/" target="_blank" rel="noopener noreferrer"><img width="120" align="right" alt="ALTCHA logo" src="https://altcha.org/_astro/logo-symbol.JlHBPz1R_Z1HYzg2.svg" /></a>

CaptchaService is a Spring Boot microservice that provides proof-of-work CAPTCHA challenges using the [ALTCHA library](https://altcha.org/) — a GDPR-compliant, privacy-first alternative to traditional image-based CAPTCHAs, [made in Europe](https://altcha.org/), with no cookies, no tracking, and no third-party calls. Picking an open-source, European library is a deliberate vote for **digital sovereignty** in the public sector. CaptchaService adds adaptive difficulty management and multi-tenant support on top.

It is the open-source bot-protection layer in front of the public ZMS / eAppointment APIs operated by the City of Munich (Landeshauptstadt München) — specifically [`zmscitizenview`](https://github.com/it-at-m/eappointment/tree/main/zmscitizenview) and [`zmscitizenapi`](https://github.com/it-at-m/eappointment/tree/main/zmscitizenapi). It replaces years of in-house and proprietary CAPTCHA attempts with a privacy-friendly proof-of-work flow that runs entirely on the client.

Everything else — architecture, configuration, API reference, operations, project history — lives in the handbook.

## Contact

[Overview](https://opensource.muenchen.de/)

Munich Contact: it@M - opensource@muenchen.de

<table border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td style="padding-right: 30px;"><img src="https://assets.muenchen.de/logos/itm/itM_Basislogo_gelb_schwarz-500.png" height="30" align="center"></td>
    <td><img src="https://assets.muenchen.de/logos/lhm/logo-lhm-muenchen.svg" height="30" align="center"></td>
  </tr>
</table>

---

## Über CaptchaService

<a href="https://altcha.org/" target="_blank" rel="noopener noreferrer"><img width="120" align="right" alt="ALTCHA-Logo" src="https://altcha.org/_astro/logo-symbol.JlHBPz1R_Z1HYzg2.svg" /></a>

CaptchaService ist ein Spring-Boot-Microservice, der Proof-of-Work-CAPTCHA-Challenges auf Basis der [ALTCHA-Bibliothek](https://altcha.org/) bereitstellt — eine DSGVO-konforme, datenschutzfreundliche Alternative zu klassischen Bild-CAPTCHAs, [in Europa entwickelt](https://altcha.org/), ohne Cookies, ohne Tracking und ohne Drittanbieter-Aufrufe. Die Wahl einer quelloffenen, europäischen Bibliothek ist für uns ein bewusster Beitrag zur **digitalen Souveränität** der öffentlichen Verwaltung. CaptchaService ergänzt sie um adaptive Schwierigkeitssteuerung und Mehrmandantenfähigkeit.

Er ist die quelloffene Bot-Schutzschicht vor den öffentlichen ZMS-/eAppointment-APIs der Landeshauptstadt München — konkret [`zmscitizenview`](https://github.com/it-at-m/eappointment/tree/main/zmscitizenview) und [`zmscitizenapi`](https://github.com/it-at-m/eappointment/tree/main/zmscitizenapi). Er löst jahrelange Versuche mit eigenen und proprietären CAPTCHAs durch einen datenschutzfreundlichen Proof-of-Work-Ablauf ab, der vollständig auf dem Client läuft.

Architektur, Konfiguration, API-Referenz, Betrieb und Projektgeschichte stehen im Handbuch.

## Kontakt

[Übersicht](https://opensource.muenchen.de/)

Kontakt München: it@M - opensource@muenchen.de

<table border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td style="padding-right: 30px;"><img src="https://assets.muenchen.de/logos/itm/itM_Basislogo_gelb_schwarz-500.png" height="30" align="center"></td>
    <td><img src="https://assets.muenchen.de/logos/lhm/logo-lhm-muenchen.svg" height="30" align="center"></td>
  </tr>
</table>

## Dokumentation (Deutsch)

- Veröffentlichtes Entwicklerhandbuch (GitHub Pages, Startseite): [https://it-at-m.github.io/captchaservice/de/](https://it-at-m.github.io/captchaservice/de/)

## Screenshot

CaptchaService in action on the public `zmscitizenview` appointment-booking page (Landeshauptstadt München) — an unobtrusive "Ich bin kein Bot" checkbox backed by an ALTCHA proof-of-work challenge.

![Ich bin kein Bot CAPTCHA checkbox rendered by CaptchaService in zmscitizenview](../docs/img/ich_bin_kein_bot.png)

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[itm-opensource]: https://opensource.muenchen.de/
[made-with-love-shield]: https://img.shields.io/badge/made%20with%20%E2%9D%A4%20by-it%40M-yellow?style=for-the-badge
[contributors-shield]: https://img.shields.io/github/contributors/it-at-m/captchaservice.svg?style=for-the-badge
[contributors-url]: https://github.com/it-at-m/captchaservice/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/it-at-m/captchaservice.svg?style=for-the-badge
[forks-url]: https://github.com/it-at-m/captchaservice/network/members
[stars-shield]: https://img.shields.io/github/stars/it-at-m/captchaservice.svg?style=for-the-badge
[stars-url]: https://github.com/it-at-m/captchaservice/stargazers
[issues-shield]: https://img.shields.io/github/issues/it-at-m/captchaservice.svg?style=for-the-badge
[issues-url]: https://github.com/it-at-m/captchaservice/issues
[license-shield]: https://img.shields.io/github/license/it-at-m/captchaservice.svg?style=for-the-badge
[license-url]: https://github.com/it-at-m/captchaservice/blob/main/LICENSE
