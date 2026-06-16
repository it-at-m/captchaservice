import fs from "node:fs";
import path from "node:path";

const GH_REPO = "https://github.com/it-at-m/captchaservice";

const SITE_HOSTNAME = "https://it-at-m.github.io";
const SITE_BASE = "/captchaservice/";

// docs/en/ is the single on-disk source for English content. We use VitePress
// `rewrites` so every docs/en/<path>.md is rendered at URL /<path>.html (the
// root locale). German content lives under docs/de/ and serves under /de/.
const DOCS_DIR = path.resolve(import.meta.dirname, "..");
const EN_DIR = path.join(DOCS_DIR, "en");

const toPosix = (p) => p.split(path.sep).join("/");

const listEnMarkdownFiles = (dir) => {
  const out = [];
  if (!fs.existsSync(dir)) return out;
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const full = path.join(dir, entry.name);
    if (entry.isDirectory()) {
      out.push(...listEnMarkdownFiles(full));
    } else if (entry.isFile() && entry.name.endsWith(".md")) {
      out.push(full);
    }
  }
  return out;
};

const buildEnRewrites = () => {
  const rewrites = {};
  for (const abs of listEnMarkdownFiles(EN_DIR)) {
    const rel = toPosix(path.relative(EN_DIR, abs));
    rewrites[`en/${rel}`] = rel;
  }
  return rewrites;
};

const navLabels = {
  en: {
    overview: "Overview",
    releases: "Releases",
    openSource: "Open Source",
    openSourceUrl:
      "https://opensource.muenchen.de/software/captchaservice.html",
    altcha: "ALTCHA",
  },
  de: {
    overview: "Übersicht",
    releases: "Releases",
    openSource: "Open Source",
    openSourceUrl:
      "https://opensource.muenchen.de/de/software/captchaservice.html",
    altcha: "ALTCHA",
  },
};

const buildNav = (prefix, lang) => {
  const t = navLabels[lang];
  return [
    { text: t.overview, link: `${prefix}/` },
    { text: t.releases, link: `${GH_REPO}/releases` },
    {
      text: t.openSource,
      link: t.openSourceUrl,
    },
    { text: t.altcha, link: "https://altcha.org/" },
  ];
};

const sidebarLabels = {
  en: {
    overview: "Overview",
    introduction: "Introduction",
    projectHistory: "Project History",
    architecture: "Architecture",
    releases: "Releases",
    gettingStarted: "Getting Started",
    prerequisites: "Prerequisites",
    quickStart: "Quick Start",
    configuration: "Configuration",
    environmentVariables: "Environment Variables",
    siteConfiguration: "Site Configuration",
    api: "API Reference",
    challenge: "Create Challenge",
    verify: "Verify Solution",
    errors: "Error Responses",
    operations: "Operations",
    database: "Database and Migrations",
    monitoring: "Monitoring",
    community: "Community",
    contributing: "Contributing",
    codeOfConduct: "Code of Conduct",
    license: "License (MIT)",
  },
  de: {
    overview: "Übersicht",
    introduction: "Einführung",
    projectHistory: "Projektgeschichte",
    architecture: "Architektur",
    releases: "Releases",
    gettingStarted: "Erste Schritte",
    prerequisites: "Voraussetzungen",
    quickStart: "Schnellstart",
    configuration: "Konfiguration",
    environmentVariables: "Umgebungsvariablen",
    siteConfiguration: "Site-Konfiguration",
    api: "API-Referenz",
    challenge: "Challenge anlegen",
    verify: "Lösung prüfen",
    errors: "Fehlerantworten",
    operations: "Betrieb",
    database: "Datenbank und Migrationen",
    monitoring: "Monitoring",
    community: "Community",
    contributing: "Mitwirken",
    codeOfConduct: "Verhaltenskodex",
    license: "Lizenz (MIT)",
  },
};

