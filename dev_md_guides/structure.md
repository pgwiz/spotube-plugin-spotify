# Codebase Architecture & Directory Dependency Graph
_Last regenerated: 2026-10-27 09:45:08 UTC by dev-md-compactor_

## Project Manifests & Build Tools
- *(No standard build manifests detected at root)*

## Module Directory Topography

### `gradle/`
- **Files (2)**: `gradle-daemon-jvm.properties, libs.versions.toml`

### `gradle/wrapper/`
- **Files (2)**: `gradle-wrapper.jar, gradle-wrapper.properties`

### `root/`
- **Files (10)**: `AGENTS.md, CHANGELOG.md, LICENSE, README.MD, build.gradle.kts, gradle.properties, gradlew, gradlew.bat` (+2 more)

### `scripts/`
- **Files (1)**: `backport-legacy-plugin.sh`

### `spotify_gql_client/`
- **Files (2)**: `build.gradle.kts, local.properties`

### `spotify_gql_client/src/androidMain/`
- **Files (1)**: `AndroidManifest.xml`

### `spotify_gql_client/src/androidMain/kotlin/io/github/sonic_liberation/spotify_gql_client/loader/`
- **Files (1)**: `ResourceLoader.android.kt`

### `spotify_gql_client/src/commonMain/kotlin/io/github/sonic_liberation/spotify_gql_client/gql/`
- **Files (4)**: `GQLRequestModels.kt, Models.kt, SpotifyGQLBaseClient.kt, SpotifyGQLClient.kt`

### `spotify_gql_client/src/commonMain/kotlin/io/github/sonic_liberation/spotify_gql_client/gql/album/`
- **Files (2)**: `AlbumClient.kt, AlbumModels.kt`

### `spotify_gql_client/src/commonMain/kotlin/io/github/sonic_liberation/spotify_gql_client/gql/artist/`
- **Files (2)**: `ArtistClient.kt, ArtistModels.kt`

### `spotify_gql_client/src/commonMain/kotlin/io/github/sonic_liberation/spotify_gql_client/gql/auth/`
- **Files (2)**: `AuthClient.kt, AuthModels.kt`

### `spotify_gql_client/src/commonMain/kotlin/io/github/sonic_liberation/spotify_gql_client/gql/browse/`
- **Files (2)**: `BrowseClient.kt, BrowseModels.kt`

### `spotify_gql_client/src/commonMain/kotlin/io/github/sonic_liberation/spotify_gql_client/gql/library/`
- **Files (2)**: `LibraryClient.kt, LibraryModels.kt`

### `spotify_gql_client/src/commonMain/kotlin/io/github/sonic_liberation/spotify_gql_client/gql/playlist/`
- **Files (2)**: `PlaylistClient.kt, PlaylistModels.kt`

### `spotify_gql_client/src/commonMain/kotlin/io/github/sonic_liberation/spotify_gql_client/gql/search/`
- **Files (2)**: `SearchClient.kt, SearchModels.kt`

### `spotify_gql_client/src/commonMain/kotlin/io/github/sonic_liberation/spotify_gql_client/gql/tracks/`
- **Files (2)**: `TracksClient.kt, TracksModels.kt`

### `spotify_gql_client/src/commonMain/kotlin/io/github/sonic_liberation/spotify_gql_client/gql/user/`
- **Files (2)**: `UserClient.kt, UserModels.kt`

### `spotify_gql_client/src/commonMain/kotlin/io/github/sonic_liberation/spotify_gql_client/loader/`
- **Files (1)**: `ResourceLoader.kt`

### `spotify_gql_client/src/commonMain/kotlin/io/github/sonic_liberation/spotify_gql_client/services/`
- **Files (1)**: `UserAgents.kt`

### `spotify_gql_client/src/commonTest/kotlin/io/github/sonic_liberation/spotify_gql_client/`
- **Files (8)**: `AlbumClientTest.kt, ArtistClientTest.kt, BrowseClientTest.kt, LibraryClientTest.kt, PlaylistClientTest.kt, SearchClientTest.kt, TracksClientTest.kt, UserClientTest.kt`

### `spotify_gql_client/src/commonTest/kotlin/io/github/sonic_liberation/spotify_gql_client/integration/`
- **Files (1)**: `RealHttpClientAPI.kt`

### `spotify_gql_client/src/commonTest/kotlin/io/github/sonic_liberation/spotify_gql_client/mocks/`
- **Files (3)**: `MockCredentialsFromCookieResult.kt, MockHTTPClientAPI.kt, MockSystemInfoAPI.kt`

