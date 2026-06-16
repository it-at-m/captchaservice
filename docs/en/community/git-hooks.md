# Git Hooks (Husky)

This repository uses [Husky](https://github.com/typicode/husky) to run Git hooks from the root `.husky/` directory. Hooks run automatically during commits to keep commit messages, documentation formatting, and Java code style consistent.

Hook scripts live in [`.husky/`](https://github.com/it-at-m/captchaservice/tree/main/.husky) (`pre-commit`, `commit-msg`).

## Setup

Hooks are configured when you run at the **repository root**:

```bash
npm install
```

The `prepare` script runs automatically and points Git at `.husky/`.

For documentation work, install docs dependencies once:

```bash
cd docs && npm install
```

## Hooks

### `pre-commit`

No-op placeholder. Git always runs `pre-commit` before `commit-msg`; the real checks are in `commit-msg` so the **commit subject is validated first**.

### `commit-msg`

All checks run in this hook, in **fail-fast** order:

1. **Commit message** — subject line from the message file Git passes to this hook
2. **Docs formatting** — Prettier check in `docs/` when staged files are under `docs/` (`npm run format:check`)
3. **Java code style** — Spotless check via Maven when staged `*.java` files are present

**Behavior**

- Commit message and docs checks **block** the commit on failure
- Spotless runs only when Java files are staged; if Maven is not installed locally, Spotless is skipped with a warning

**Commit message format** (step 1):

```txt
type(PROJECT-123): commit message
type(PROJECT): commit message
```

The ticket number is **optional** — use `CAPTCHA-123` or just `CAPTCHA` (uppercase).

**Valid types:** `feat`, `fix`, `clean`, `chore`, `docs`

**Valid projects:** `CAPTCHA`, `GH`

**Merge commits**

Git’s default merge subjects (for example `Merge branch 'main' into feature-branch`) are **allowed automatically** so `git merge` can finish without renaming the message.

## Troubleshooting

### Docs formatting fails

```bash
cd docs
npm run format
```

### Spotless fails

```bash
mvn -f captchaservice-backend/pom.xml spotless:apply
```

### Skip hooks temporarily

```bash
git commit --no-verify -m "chore(CAPTCHA): emergency hotfix"
```

Use sparingly — CI still enforces the same checks.

## Further reading

- [Contributing](./contributing.md)
- [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/)
