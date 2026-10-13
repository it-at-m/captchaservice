import type { Challenge, Solution } from "altcha/types";

type AltchaPayload = {
  challenge: Challenge;
  solution: Solution;
};

type CaptchaVerifyResponse = {
  valid?: boolean;
};

type AltchaVerifyRequestBody = {
  payload?: string;
};

export type CaptchaFetchOptions = {
  siteKey: string;
  siteSecret: string;
  clientAddress: string;
  challengeUrl: string;
  verifyUrl: string;
};

function decodeAltchaPayload(
  payload: string | undefined
): AltchaPayload | undefined {
  if (!payload) return undefined;

  try {
    return JSON.parse(atob(payload)) as AltchaPayload;
  } catch {
    return undefined;
  }
}

function extractAltchaPayload(
  body: BodyInit | null | undefined
): string | undefined {
  if (typeof body !== "string") return undefined;

  try {
    return (JSON.parse(body) as AltchaVerifyRequestBody).payload;
  } catch {
    return undefined;
  }
}

function isSameUrl(requestUrl: string, configuredUrl: string): boolean {
  try {
    return (
      new URL(requestUrl, location.origin).href ===
      new URL(configuredUrl, location.origin).href
    );
  } catch {
    return requestUrl === configuredUrl;
  }
}

async function fetchChallenge(
  url: string,
  options: CaptchaFetchOptions
): Promise<Response> {
  const response = await fetch(url, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      siteKey: options.siteKey,
      siteSecret: options.siteSecret,
      clientAddress: options.clientAddress,
    }),
  });

  if (!response.headers.get("content-type")?.includes("json")) {
    return response;
  }

  const json = (await response.json()) as { challenge?: Challenge };
  if (!json.challenge) {
    return Response.json(json, { status: response.status });
  }

  return Response.json(json.challenge, { status: response.status });
}

async function fetchVerify(
  url: string,
  init: RequestInit | undefined,
  options: CaptchaFetchOptions
): Promise<Response> {
  const altchaPayloadString = extractAltchaPayload(init?.body);
  const payload = decodeAltchaPayload(altchaPayloadString);

  const response = await fetch(url, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      siteKey: options.siteKey,
      siteSecret: options.siteSecret,
      clientAddress: options.clientAddress,
      payload,
    }),
  });

  if (!response.headers.get("content-type")?.includes("json")) {
    return response;
  }

  const json = (await response.json()) as CaptchaVerifyResponse;

  return Response.json(
    {
      verified: json.valid === true,
      payload: altchaPayloadString,
    },
    { status: response.status }
  );
}

export function createCaptchaFetch(options: CaptchaFetchOptions): typeof fetch {
  return async (url, init) => {
    const requestUrl = typeof url === "string" ? url : url.toString();

    if (isSameUrl(requestUrl, options.challengeUrl)) {
      return fetchChallenge(requestUrl, options);
    }

    if (isSameUrl(requestUrl, options.verifyUrl)) {
      return fetchVerify(requestUrl, init, options);
    }

    return fetch(url, init);
  };
}
