const apiBase = import.meta.env.VITE_CAPTCHA_API_BASE ?? '';

export const CAPTCHA_CHALLENGE_URL = `${apiBase}/api/v1/captcha/challenge`;
export const CAPTCHA_VERIFY_URL = `${apiBase}/api/v1/captcha/verify`;

export const CAPTCHA_SITE_KEY = import.meta.env.VITE_CAPTCHA_SITE_KEY ?? 'test';
export const CAPTCHA_SITE_SECRET = import.meta.env.VITE_CAPTCHA_SITE_SECRET ?? 'test';
export const CAPTCHA_CLIENT_ADDRESS =
  import.meta.env.VITE_CAPTCHA_CLIENT_ADDRESS ?? '127.0.0.1';
