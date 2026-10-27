# Unofficial Spotify plugin for Spotube

Spotify metadata plugin for [Spotube](https://spotube.cc).

## Installation

### Manual Install via URL (in Spotube)

You can install this plugin directly in Spotube by copying and pasting a plugin URL:

1. In Spotube, open **Settings** > **Plugins** (or onboarding setup).
2. Click **Install a Plugin** (or the **+** button).
3. Under **Download from URL**, paste one of the following URLs:

- **Latest Release (Recommended)**:
  ```text
  https://github.com/pgwiz/spotube-plugin-spotify/releases/latest/download/plugin.smplug
  ```

- **Universal / Backported Release (v1.0.0)**:
  ```text
  https://github.com/pgwiz/spotube-plugin-spotify/releases/download/1.0.0/plugin.smplug
  ```

- **Production Zipline Build (v1.0.0)**:
  ```text
  https://github.com/pgwiz/spotube-plugin-spotify/releases/download/1.0.0/plugin-production.smplug
  ```

- **Legacy Plugin Reference (for older Spotube versions pre-Zipline)**:
  ```text
  https://github.com/sonic-liberation/spotube-plugin-spotify/releases/download/0.2.2/plugin.smplug
  ```
  *(Format: `https://github.com/$LEGACY_REPO/releases/download/$LEGACY_TAG/plugin.smplug` where `$LEGACY_REPO=sonic-liberation/spotube-plugin-spotify` and `$LEGACY_TAG=0.2.2`)*

4. Click **Download**, review requested permissions, and confirm installation.
5. In **Default Providers**, select **Spotify** as your **Metadata** provider.

## Build

```bash
# Compiles to .smplug (development)
./gradlew :spotube_plugin_spotify:packageDevelopmentPlugin

# Compiles production plugin
./gradlew :spotube_plugin_spotify:packageProductionPlugin

# Backport legacy plugin (combines production with legacy pre-Zipline plugin)
bash scripts/backport-legacy-plugin.sh spotube_plugin_spotify/build/distributions/plugin-production.smplug spotube_plugin_spotify/build/distributions/plugin.smplug 0.2.2
```

## License

This project is licensed under the [GNU Affero General Public License v3.0 or later](LICENSE).

> This project is not associated with Spotube nor Spotify.