# Changelog

## [Unreleased]

### Fixes
- **loader**: Implement platform `ResourceLoader` on Android (`classLoader.getResourceAsStream`) and iOS (`NSBundle.mainBundle.pathForResource`), resolving 40 unit test failures in `:spotify_gql_client:testAndroidHostTest`.
- **spotify_gql_client & spotube_plugin_spotify**: Add keyless Spotify embed fallback parsing `https://open.spotify.com/embed/track/{id}` (`<script id="__NEXT_DATA__">`) extracting track title, artists, duration, thumbnails, and direct preview audio URL (`p.scdn.co`) when GQL `getTrack` returns null, 401, or 429.
- **build**: Increase Gradle wrapper network timeout to 600,000ms (10m) to prevent socket read timeouts on large distribution downloads.

## 1.0.0

### Highlights
- **Full rewrite in Kotlin Multiplatform (Kotlin/JS).** The plugin is now compiled to a Zipline plugin instead of the previous implementation (#76).
- **Update checks.** The plugin now checks GitHub releases for new versions.
- **Legacy plugin backport script.** This is for older plugin versions.

### Features
- Implement the GitHub release check for plugin updates (`b2045a5`).
- Update the plugin version to 1.0.0 and add a legacy plugin backport script (`b591463`).
- Support channel selection in the release workflow, and bump `spotube-gradle` to 0.1.2 (`e05ca8e`).

### Fixes
- Improve session token refresh handling to prevent 401 errors (`cac80dd`).
- Update the `sha256Hash` values for `libraryV3` operations (`c948254`).
- Update plugin interface references from `dev.krtirtho` to `org.spotube` (`81eb52b`).
- Use the GitHub API for the gist (`134d87d`).
- Correct an invalid nuances URL (`db2b697`).
- Fix the user profile not being fetched (`c391199`).
- Fix tracks of albums, artists and playlists not loading (`a8ccb0e`).
- Fix an audiobooks bug (#32) (`31f7bbd`).

### CI / Release
- The release workflow uses the `continuous` tag instead of `nightly` (`888cec6`).
