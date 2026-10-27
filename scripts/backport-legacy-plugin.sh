#!/usr/bin/env bash
#
# Copyright (C) 2026 Sonic Liberation
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.
#
# Bundles the legacy (pre-Zipline) plugin into a new .smplug so older Spotube
# versions keep working with the latest plugin release.
#
# - plugin.json: legacy and current manifests are merged; current wins on conflicts,
#   except abilities, which are unioned
# - other files: legacy-only files (e.g. plugin.out) are added; current files win
#
# Usage: scripts/backport-legacy-plugin.sh <current.smplug> <output.smplug> [legacy-tag]
#
# Requires: curl, jq, unzip, zip

set -euo pipefail

if [ $# -lt 2 ]; then
  echo "Usage: $0 <current.smplug> <output.smplug> [legacy-tag]" >&2
  exit 1
fi

CURRENT=$(realpath "$1")
OUTPUT=$(realpath -m "$2")
LEGACY_TAG=${3:-0.2.2}
REPO=${GITHUB_REPOSITORY:-pgwiz/spotube-plugin-spotify}
LEGACY_REPO=${LEGACY_REPO:-sonic-liberation/spotube-plugin-spotify}
LEGACY_URL="https://github.com/$LEGACY_REPO/releases/download/$LEGACY_TAG/plugin.smplug"

for cmd in curl unzip; do
  command -v "$cmd" >/dev/null || { echo "Missing required command: $cmd" >&2; exit 1; }
done

if ! command -v jq >/dev/null && ! command -v python3 >/dev/null && ! command -v python >/dev/null; then
  echo "Missing required JSON processor: jq or python/python3" >&2
  exit 1
fi
if ! command -v zip >/dev/null && ! command -v python3 >/dev/null && ! command -v python >/dev/null; then
  echo "Missing required archive utility: zip or python/python3" >&2
  exit 1
fi

WORK=$(mktemp -d)
trap 'rm -rf "$WORK"' EXIT

echo "Downloading legacy plugin from $LEGACY_URL"
curl -fsSL -o "$WORK/legacy.smplug" "$LEGACY_URL"

mkdir "$WORK/legacy" "$WORK/current"
unzip -q "$WORK/legacy.smplug" -d "$WORK/legacy"
unzip -q "$CURRENT" -d "$WORK/current"

for dir in legacy current; do
  [ -f "$WORK/$dir/plugin.json" ] || { echo "No plugin.json in $dir plugin" >&2; exit 1; }
done

# Shallow merge: every top-level key from the current manifest overrides the legacy one,
# except abilities, which are unioned so legacy-only abilities (e.g. authentication) survive
if command -v jq >/dev/null; then
  jq -s '.[0] + .[1] + {abilities: ((.[1].abilities // []) + (.[0].abilities // []) | reduce .[] as $a ([]; if index([$a]) then . else . + [$a] end))}' \
    "$WORK/legacy/plugin.json" "$WORK/current/plugin.json" > "$WORK/plugin.json"
else
  PY_CMD=$(command -v python3 || command -v python)
  "$PY_CMD" -c '
import json, sys
with open(sys.argv[1], "r", encoding="utf-8") as f:
    legacy = json.load(f)
with open(sys.argv[2], "r", encoding="utf-8") as f:
    current = json.load(f)
merged = {**legacy, **current}
leg_ab = legacy.get("abilities") or []
cur_ab = current.get("abilities") or []
merged["abilities"] = list(dict.fromkeys(cur_ab + leg_ab))
with open(sys.argv[3], "w", encoding="utf-8") as f:
    json.dump(merged, f, indent=2)
' "$WORK/legacy/plugin.json" "$WORK/current/plugin.json" "$WORK/plugin.json"
fi
mv "$WORK/plugin.json" "$WORK/current/plugin.json"

# Add legacy-only files without overwriting anything from the current build
(cd "$WORK/legacy" && find . -type f ! -name plugin.json) | while read -r f; do
  if [ ! -e "$WORK/current/$f" ]; then
    mkdir -p "$(dirname "$WORK/current/$f")"
    cp "$WORK/legacy/$f" "$WORK/current/$f"
    echo "Added legacy file: ${f#./}"
  fi
done

mkdir -p "$(dirname "$OUTPUT")"
rm -f "$OUTPUT"
if command -v zip >/dev/null; then
  (cd "$WORK/current" && zip -qrX "$OUTPUT" .)
else
  PY_CMD=$(command -v python3 || command -v python)
  "$PY_CMD" -c '
import zipfile, os, sys
out_path = sys.argv[1]
src_dir = sys.argv[2]
with zipfile.ZipFile(out_path, "w", zipfile.ZIP_DEFLATED) as zf:
    for root, _, files in os.walk(src_dir):
        for file in files:
            full = os.path.join(root, file)
            arcname = os.path.relpath(full, src_dir)
            zf.write(full, arcname)
' "$OUTPUT" "$WORK/current"
fi

echo "Merged plugin.json:"
cat "$WORK/current/plugin.json"
echo
echo "Wrote $OUTPUT"
