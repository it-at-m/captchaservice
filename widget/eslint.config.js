import skipFormatting from "@vue/eslint-config-prettier";
import {
  configureVueProject,
  defineConfigWithVueTs,
  vueTsConfigs,
} from "@vue/eslint-config-typescript";
import pluginVue from "eslint-plugin-vue";

configureVueProject({
  rootDir: import.meta.dirname,
});

export default defineConfigWithVueTs(
  {
    name: "app/files-to-ignore",
    ignores: [
      "**/dist/**",
      "**/node_modules/**",
      "**/.eslintcache",
      "**/.prettiercache",
    ],
  },
  ...pluginVue.configs["flat/recommended"],
  vueTsConfigs.recommended,
  skipFormatting
);
