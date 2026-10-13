/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_CAPTCHA_API_BASE?: string;
  readonly VITE_CAPTCHA_SITE_KEY?: string;
  readonly VITE_CAPTCHA_SITE_SECRET?: string;
  readonly VITE_CAPTCHA_CLIENT_ADDRESS?: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
