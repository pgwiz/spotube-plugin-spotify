# Procedural Execution Flows & Operational Recipes

_Standard execution workflows, tool-chain recipes, and step-by-step verification procedures._
_Maintained by the dev-md-compactor Conversational Reflection Protocol._

---

## 1. Test & Quality Gate Execution Flow

```bash
# Step 1: Run comprehensive test discovery
python -m unittest discover tests

# Step 2: Verify zero test failures and clean exit code
```

---

## 2. Context Compaction & Living Documentation Sync Flow

```bash
# Step 1: Preview ground truth state
python dev-md-compactor/scripts/run_compactor.py --report-only

# Step 2: Synchronize dev_md_guides/
python dev-md-compactor/scripts/run_compactor.py
```

---

## 3. Git Branching, Secret Safety & PR Update Flow

```bash
# Step 1: Verify dev_md_guides/credentials.md is not tracked
git ls-files dev_md_guides/credentials.md

# Step 2: Stage and commit changes
git add <files>
git commit -m "feat: description"
```
