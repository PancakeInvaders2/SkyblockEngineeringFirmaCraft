#!/usr/bin/env bash
set -euo pipefail


root_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/../.." && pwd)"
pack_dir="$root_dir/modpack"
packwiz_bin="${PACKWIZ_BIN:-packwiz}"
instance_dir="${PACKWIZ_INSTANCE_DIR:-/mnt/LinuxSSD/Games/Minecraft/instances/1.21.1_dev_packwiz/minecraft}"
source_mods="${PACKWIZ_SOURCE_MODS:-/mnt/LinuxSSD/Games/Minecraft/dev/modCache}"
bootstrap_jar="$instance_dir/packwiz-installer-bootstrap.jar"

if [[ ! -f "$bootstrap_jar" ]]; then
    printf 'Packwiz installer bootstrap not found: %s\n' "$bootstrap_jar" >&2
    exit 1
fi

for filename in \
    'Immersive Crate View (WIMC) Mod Porting-0.1.39.jar' \
    'tfcnitrary-1.21.1-0.1.2.jar'; do
    target="$pack_dir/mods/$filename"
    if [[ ! -f "$target" ]]; then
        if [[ ! -f "$source_mods/$filename" ]]; then
            printf 'Missing CurseForge-excluded JAR "%s" in %s\n' "$filename" "$source_mods" >&2
            exit 1
        fi
        cp -- "$source_mods/$filename" "$target"
    fi
done

stage_mod() {
    local module_dir="$1"
    local jar_prefix="$2"
    local staged_name="$3"
    local build_dir="$module_dir/build/libs"
    local -a jars

    if [[ ! -d "$build_dir" ]]; then
        printf 'Build output directory not found: %s\n' "$build_dir" >&2
        exit 1
    fi

    mapfile -t jars < <(
        find "$build_dir" -maxdepth 1 -type f -name "${jar_prefix}-*.jar" \
            ! -name '*-sources.jar' ! -name '*-javadoc.jar' -print | sort
    )
    if [[ "${#jars[@]}" -ne 1 ]]; then
        printf 'Expected one built JAR for %s; found %s.\n' "$jar_prefix" "${#jars[@]}" >&2
        printf 'Run the module build and inspect %s.\n' "$build_dir" >&2
        exit 1
    fi

    cp -- "${jars[0]}" "$pack_dir/mods/$staged_name"
}

for module in skyblock-tfc tfctriphammerpp; do
    module_dir="$root_dir/mods/$module"
    "$module_dir/gradlew" -p "$module_dir" build
done

mkdir -p "$pack_dir/mods"
stage_mod "$root_dir/mods/skyblock-tfc" "SkyblockTFC" "SkyblockTFC-local.jar"
stage_mod "$root_dir/mods/tfctriphammerpp" "TFCTripHammer++" "TFCTripHammerPP-local.jar"

cd "$pack_dir"
"$packwiz_bin" refresh

pack_url="$(python3 -c 'from pathlib import Path; import sys; print(Path(sys.argv[1]).resolve().as_uri())' "$pack_dir/pack.toml")"
cd "$instance_dir"
exec java -jar "$bootstrap_jar" -g "$pack_url"