const buildSidebar = (prefix, lang) => {
  const t = sidebarLabels[lang];
  return [
    {
      text: t.overview,
      items: [
        { text: t.introduction, link: `${prefix}/` },
        {
          text: t.projectHistory,
          link: `${prefix}/overview/project-history`,
        },
        { text: t.architecture, link: `${prefix}/overview/architecture` },
        { text: t.releases, link: `${prefix}/overview/releases` },
      ],
    },
    {
      text: t.gettingStarted,
      items: [
        {
          text: t.prerequisites,
          link: `${prefix}/getting-started/prerequisites`,
        },
        {
          text: t.quickStart,
          link: `${prefix}/getting-started/quick-start`,
        },
      ],
    },
    {
      text: t.configuration,
      items: [
        {
          text: t.environmentVariables,
          link: `${prefix}/configuration/environment-variables`,
        },
        {
          text: t.siteConfiguration,
          link: `${prefix}/configuration/sites`,
        },
      ],
    },
    {
      text: t.api,
      items: [
        { text: t.challenge, link: `${prefix}/api/challenge` },
        { text: t.verify, link: `${prefix}/api/verify` },
        { text: t.errors, link: `${prefix}/api/errors` },
      ],
    },
    {
      text: t.operations,
      items: [
        { text: t.database, link: `${prefix}/operations/database` },
        { text: t.monitoring, link: `${prefix}/operations/monitoring` },
      ],
    },
    {
      text: t.community,
      items: [
        {
          text: t.contributing,
          link: `${prefix}/community/contributing`,
        },
        {
          text: t.codeOfConduct,
          link: `${GH_REPO}/blob/main/.github/CODE_OF_CONDUCT.md`,
        },
        { text: t.license, link: `${GH_REPO}/blob/main/LICENSE` },
      ],
    },
  ];
};

const sharedThemeConfig = {
  socialLinks: [
    {
      icon: "github",
      link: `${GH_REPO}/`,
      ariaLabel: "GitHub Repository",
    },
  ],
  search: {
    provider: "local",
    options: {
      locales: {
        de: {
          translations: {
            button: {
              buttonText: "Suchen",
              buttonAriaLabel: "Suchen",
            },
            modal: {
              displayDetails: "Details anzeigen",
              resetButtonTitle: "Suche zurücksetzen",
              backButtonTitle: "Suche schließen",
              noResultsText: "Keine Ergebnisse",
              footer: {
                selectText: "Auswählen",
                selectKeyAriaLabel: "Eingabe",
                navigateText: "Navigieren",
                navigateUpKeyAriaLabel: "Pfeil nach oben",
                navigateDownKeyAriaLabel: "Pfeil nach unten",
                closeText: "Schließen",
                closeKeyAriaLabel: "esc",
              },
            },
          },
        },
      },
    },
  },
};

