# Project Memory Bank & Architecture Decision Records (ADR)
_Durable knowledge — decisions, gotchas, conventions. Not a log._

## Architecture decisions
- **Keyless Spotify Embed Fallback**: When Spotify GraphQL endpoint `getTrack` returns null or throws 401/429 (e.g. unauthenticated sessions, anonymous users, rate limits), `RealMetadataTrackAPI` falls back to querying `https://open.spotify.com/embed/track/{id}` and parsing the embedded JSON payload inside `<script id="__NEXT_DATA__">`. This yields track metadata, artist info, album art, and direct 30s preview MP3 stream URLs from `p.scdn.co` without requiring OAuth tokens.

## Gotchas
- **Cross-platform string parsing in Kotlin/JS / Native**: Avoid JVM-only regex flags like `RegexOption.DOT_MATCHES_ALL` or relying on regex group syntax differences across Kotlin targets. Simple string delimiter matching (`indexOf("<script id=\"__NEXT_DATA__\"")` and `indexOf("</script>")`) is significantly faster and 100% portable across JVM, JS, and Native.
- **ResourceLoader on Android & iOS**: `ResourceLoader` in `spotify_gql_client` has target-specific `actual` implementations. On Android, unit tests run on the JVM host (`testAndroidHostTest`), so `ResourceLoader.android.kt` must delegate to `ClassLoader.getResourceAsStream`. On iOS, it uses `NSBundle.mainBundle.pathForResource`. Leaving them as `TODO()` breaks 40+ unit tests across the client module.
- **Gradle wrapper timeout on large distributions**: Default `networkTimeout=10000` (10s) causes socket timeouts on slow or high-latency connections during large distribution downloads (~150MB). Set to at least 600000 (10m).

## Conventions
- Always run `./gradlew packageProductionPlugin packageDevelopmentPlugin` before committing plugin changes to ensure both development and production Zipline bundles compile without JS or linking errors.

## Dead ends

