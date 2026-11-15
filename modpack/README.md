# Modpack development

This directory is the Packwiz source for the development instance. External mods
are pinned in `mods/*.pw.toml`; configs and KubeJS files are tracked alongside
them. The two mods in this repository are built locally and are not committed
as JARs.

Install the Packwiz CLI, then run `./modpack/dev.sh` from the repository root.
It builds `skyblock-tfc` and `tfctriphammerpp`, stages their JARs, refreshes the
Packwiz index, and starts the local server. Keep it running while launching the
Prism instance.

CurseForge excludes the installed Immersive Crate View and TFC Nitrary files
from its API. Their JARs are therefore local-only and ignored by Git. To seed
them from another instance the first time, set `PACKWIZ_SOURCE_MODS` to that
instance's `minecraft/mods` directory when running `dev.sh`.

For the Prism instance, place `packwiz-installer-bootstrap.jar` in its
`minecraft` directory and set this as its pre-launch command. Prism runs
custom commands directly, so invoke a shell explicitly for `cd` and `&&`.
Use double quotes to pass the shell script as one argument to `-c`:

```sh
PreLaunchCommand=/bin/sh -c \"cd $INST_MC_DIR && exec $INST_JAVA -jar packwiz-installer-bootstrap.jar http://localhost:8080/pack.toml\"
```

Packwiz installs the pinned mods and pack files before each launch. Changes to
configs and scripts are synced on the next launch; mods still need a game
restart. The launcher's existing Java and debugger settings are independent of
this setup.