const toUrlPath = (relativePath) => {
  let p = (relativePath || "").replace(/\\/g, "/");
  p = p.replace(/^en\//, "");
  p = p.replace(/\.md$/, ".html");
  p = p.replace(/(^|\/)index\.html$/, "$1");
  return p;
};

const stripDeLocalePrefix = (urlPath) => urlPath.replace(/^de\//, "");

const buildAbsoluteUrl = (urlPath) => `${SITE_HOSTNAME}${SITE_BASE}${urlPath}`;

const localeOfUrl = (urlPath) => (urlPath.startsWith("de/") ? "de" : "en");

const SITE_TITLE = "CaptchaService Docs";
const SITE_DESCRIPTION =
  "CaptchaService is a Spring Boot microservice that provides proof-of-work CAPTCHA challenges using the ALTCHA library. It offers an alternative to traditional image-based CAPTCHAs with adaptive difficulty management and multi-tenant support.";
const SITE_TITLE_DE = "CaptchaService-Doku";
const SITE_DESCRIPTION_DE =
  "CaptchaService ist ein Spring-Boot-Microservice, der Proof-of-Work-CAPTCHA-Challenges auf Basis der ALTCHA-Bibliothek bereitstellt. Er bietet eine Alternative zu klassischen Bild-CAPTCHAs mit adaptiver Schwierigkeitssteuerung und Mehrmandantenfähigkeit.";

export default {
  title: SITE_TITLE,
  description: SITE_DESCRIPTION,
  base: SITE_BASE,
  lang: "en-US",
  rewrites: buildEnRewrites(),
  sitemap: {
    hostname: `${SITE_HOSTNAME}${SITE_BASE}`,
  },
  transformHead({ pageData, siteConfig }) {
    const tags = [];
    const urlPath = toUrlPath(pageData.relativePath);
    const fullUrl = buildAbsoluteUrl(urlPath);
    const stripped = stripDeLocalePrefix(urlPath);
    const enUrl = buildAbsoluteUrl(stripped);
    const deUrl = buildAbsoluteUrl(`de/${stripped}`);
    const locale = localeOfUrl(urlPath);
    const ogLocale = locale === "de" ? "de_DE" : "en_US";

    const fm = pageData.frontmatter || {};
    const siteTitle = siteConfig?.site?.title || SITE_TITLE;
    const siteDescription = siteConfig?.site?.description || SITE_DESCRIPTION;
    const pageTitle = fm.title || pageData.title || siteTitle;
    const pageDescription =
      fm.description || pageData.description || siteDescription;
    const ogImage = `${SITE_HOSTNAME}${SITE_BASE}img/captchaservice_logo.png`;

    tags.push(["link", { rel: "canonical", href: fullUrl }]);
    tags.push(["link", { rel: "alternate", hreflang: "en", href: enUrl }]);
    tags.push(["link", { rel: "alternate", hreflang: "de", href: deUrl }]);
    tags.push([
      "link",
      { rel: "alternate", hreflang: "x-default", href: enUrl },
    ]);

    tags.push(["meta", { property: "og:type", content: "article" }]);
    tags.push(["meta", { property: "og:site_name", content: siteTitle }]);
    tags.push(["meta", { property: "og:title", content: pageTitle }]);
    tags.push([
      "meta",
      { property: "og:description", content: pageDescription },
    ]);
    tags.push(["meta", { property: "og:url", content: fullUrl }]);
    tags.push(["meta", { property: "og:image", content: ogImage }]);
    tags.push(["meta", { property: "og:locale", content: ogLocale }]);
    tags.push(["meta", { name: "twitter:card", content: "summary" }]);
    tags.push(["meta", { name: "twitter:title", content: pageTitle }]);
    tags.push([
      "meta",
      { name: "twitter:description", content: pageDescription },
    ]);
    tags.push(["meta", { name: "twitter:image", content: ogImage }]);

    return tags;
  },
  markdown: {
    config(md) {
      const defaultFence = md.renderer.rules.fence;
      md.renderer.rules.fence = (tokens, idx, options, env, self) => {
        const token = tokens[idx];
        const info = md.utils.unescapeAll(token.info || "").trim();
        const langName = info.split(/\s+/g)[0];
        if (langName === "mermaid") {
          return `<pre class="mermaid" v-pre>${md.utils.escapeHtml(token.content)}</pre>`;
        }
        if (defaultFence) {
          return defaultFence(tokens, idx, options, env, self);
        }
        return `<pre><code>${md.utils.escapeHtml(token.content)}</code></pre>`;
      };
    },
  },
  head: [
    [
      "link",
      {
        rel: "icon",
        href: "https://assets.muenchen.de/logos/lhm/icon-lhm-muenchen-32.png",
      },
    ],
    ["meta", { name: "robots", content: "index,follow" }],
    ["meta", { name: "author", content: "it@M / Landeshauptstadt München" }],
    [
      "meta",
      {
        name: "keywords",
        content:
          "CaptchaService, ALTCHA, captcha, proof of work, proof-of-work, bot protection, anti-bot, spring boot, java, microservice, postgresql, multi-tenant, government, municipalities, eAppointment, ZMS, zmscitizenapi, zmscitizenview, it@M, it-at-m, Munich, München, open source",
      },
    ],
  ],
  themeConfig: {
    ...sharedThemeConfig,
  },
  locales: {
    root: {
      label: "English",
      lang: "en",
      themeConfig: {
        ...sharedThemeConfig,
        nav: buildNav("", "en"),
        sidebar: buildSidebar("", "en"),
      },
    },
    de: {
      label: "Deutsch",
      lang: "de",
      link: "/de/",
      title: SITE_TITLE_DE,
      description: SITE_DESCRIPTION_DE,
      themeConfig: {
        ...sharedThemeConfig,
        nav: buildNav("/de", "de"),
        sidebar: buildSidebar("/de", "de"),
        outline: { label: "Auf dieser Seite", level: [2, 4] },
        darkModeSwitchLabel: "Darstellung",
        langMenuLabel: "Sprache wechseln",
        returnToTopLabel: "Zurück nach oben",
        sidebarMenuLabel: "Menü",
        docFooter: { prev: "Vorherige Seite", next: "Nächste Seite" },
        lastUpdatedText: "Zuletzt aktualisiert",
      },
    },
  },
};
