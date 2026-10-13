<script setup lang="ts">
import { computed, nextTick, onMounted, onUnmounted, ref } from "vue";

import "altcha";
import "altcha/i18n/de";

import type { WidgetAttributes, WidgetMethods } from "altcha/types";

import { State } from "altcha/types";

import {
  CAPTCHA_CHALLENGE_URL,
  CAPTCHA_CLIENT_ADDRESS,
  CAPTCHA_SITE_KEY,
  CAPTCHA_SITE_SECRET,
  CAPTCHA_VERIFY_URL,
} from "./config/captcha";
import { createCaptchaFetch } from "./utils/captchaFetch";

const altchaWidget = ref<
  (HTMLElement & WidgetAttributes & WidgetMethods) | null
>(null);
const captchaVerified = ref(false);

const configuration = computed(() =>
  JSON.stringify({
    debug: true,
    verifyUrl: CAPTCHA_VERIFY_URL,
  })
);

const onStateChange = (ev: CustomEvent | Event) => {
  if (!("detail" in ev)) return;
  captchaVerified.value = ev.detail.state === State.VERIFIED;
};

onMounted(async () => {
  await nextTick();
  const widget = altchaWidget.value;
  if (widget?.configure) {
    await widget.configure({
      language: "de",
      fetch: createCaptchaFetch({
        siteKey: CAPTCHA_SITE_KEY,
        siteSecret: CAPTCHA_SITE_SECRET,
        clientAddress: CAPTCHA_CLIENT_ADDRESS,
        challengeUrl: CAPTCHA_CHALLENGE_URL,
        verifyUrl: CAPTCHA_VERIFY_URL,
      }),
    });
  }
  altchaWidget.value?.addEventListener("statechange", onStateChange);
});

onUnmounted(() => {
  altchaWidget.value?.removeEventListener("statechange", onStateChange);
});
</script>

<template>
  <main>
    <h1>ALTCHA Widget-Demo</h1>
    <p class="hint">
      Nach erfolgreicher Prüfung wird Weiter freigeschaltet; ein Klick lädt die
      Seite neu.
    </p>

    <form action="#">
      <altcha-widget
        ref="altchaWidget"
        :challenge="CAPTCHA_CHALLENGE_URL"
        :configuration="configuration"
      />
      <button
        type="submit"
        :disabled="!captchaVerified"
      >
        Weiter
      </button>
    </form>
  </main>
</template>
