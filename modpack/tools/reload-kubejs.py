#!/usr/bin/env python3
import argparse
import json
import os
from pathlib import Path
import subprocess
import sys
import time
from urllib.error import HTTPError, URLError
from urllib.request import Request, urlopen


ROOT = Path(__file__).resolve().parents[2]
SOURCE = ROOT / "modpack" / "kubejs"
INSTANCE = Path(
    os.environ.get(
        "PACKWIZ_INSTANCE_DIR",
        "/mnt/LinuxSSD/Games/Minecraft/instances/1.21.1_dev_packwiz/minecraft",
    )
)
RELOAD_TIMEOUT = 120
RELOAD_COMPLETE = "Server resource reload complete!"


def fail(message):
    print(message, file=sys.stderr)
    raise SystemExit(1)


def read_errors(base_url, auth):
    request = Request(
        f"{base_url}/api/console/server/errors",
        headers={"Authorization": f"Bearer {auth}"},
    )
    try:
        with urlopen(request, timeout=10) as response:
            errors = json.loads(response.read())
    except (HTTPError, URLError, TimeoutError, json.JSONDecodeError) as error:
        fail(f"Could not read KubeJS server errors: {error}")
    if not isinstance(errors, list):
        fail("KubeJS returned an unexpected response for server script errors.")
    return errors


def request_reload(base_url, auth):
    request = Request(
        f"{base_url}/api/reload/server",
        data=b"",
        headers={"Authorization": f"Bearer {auth}"},
        method="POST",
    )
    try:
        with urlopen(request, timeout=10) as response:
            if not 200 <= response.status < 300:
                fail(f"KubeJS reload request failed with HTTP {response.status}.")
    except HTTPError as error:
        fail(f"KubeJS reload request failed with HTTP {error.code}.")
    except (URLError, TimeoutError) as error:
        fail(f"Could not request a KubeJS reload: {error}")


def wait_for_reload(log_path, offset):
    deadline = time.monotonic() + RELOAD_TIMEOUT
    while time.monotonic() < deadline:
        try:
            with log_path.open("rb") as log:
                if log.seek(0, 2) < offset:
                    offset = 0
                log.seek(offset)
                if RELOAD_COMPLETE in log.read().decode("utf-8", errors="replace"):
                    return
        except FileNotFoundError:
            pass
        time.sleep(1)
    fail(f"Timed out waiting for KubeJS reload completion in {log_path}.")


def sync_directory(name, target):
    source = SOURCE / name
    if not source.is_dir():
        fail(f"KubeJS source directory not found: {source}")
    target.mkdir(parents=True, exist_ok=True)
    try:
        subprocess.run(
            ["rsync", "-a", "--delete", f"{source}/", f"{target}/"],
            check=True,
        )
    except (FileNotFoundError, subprocess.CalledProcessError) as error:
        fail(f"Could not sync KubeJS {name}: {error}")


def main():
    argparse.ArgumentParser(
        description="Sync and reload server-side KubeJS changes in the test instance."
    ).parse_args()

    config_path = INSTANCE / "kubejs" / "config" / "web_server.json"
    try:
        config = json.loads(config_path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as error:
        fail(f"Could not read KubeJS web server config at {config_path}: {error}")
    if not isinstance(config, dict):
        fail(f"KubeJS web server config must be a JSON object: {config_path}")

    port = config.get("port")
    auth = config.get("auth")
    if config.get("enabled") is not True:
        fail("KubeJS local web server is disabled in web_server.json.")
    if isinstance(port, bool) or not isinstance(port, int) or not 1 <= port <= 65535:
        fail("KubeJS web server config has an invalid port.")
    if not isinstance(auth, str) or not auth:
        fail("KubeJS web server auth is empty; refusing an unauthenticated reload.")

    base_url = f"http://127.0.0.1:{port}"
    read_errors(base_url, auth)

    log_path = INSTANCE / "logs" / "kubejs" / "server.log"
    if not log_path.is_file():
        fail(f"KubeJS server log not found: {log_path}")

    kubejs_dir = INSTANCE / "kubejs"
    sync_directory("server_scripts", kubejs_dir / "server_scripts")
    sync_directory("data", kubejs_dir / "data")

    offset = log_path.stat().st_size
    request_reload(base_url, auth)
    wait_for_reload(log_path, offset)

    errors = read_errors(base_url, auth)
    print(f"KubeJS reload completed with {len(errors)} server script error(s).")
    for error in errors:
        print(json.dumps(error, indent=2, ensure_ascii=False))
    return 1 if errors else 0


if __name__ == "__main__":
    raise SystemExit(main())
