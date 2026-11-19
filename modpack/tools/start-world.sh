#!/usr/bin/env bash
set -euo pipefail

instance_dir="${PACKWIZ_INSTANCE_DIR:-/mnt/LinuxSSD/Games/Minecraft/instances/1.21.1_dev_packwiz/minecraft}"
log_dir="${SKYBLOCK_LOG_DIR:-/tmp/skyblocktfc/logs}"
launcher_log="$log_dir/start-world.log"
log_offset_file="$log_dir/start-world.offset"
launch_started_file="$log_dir/start-world.started"
latest_log="$instance_dir/logs/latest.log"
startup_timeout=600
command="${1:-start}"

usage() {
    printf 'Usage: %s [start|status|wait [timeout-seconds]|logs|stop]\n' "$0"
}

game_pids() {
    local instance_parent
    instance_parent="$(dirname -- "$instance_dir")"
    ps -ww -eo pid=,comm=,args= |
        awk \
            -v instance_marker="-Djava.library.path=$instance_dir/natives" \
            -v parent_marker="-Djava.library.path=$instance_parent/natives" \
            '$2 == "java" && (index($0, instance_marker) || index($0, parent_marker)) { print $1 }'
}

current_run_log() {
    [[ -f "$latest_log" ]] || return 0

    if [[ -f "$log_offset_file" ]]; then
        local offset size
        offset="$(<"$log_offset_file")"
        size="$(stat -c '%s' "$latest_log")"
        if [[ "$offset" =~ ^[0-9]+$ ]] && (( size >= offset )); then
            tail -c "+$((offset + 1))" "$latest_log"
            return
        fi
    fi

    cat -- "$latest_log"
}

world_ready() {
    current_run_log | grep -E ' logged in with entity id | joined the game$' >/dev/null
}

progress_lines() {
    current_run_log |
        grep -Ei 'Starting integrated minecraft server|Loading Minecraft|Loading mod|Found mod file|Constructing|Preparing level|Preparing spawn|Done \(|Stopping server|Saving and pausing game|Saving chunks|ERROR|FATAL|Crash Report' |
        tail -n 8 || true
}

launcher_failure_lines() {
    if [[ -f "$launcher_log" ]]; then
        grep -Ei 'Failed to start Minecraft|The game crashed|Minecraft crashed|process exited with code [1-9]|Exception in thread|Exception:' "$launcher_log" |
            tail -n 8 || true
    fi
}

game_failure_lines() {
    current_run_log |
        grep -Ei 'Crash Report|Failed to start|Exception in thread' |
        tail -n 8 || true
}

launch_age() {
    [[ -f "$launch_started_file" ]] || return 1
    local started
    started="$(<"$launch_started_file")"
    [[ "$started" =~ ^[0-9]+$ ]] || return 1
    printf '%s\n' "$(( $(date +%s) - started ))"
}

mkdir -p "$log_dir"

