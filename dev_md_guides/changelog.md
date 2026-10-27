# Session Operational Changelog
_Append-only. Newest first. Never edit past entries._

## [2026-10-27 09:43:00 UTC] — Branch `main` (HEAD: `79be863`)
- **Event**: Plugin Release 1.0.0 & Release Workflow Modernization
- **Operational Scope**: Created GitHub Release `1.0.0` on `pgwiz/spotube-plugin-spotify` with dual assets (`plugin.smplug` and `plugin-production.smplug`), enhanced GitHub Actions release workflow with tag triggers and idempotent upload, added cross-platform Python fallback to `scripts/backport-legacy-plugin.sh`, updated README and `RealCoreAPI.kt` with manual install URLs and legacy reference, and documented manual install flow in Spotube.

### AST & Code Modifications
- `spotube-plugin-spotify/.github/workflows/release.yml`:
  - Added `push: tags: ['v*', '[0-9]+.[0-9]+.[0-9]+*', 'continuous']` triggers.
  - Set concurrency to `publish-${{ github.event.inputs.channel || github.ref }}`.
  - Updated checkout and java setup actions to `@v4`.
  - Added upload of `plugin-production.smplug` alongside `plugin.smplug`.
  - Made release step idempotent using `gh release view` and `gh release upload --clobber`.
- `spotube-plugin-spotify/scripts/backport-legacy-plugin.sh`:
  - Added Python 3 fallback when `jq` or `zip` utilities are absent.
  - Enabled clean cross-platform execution on Windows and Linux.
- `spotube-plugin-spotify/spotube_plugin_spotify/src/jsMain/kotlin/io/github/sonic_liberation/spotube_plugin_spotify/core/RealCoreAPI.kt`:
  - Updated `supportMarkdownText` to return copyable manual download URLs and legacy reference.
- `spotube-plugin-spotify/README.MD`:
  - Added comprehensive "Installation > Manual Install via URL (in Spotube)" guide with copyable URLs for latest, v1.0.0, and legacy release `0.2.2`.
- `spotube/composeApp/src/commonMain/kotlin/org/spotube/spotube/modules/plugin/PluginScreen.kt`:
  - Added `"pgwiz"` and `"sonic-liberation"` to `VERIFIED_PLUGIN_OWNERS`.
- `spotube/README.md`:
  - Added "Plugins" section documenting manual installation via URL.

### Command Results
- `./gradlew :spotube_plugin_spotify:packageProductionPlugin`: BUILD SUCCESSFUL.
- `bash scripts/backport-legacy-plugin.sh ...`: Generated `plugin.smplug` (927,660 bytes) and `plugin-production.smplug` (905,457 bytes).
- `gh release create 1.0.0 ... --repo pgwiz/spotube-plugin-spotify`: Published release with both assets.
- `git push origin main` / `git push origin master`: Both repos synchronized to GitHub.

---

