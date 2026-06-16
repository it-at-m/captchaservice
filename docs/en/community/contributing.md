# Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

To learn more about how you can contribute, please read the Munich open-source contribution guide on the [RefArch documentation site](https://refarch.oss.muenchen.de/contribute).

## Quick Path

1. Fork the [`it-at-m/captchaservice`](https://github.com/it-at-m/captchaservice) repository on GitHub.
2. Create your feature branch (`git checkout -b feature/your-improvement`).
3. Run `npm install` at the repository root to enable [Git hooks (Husky)](./git-hooks.md).
4. Run `bash runLocal.sh` against the Compose stack (see [Quick Start](../getting-started/quick-start.md)) to verify your changes locally.
5. Commit using the conventional format (`type(CAPTCHA): summary`), push, and open a Pull Request against `main`.

The CI workflows ([`maven-node-build.yml`](https://github.com/it-at-m/captchaservice/blob/main/.github/workflows/maven-node-build.yml), [`actionlint.yml`](https://github.com/it-at-m/captchaservice/blob/main/.github/workflows/actionlint.yml), [`codeql.yml`](https://github.com/it-at-m/captchaservice/blob/main/.github/workflows/codeql.yml), [`dependency-review.yml`](https://github.com/it-at-m/captchaservice/blob/main/.github/workflows/dependency-review.yml), [`dockercompose-healthcheck.yml`](https://github.com/it-at-m/captchaservice/blob/main/.github/workflows/dockercompose-healthcheck.yml)) gate every Pull Request.

## Code of Conduct

Please read and follow our [Code of Conduct](https://github.com/it-at-m/captchaservice/blob/main/.github/CODE_OF_CONDUCT.md) when participating in this project.

## License

CaptchaService is distributed under the [MIT License](https://github.com/it-at-m/captchaservice/blob/main/LICENSE). By contributing, you agree that your contributions will be licensed under the same license.
