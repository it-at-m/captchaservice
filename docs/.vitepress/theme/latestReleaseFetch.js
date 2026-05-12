const LATEST_API =
  "https://api.github.com/repos/it-at-m/captchaservice/releases/latest";

// Release tags in this repo are prefixed with the Maven artifactId, e.g.
// `captchaservice-backend-1.4.0`. Strip the prefix for display; the link still
// points to the full tag URL from the GitHub API.
const formatTagName = (tagName) =>
  tagName.replace(/^captchaservice-backend-/, "");

let cached = null;
let inflight = null;

/**
 * @returns {Promise<{ tagName: string, tagUrl: string }>}
 */
export async function fetchLatestRelease() {
  if (cached) {
    return cached;
  }
  if (inflight) {
    return inflight;
  }
  inflight = (async () => {
    try {
      const res = await fetch(LATEST_API, {
        headers: { Accept: "application/vnd.github+json" },
      });
      if (!res.ok) {
        cached = { tagName: "", tagUrl: "" };
        return cached;
      }
      const data = await res.json();
      const name = data.tag_name;
      const url = data.html_url;
      if (typeof name === "string" && name && typeof url === "string" && url) {
        cached = { tagName: formatTagName(name), tagUrl: url };
      } else {
        cached = { tagName: "", tagUrl: "" };
      }
      return cached;
    } catch {
      cached = { tagName: "", tagUrl: "" };
      return cached;
    } finally {
      inflight = null;
    }
  })();
  return inflight;
}
