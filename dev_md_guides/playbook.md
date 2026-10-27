# Operational Playbooks & Framework Runbooks (`dev_md_guides/playbook.md`)

_Battle-tested operational execution recipes, multi-step runbooks, and framework verification workflows._  
_Maintained and updated in-place by the dev-md-compactor Conversational Reflection Protocol._

> [!NOTE]
> **Difference from `flow.md` and `solution.md`**:
> - `flow.md` defines high-level architectural lifecycle flows (e.g. Git branching strategy, context compaction lifecycle).
> - `solution.md` tracks empirical problem resolutions (the "what worked" evolution: Baseline → X+Y → Proven Truth).
> - **`playbook.md`** provides **concrete, reproducible execution runbooks** for complex tooling, build chains, framework operations, and operational verification.

---

## 1. Playbook Schema

Each playbook entry follows this anatomy:
- **`PLAY-XXX: [Operational Objective]`**
- **Category**: `Build & Packaging` | `Testing & QA` | `Deployment & Sync` | `Environment Setup` | `Recovery`
- **Prerequisites**: Required CLI tools, environment variables (referenced from `directory.md`), permissions.
- **Proven Step-by-Step Execution Sequence**: Exact commands with verified flags and parameters.
- **Expected Verification Output**: Exact console outputs or health check responses confirming success.
- **Failure Recovery & Rollback Protocol**: Exact steps if any step fails.
- **Last Verified**: Date and environment tested.

---

## 2. Active Operational Playbooks

### PLAY-001: Universal Multi-Target Skill Installation & Verification
- **Category**: `Deployment & Sync`
- **Prerequisites**: Windows PowerShell (or pwsh 7+) on Windows, POSIX `/bin/sh` or bash on Linux/macOS.
- **Execution Runbook**:
  1. *Windows Local Repository Run*:
     ```powershell
     powershell -ExecutionPolicy Bypass -File .\install\install.ps1 -Target all -Skill all -Yes
     ```
  2. *macOS / Linux / WSL Local Run*:
     ```bash
     ./install/install.sh --target all --skill all -y
     ```
  3. *Targeted Antigravity Verification*:
     Verify that `~/.gemini/config/skills/dev-md-compactor` and `~/.gemini/config/skills/agent-memory` exist and contain active `SKILL.md` files:
     ```powershell
     Test-Path "$HOME\.gemini\config\skills\dev-md-compactor\SKILL.md"
     Test-Path "$HOME\.gemini\config\skills\agent-memory\SKILL.md"
     ```
- **Expected Verification Output**: `Install complete!` with green checkmarks listing all installed paths.
- **Failure Recovery**: If a syntax error occurs due to PowerShell CP1252 parsing, verify all scripts are strictly 7-bit ASCII without unicode checkmarks.
- **Last Verified**: 2026-10-02 (Windows 11 & WSL)

---

### PLAY-002: Full-Spectrum Automated Compactor Regression Run
- **Category**: `Testing & QA`
- **Prerequisites**: Python 3.10+, git repository initialized.
- **Execution Runbook**:
  1. *Execute Unittest Suite*:
     ```bash
     python -m unittest discover tests -v
     ```
  2. *Execute Report-Only Extraction Pass*:
     ```bash
     python dev-md-compactor/scripts/run_compactor.py --report-only
     ```
  3. *Verify Git Worktree Cleanliness*:
     ```bash
     git status --porcelain
     ```
     Ensure no unintended files are created or unstaged outside `dev_md_guides/`.
- **Expected Verification Output**: `Ran 27+ tests ... OK` and exit code 0.
- **Failure Recovery**: If secret leak warnings appear, verify whether a test fixture inadvertently left unredacted keys in a tracked markdown guide.
- **Last Verified**: 2026-10-02 (Windows 11)

---

### PLAY-003: Clean Repository Compaction & Guide Synchronization
- **Category**: `Build & Packaging`
- **Prerequisites**: Clean working tree or understood diff.
- **Execution Runbook**:
  1. Run the deterministic compactor:
     ```bash
     python dev-md-compactor/scripts/run_compactor.py
     ```
  2. Inspect the updated master inventory in `dev_md_guides/agent.md`.
  3. Verify that `dev_md_guides/credentials.md` and `dev_md_guides/directory.md` remain strictly uncommitted:
     ```bash
     git ls-files dev_md_guides/credentials.md dev_md_guides/directory.md
     ```
     (Should return empty output).
- **Expected Verification Output**: `[COMPACTOR] Successfully synchronized dev_md_guides/ ...`
- **Failure Recovery**: If `credentials.md` is detected in `git ls-files`, immediately run `git rm --cached dev_md_guides/credentials.md`.
- **Last Verified**: 2026-10-02
