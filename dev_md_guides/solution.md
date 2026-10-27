# Validated Solutions & Empirical Truths (`dev_md_guides/solution.md`)

_The living, mutable catalog of battle-tested problem resolutions, working implementations, and empirical ground truths._  
_Maintained in-place by the dev-md-compactor Conversational Reflection Protocol._

> [!NOTE]
> **Difference from `changelog.md` and `gotchas.md`**:
> - `changelog.md` is an append-only historical audit trail; past entries are frozen.
> - `gotchas.md` documents failure modes and crashes (negative knowledge: "what broke").
> - **`solution.md`** documents the **mutable, latest proven truth** (positive knowledge: "what actually works").
> As iterations progress and parameters (X + Y) are refined, update each entry in-place so agents always load the current golden formula rather than re-discovering fragile baselines.

---

## 1. Solution Schema

Each solution entry follows this anatomy:
- **`SOL-XXX: [Descriptive Title]`**
- **Status**: `Verified` (battle-tested in production/CI) | `Iterating` (works but undergoing refinement) | `Superseded` (replaced by newer solution).
- **Target Context**: Framework, language runtime, OS/platform, toolchain.
- **Problem & Goal**: What specific capability or fix was needed.
- **Evolution Ledger**:
  1. *Baseline / Initial Attempt*: The first approach that worked partially or had edge cases.
  2. *Refinements (Adding X + Y)*: The parameter adjustments, flags, timing, or wrapper logic that stabilized behavior.
  3. *Latest Proven Truth (The Golden Formula)*: The exact code snippet, command invocation, or architectural pattern that works reliably today.
- **What Did NOT Work (Anti-Patterns / Traps)**: Approaches that seemed viable or were suggested by docs but failed in practice.
- **Why It Works (Mechanics)**: The underlying technical reason this combination behaves predictably.
- **Verification Evidence**: Automated test name, CLI exit code, or verification log.

---

## 2. Active Solutions Directory

<!-- SOLUTIONS CATALOG: Add new entries below with latest proven truths -->

### SOL-001: Robust UTF-8 Subprocess Execution on Windows Without CP1252 Crashes
- **Status**: `Verified`
- **Target Context**: Python 3.10+ CLI / Subprocess on Windows & POSIX
- **Problem & Goal**: Execute external shell commands (git, tree, compilers) and capture stdout/stderr without crashing on emojis or multibyte developer names.
- **Evolution Ledger**:
  1. *Baseline*: `subprocess.run(cmd, capture_output=True, text=True)`.
     - *Result*: Succeeded on standard English text, but crashed with `UnicodeDecodeError: 'charmap' codec can't decode byte...` when git logs contained non-ASCII chars or emojis on Western Windows (defaulting to CP1252).
  2. *Refinement 1 (Adding X)*: Added `encoding="utf-8"`.
     - *Result*: Resolved standard UTF-8 characters, but crashed on broken surrogate sequences from legacy Windows command output.
  3. *Latest Proven Truth (Adding X + Y)*:
     ```python
     res = subprocess.run(
         cmd,
         cwd=str(cwd) if cwd else None,
         capture_output=True,
         text=True,
         check=True,
         encoding="utf-8",
         errors="replace",
     )
     ```
- **What Did NOT Work**:
  - Running `chcp 65001` before each command (caused race conditions across concurrent workers and failed in restricted shells).
  - Leaving Python to default to `locale.getpreferredencoding()`.
- **Why It Works (Mechanics)**: `encoding="utf-8"` forces standard multibyte decoding, while `errors="replace"` safely substitutes unmappable bytes with the unicode replacement character `\ufffd`, eliminating fatal decode crashes.
- **Verification Evidence**: `tests/test_compactor.py::TestCompactorEngine::test_git_tracked_credentials_detection_and_security_check`

---

### SOL-002: Reliable Git Index Inspection for Ignored Secrets
- **Status**: `Verified`
- **Target Context**: Git CLI / Security Scanners
- **Problem & Goal**: Verify whether sensitive documentation files (`credentials.md`, `local.*.md`) are tracked in the repository, bypassing the blind spot where `.gitignore` does not apply to already-tracked files.
- **Evolution Ledger**:
  1. *Baseline*: Checking `git status --porcelain` and testing if files match `.gitignore`.
     - *Result*: Failed when a file was previously committed or staged with `git add -f`; git reported it as tracked, but `.gitignore` checks reported it as "ignored", creating a false sense of security.
  2. *Latest Proven Truth (Adding X + Y)*:
     ```python
     res = subprocess.run(["git", "ls-files", rel_path], cwd=root_dir, capture_output=True, text=True, encoding="utf-8")
     is_tracked = bool(res.stdout.strip() and rel_path in res.stdout)
     ```
     Coupled with scanning `git ls-files "*local*.md"` and `git status --porcelain` to catch staged index entries.
- **What Did NOT Work**:
  - Relying exclusively on `git check-ignore <file>` (returns exit code 0 even if the file is committed to git history).
- **Why It Works (Mechanics)**: `git ls-files` directly queries git's staging index and tree object cache, reporting files that git is actually tracking regardless of ignore rules.
- **Verification Evidence**: `tests/test_compactor.py::TestCompactorEngine::test_git_tracked_credentials_detection_and_security_check`

---

### SOL-003: Idempotent Dual-Asset Plugin Release Pipeline (Universal & Zipline Production)
- **Status**: `Verified`
- **Target Context**: GitHub Actions CI (`release.yml`), GitHub CLI (`gh`), Spotube Plugin Architecture
- **Problem & Goal**: Older Spotube installations require legacy pre-Zipline binaries (`plugin.out`), while modern installations use Kotlin/JS Zipline bytecode. The CI pipeline and releases must produce and publish both `plugin.smplug` (universal merged bundle) and `plugin-production.smplug` (pure modern bundle) seamlessly without race conditions or overwriting failures.
- **Evolution Ledger**:
  1. *Baseline*: Releasing only `plugin.smplug` via `gh release create "$VERSION" plugin.smplug`.
     - *Result*: Modern clients needing clean single-target builds lacked `plugin-production.smplug`, and re-running failed if the release or tag already existed.
  2. *Latest Proven Truth (Adding X + Y)*:
     - In `scripts/backport-legacy-plugin.sh`, download the legacy `0.2.2` release from `sonic-liberation`, union `abilities` and merge metadata using Python/jq, then pack `plugin.smplug`.
     - In `.github/workflows/release.yml`, package and upload both `plugin.smplug` and `plugin-production.smplug`:
       ```bash
       if gh release view "$TAG_NAME" >/dev/null 2>&1; then
         gh release upload "$TAG_NAME" plugin.smplug plugin-production.smplug --clobber
       else
         gh release create "$TAG_NAME" plugin.smplug plugin-production.smplug \
           --target "$GITHUB_SHA" --title "$TAG_NAME" --generate-notes
       fi
       ```
- **What Did NOT Work**:
  - Plain `gh release create` without `--clobber` or existence checking (crashes when re-running or when tag was pre-pushed).
  - Hardcoding POSIX-only `jq` and `zip` in the backport script (crashed on Windows dev hosts).
- **Why It Works (Mechanics)**: Checking `gh release view` first provides idempotency; bundling both files satisfies both modern and legacy Spotube client runtimes.
- **Verification Evidence**: GitHub release `1.0.0` published successfully at `https://github.com/pgwiz/spotube-plugin-spotify/releases/tag/1.0.0` containing both assets.