case "$command" in
    start)
        pids="$(game_pids)"
        if [[ -n "$pids" ]]; then
            printf 'Minecraft is already running. Use %s status to check progress.\n' "$0"
            exit 0
        fi

        if ! world_ready && [[ -z "$(launcher_failure_lines)$(game_failure_lines)" ]] &&
            age="$(launch_age)" && (( age >= 0 && age < startup_timeout )); then
            printf 'Minecraft launch is already in progress (%s seconds elapsed). Use %s status to check progress.\n' "$age" "$0"
            exit 0
        fi

        : > "$launcher_log"
        if [[ -f "$latest_log" ]]; then
            stat -c '%s' "$latest_log" > "$log_offset_file"
        else
            printf '0\n' > "$log_offset_file"
        fi
        date +%s > "$launch_started_file"
        flatpak run org.prismlauncher.PrismLauncher \
            --launch 1.21.1_dev_packwiz \
            --world "SkyblockTest" > "$launcher_log" 2>&1 < /dev/null &
        printf 'Prism launch requested. Use %s status to check progress or %s stop to close Minecraft.\n' "$0" "$0"
        ;;
    status)
        if world_ready; then
            printf 'World startup succeeded: player joined the world.\n'
            progress_lines
            exit 0
        fi

        pids="$(game_pids)"
        if [[ -n "$pids" ]]; then
            if [[ "$pids" == *$'\n'* ]]; then
                printf 'Minecraft is running (PIDs: %s).\n' "${pids//$'\n'/, }"
            else
                printf 'Minecraft is running (PID: %s).\n' "$pids"
            fi
            if progress="$(progress_lines)" && [[ -n "$progress" ]]; then
                printf '%s\n' "$progress"
            else
                printf 'No startup milestones are present in the recent log yet.\n'
            fi
        else
            failures="$(launcher_failure_lines)"
            game_failures="$(game_failure_lines)"
            if [[ -n "$failures" || -n "$game_failures" ]]; then
                printf 'Minecraft is not running.\n'
                printf 'Recent crash or startup failure markers:\n'
                [[ -z "$failures" ]] || printf '%s\n' "$failures"
                [[ -z "$game_failures" ]] || printf '%s\n' "$game_failures"
                exit 1
            fi
            if age="$(launch_age)" && (( age >= 0 && age < startup_timeout )); then
                printf 'Minecraft is starting (%s seconds since Prism launch was requested).\n' "$age"
                progress_lines
            elif age="$(launch_age)" && (( age >= startup_timeout )); then
                printf 'Minecraft did not start within %s seconds; no game process or joined player was detected.\n' "$startup_timeout" >&2
                progress_lines >&2
                exit 1
            else
                printf 'Minecraft is not running, and no active launch request or world-joined marker was found.\n'
                exit 1
            fi
        fi
        ;;
    wait)
        timeout="${2:-600}"
        if [[ ! "$timeout" =~ ^[0-9]+$ ]] || (( timeout < 1 )); then
            printf 'Timeout must be a positive number of seconds.\n' >&2
            exit 2
        fi

        deadline=$((SECONDS + timeout))
        while (( SECONDS < deadline )); do
            if world_ready; then
                printf 'World startup succeeded: player joined the world.\n'
                progress_lines
                exit 0
            fi

            failures="$(launcher_failure_lines)"
            game_failures="$(game_failure_lines)"
            if [[ -n "$failures" || -n "$game_failures" ]]; then
                printf 'Minecraft failed before the player joined the world.\n' >&2
                [[ -z "$failures" ]] || printf '%s\n' "$failures" >&2
                [[ -z "$game_failures" ]] || printf '%s\n' "$game_failures" >&2
                exit 1
            fi
            pids="$(game_pids)"
            if [[ -z "$pids" ]] && age="$(launch_age)" && (( age >= startup_timeout )); then
                printf 'Minecraft did not start within %s seconds; no game process or joined player was detected.\n' "$startup_timeout" >&2
                progress_lines >&2
                exit 1
            fi

            sleep 2
        done

        pids="$(game_pids)"
        if [[ -n "$pids" ]]; then
            printf 'Timed out after %s seconds waiting for the player to join the world; Minecraft is still running (PID(s): %s).\n' \
                "$timeout" "${pids//$'\n'/, }" >&2
        else
            printf 'Timed out after %s seconds waiting for the player to join the world.\n' "$timeout" >&2
        fi
        progress_lines >&2
        exit 1
        ;;
    logs)
        progress="$(progress_lines)"
        failures="$(launcher_failure_lines)"
        if [[ -z "$progress" && -z "$failures" ]]; then
            printf 'No startup, shutdown, or error milestones found in the recent game log.\n'
        else
            [[ -z "$progress" ]] || printf '%s\n' "$progress"
            [[ -z "$failures" ]] || printf '%s\n' "$failures"
        fi
        ;;
    stop)
        pids="$(game_pids)"
        if [[ -z "$pids" ]]; then
            printf 'Minecraft is not running.\n'
            exit 0
        fi

        while IFS= read -r pid; do
            kill -TERM "$pid" 2>/dev/null || true
        done <<<"$pids"

        deadline=$((SECONDS + 30))
        while (( SECONDS < deadline )); do
            if [[ -z "$(game_pids)" ]]; then
                printf 'Minecraft stopped cleanly.\n'
                exit 0
            fi
            sleep 1
        done

        printf 'Minecraft did not exit within 30 seconds; it has not been force-killed. Check PID(s): %s\n' "${pids//$'\n'/, }" >&2
        exit 1
        ;;
    -h|--help|help)
        usage
        ;;
    *)
        usage >&2
        exit 2
        ;;
esac
