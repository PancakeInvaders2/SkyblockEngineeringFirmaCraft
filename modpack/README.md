# Modpack development

This directory is the Packwiz source for the development instance. External mods
are pinned in `mods/*.pw.toml`; configs and KubeJS files are tracked alongside
them. The two mods in this repository are built locally and are not committed
as JARs.

Install the Packwiz CLI, then run `./modpack/tools/syncPackwiz.sh` from the repository root.
It builds `skyblock-tfc` and `tfctriphammerpp`, stages their JARs, refreshes the
Packwiz index, and installs the updated pack directly into the local Prism
instance. No Packwiz HTTP server is needed.

CurseForge excludes the installed Immersive Crate View and TFC Nitrary files
from its API. Their JARs are therefore local-only and ignored by Git. To seed
them from another directory the first time, set `PACKWIZ_SOURCE_MODS` to that
directory. It defaults to `/mnt/LinuxSSD/Games/Minecraft/dev/modCache`.
`PACKWIZ_INSTANCE_DIR` can override the default Prism instance's `minecraft`
directory for both `tools/syncPackwiz.sh` and `tools/start-world.sh`. The instance must have
`packwiz-installer-bootstrap.jar` in that directory.

Run `tools/syncPackwiz.sh` to sync changes before launching.
Start the test world with `./modpack/tools/start-world.sh`. Use the same script
with `status` to see concise loading milestones, an in-progress state for up to
10 minutes, and a successful result once a player has joined. The `wait`
command accepts an optional timeout in seconds (default 10 minutes) and exits
nonzero on startup failure or timeout. Use `logs` to show recent relevant log
lines, or `stop` to request a graceful game shutdown (waits up to 30 seconds;
it does not force-kill Minecraft).

For server-side KubeJS changes, run `./modpack/tools/reload-kubejs.py` while the
test world is open. It mirrors `kubejs/server_scripts` and `kubejs/data` into
the instance, invokes KubeJS's authenticated local `/reload` endpoint, waits
for resource reload completion, and reports server script errors. It does not
reload startup scripts, client scripts, or assets; those still need their
respective restart or client resource-reload workflow.
