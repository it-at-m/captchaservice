# Git Hooks

Husky-managed hooks for this repository: `pre-commit`, `commit-msg`.

**Documentation:** [Git hooks (Husky)](../docs/en/community/git-hooks.md) (English) · [Git-Hooks (Husky)](../docs/de/community/git-hooks.md) (Deutsch)

**Setup (once per clone, repository root):**

```bash
npm install
```

`prepare` runs automatically and wires Git to `.husky/`.

For doc changes, install docs dependencies once: `cd docs && npm install`.
