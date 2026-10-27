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

### GOTCHA-003: GitHub Actions Push Tag Filter Uses fnmatch (Not Regex) and Expression Boolean Coercion
- **Severity**: Major Blocker
- **Trigger / Symptom**: GitHub Actions release workflow failed to trigger on pushed tags like `1.0.0`, and `CHANNEL` resolved to `"true"` rather than `"continuous"` or `"stable"`.
- **Root Cause ("Why it failed")**: `push.tags` in GHA uses `fnmatch` where `+` is a literal plus, not a regex quantifier (`[0-9]+.[0-9]+.[0-9]+*` required literal plus signs). Furthermore, GHA expressions treat `&&` and `||` strictly as boolean operators returning `true`/`false`.
- **Exact Fix**: Updated `push.tags` patterns to valid globs (`'v*'`, `'*.*.*'`, `'[0-9]*'`, `'continuous'`), and moved channel/tag name resolution into bash script logic.
- **Mechanics ("Why the fix worked")**: Standard glob matching matches all semver and release tags, and native bash conditional evaluation preserves string literals without boolean coercion.

### GOTCHA-004: GitHub Repository Search API Excludes Forks Unless `fork:true` is Specified
- **Severity**: Major Blocker
- **Trigger / Symptom**: Spotube's Discover tab could not locate `pgwiz/spotube-plugin-spotify` despite having the release and metadata.
- **Root Cause ("Why it failed")**: GitHub's search API excludes forked repositories by default when querying `topic:spotube-zipline-plugin`. Additionally, the repository initially lacked the `spotube-zipline-plugin` topic.
- **Exact Fix**: Added repository topics (`spotube-zipline-plugin`, `spotube`, `spotube-plugin`, `spotube-metadata-plugin`) to `pgwiz/spotube-plugin-spotify` via `gh repo edit`, and updated Spotube's `GitHubPluginRepository.kt` query to `topic:spotube-zipline-plugin fork:true`.
- **Mechanics ("Why the fix worked")**: Guarantees that official plugins, community plugins, and verified forks appear in Spotube's in-app plugin catalog.

### GOTCHA-005: AGP CheckAarMetadata Failure with Android SDK 36.1 on `plugin_interfaces:0.1.0`
- **Severity**: Major Blocker
- **Trigger / Symptom**: `./gradlew check` failed with `:spotify_gql_client:checkAndroidMainAarMetadata` stating dependency `org.spotube.spotube:plugin_interfaces-android:0.1.0` requires compiling against Android API version 37 or later.
- **Root Cause ("Why it failed")**: `spotify_gql_client/build.gradle.kts` was pinned to `compileSdk { version = release(36) { minorApiLevel = 1 } }`.
- **Exact Fix**: Updated `compileSdk` to `release(37)`.
- **Mechanics ("Why the fix worked")**: Satisfies the AAR metadata constraint declared by `plugin_interfaces-android:0.1.0` and allows Gradle AAR checks to pass cleanly.


