# Project Gotchas & Failure Mode Analysis

_Categorized operational failures, non-obvious runtime bugs, and battle-tested fixes._
_Maintained by the dev-md-compactor Conversational Reflection Protocol._

---

## 1. Major Blockers (Fatal Crashes, Leaks & Data Loss Risks)

### GOTCHA-001: Missing `jq` or `zip` in Minimal and Windows Environments
- **Severity**: Major Blocker
- **Trigger / Symptom**: `scripts/backport-legacy-plugin.sh` failed with `Missing required command: jq` when executed on Windows Git Bash or lean containers without Linux package managers.
- **Root Cause ("Why it failed")**: The script strictly required POSIX `jq` and `zip` CLI binaries.
- **Exact Fix**: Implemented transparent in-line Python 3 fallbacks for `plugin.json` manipulation and zip archive generation when `jq` or `zip` commands are missing from PATH.
- **Mechanics ("Why the fix worked")**: Python is universally available across development and CI environments, and Python's built-in `json` and `zipfile` modules perform lossless manifest unioning and ZIP packing.

---

## 2. Minor Quirks (Silent Defaults, Formatting & Syntax Edge Cases)

### GOTCHA-002: GitHub CLI `gh release create` Failure on Pre-Existing Release/Tag
- **Severity**: Minor Quirk
- **Trigger / Symptom**: Running `gh release create "$VERSION"` fails if the release or git tag was already created or if the CI workflow is re-triggered on an existing tag.
- **Root Cause ("Why it failed")**: `gh release create` expects the release not to exist unless specifically instructed.
- **Exact Fix**: In `release.yml`, inspect release existence using `gh release view "$TAG_NAME" >/dev/null 2>&1`; if present, run `gh release upload "$TAG_NAME" ... --clobber`; otherwise create it fresh.
- **Mechanics ("Why the fix worked")**: Guarantees idempotent CI execution regardless of whether a release was triggered manually or via tag push.