### `spotify_gql_client/src/commonTest/resources/`
- **Files (40)**: `addTracksToPlaylist_200.json, albums_200.json, appearsOnPlaylists_200.json, artists_200.json, changePermission_200.json, createPlaylist_200.json, discographyAll_200.json, discographyOverview_200.json` (+32 more)

### `spotify_gql_client/src/iosMain/kotlin/io/github/sonic_liberation/spotify_gql_client/loader/`
- **Files (1)**: `ResourceLoader.ios.kt`

### `spotify_gql_client/src/jsMain/kotlin/io/github/sonic_liberation/spotify_gql_client/loader/`
- **Files (1)**: `ResourceLoader.js.kt`

### `spotify_gql_client/src/jvmMain/kotlin/io/github/sonic_liberation/spotify_gql_client/loader/`
- **Files (1)**: `ResourceLoader.jvm.kt`

### `spotube_plugin_spotify/`
- **Files (3)**: `build.gradle.kts, local.properties, logo.png`

### `spotube_plugin_spotify/src/commonMain/kotlin/io/github/sonic_liberation/spotube_plugin_spotify/core/`
- **Files (8)**: `CoreModels.kt, RealMetadataAlbumAPI.kt, RealMetadataArtistAPI.kt, RealMetadataBrowseAPI.kt, RealMetadataPlaylistAPI.kt, RealMetadataSearchAPI.kt, RealMetadataTrackAPI.kt, RealMetadataUserAPI.kt`

### `spotube_plugin_spotify/src/commonMain/kotlin/io/github/sonic_liberation/spotube_plugin_spotify/services/`
- **Files (1)**: `TOTP.kt`

### `spotube_plugin_spotify/src/commonTest/kotlin/io/github/sonic_liberation/spotube_plugin_spotify/core/`
- **Files (6)**: `RealMetadataAlbumAPITest.kt, RealMetadataArtistAPITest.kt, RealMetadataBrowseAPITest.kt, RealMetadataPlaylistAPITest.kt, RealMetadataSearchAPITest.kt, RealMetadataTrackAPITest.kt`

### `spotube_plugin_spotify/src/commonTest/kotlin/io/github/sonic_liberation/spotube_plugin_spotify/integration/`
- **Files (1)**: `RealHttpClientAPI.kt`

### `spotube_plugin_spotify/src/commonTest/kotlin/io/github/sonic_liberation/spotube_plugin_spotify/mocks/`
- **Files (2)**: `MockCredentialsFromCookieResult.kt, MockSystemInfoAPI.kt`

### `spotube_plugin_spotify/src/jsMain/kotlin/io/github/sonic_liberation/spotube_plugin_spotify/`
- **Files (1)**: `js.kt`

### `spotube_plugin_spotify/src/jsMain/kotlin/io/github/sonic_liberation/spotube_plugin_spotify/core/`
- **Files (1)**: `RealCoreAPI.kt`

## Environment & Directory Catalog Reference
- Central Catalog: `dev_md_guides/directory.md` (sample committed as `dev_md_guides/directory.md.sample`).
- All workspace paths, servers, backend links, frontend links, and external endpoints are centralized in this catalog.
- Invariant: Never hardcode local filesystem paths or network URLs directly across project markdown files.

## Credentials & Secrets Reference
- Central Secrets Schema: `dev_md_guides/credentials.md` (sample committed as `dev_md_guides/credentials.md.sample`).
- Real secrets and sensitive tokens are kept strictly local in `credentials.md` and MUST NEVER be committed to GitHub.
- Invariant: Never commit credentials to GitHub; if the user ever specifies committing credentials, the agent must first explicitly warn the user about critical security risks.

## Architectural Invariants & Boundary Rules
- Internal modules should adhere to defined dependency boundaries without cyclic imports.
- Configuration, secrets, and environment overrides must not be hardcoded in application logic.
- Zero Hardcoded Endpoints: Do not hardcode machine directories, server IPs, backend links, or frontend links across markdown docs; resolve and reference them via directory.md (only directory.md.sample is committed to version control).
- Zero Credential Exposure: Never commit credentials.md or real secrets to git/GitHub. Only credentials.md.sample with sanitized placeholders is tracked. If the user explicitly asks to commit credentials, issue a critical security warning and require confirmation before proceeding.
