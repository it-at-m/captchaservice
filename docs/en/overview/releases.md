# Releases

CaptchaService does not maintain an in-repository changelog. All releases are published on GitHub and as artifacts on Maven Central.

- **GitHub releases**: [github.com/it-at-m/captchaservice/releases](https://github.com/it-at-m/captchaservice/releases)
- **Maven Central**: artifacts are published under the `de.muenchen.captchaservice` group.

The latest release tag is also shown next to the site title in the top navigation; it is fetched live from the GitHub Releases API.

## Maven Coordinates

```xml
<dependency>
    <groupId>de.muenchen.captchaservice</groupId>
    <artifactId>captchaservice-backend</artifactId>
    <version>${version.captchaservice}</version>
</dependency>
```

Pin to an explicit version — omitting the version tag is **not recommended**.

## Release Process

The repository uses the shared Munich CI templates to build and release:

- **Build**: [`.github/workflows/maven-node-build.yml`](https://github.com/it-at-m/captchaservice/blob/main/.github/workflows/maven-node-build.yml)
- **Release**: [`.github/workflows/maven-release.yml`](https://github.com/it-at-m/captchaservice/blob/main/.github/workflows/maven-release.yml) and [`.github/workflows/npm-release.yml`](https://github.com/it-at-m/captchaservice/blob/main/.github/workflows/npm-release.yml) for any companion npm artifacts
- **Reusable actions**: [`it-at-m/lhm_actions`](https://github.com/it-at-m/lhm_actions)

To create a release:

1. Open the **Actions** tab in GitHub and select the release workflow.
2. Run the workflow. It will:
   - Build with tests skipped via the Maven `release` profile.
   - Sign and deploy artifacts to Maven Central via the Sonatype Central publishing plugin.
   - Open a pull request with the updated snapshot version (when `use-pr` is enabled).
