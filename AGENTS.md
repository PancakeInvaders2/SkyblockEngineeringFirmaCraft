
# Project Instructions

## Project identity

This repository contains TerraFirmaCraft Skyblock, a Minecraft modpack, and associated mods developped for it.

The project aims to create a technically and scientifically plausible Skyblock progression experience built around TerraFirmaCraft. Progression should emerge from the interaction of geology, chemistry, biology, metallurgy,
agriculture and technology.

## File Structure

- modpack: All the code related to the modpack
- mods: folder containing a folder for each mod that had to be made for the modpack
- design: tools for designing the modpack and its progression

## Core technology

The current target environment is:

- Minecraft 1.21.1
- NeoForge 21.1.251
- TerraFirmaCraft 4.2.10
- Java 21 for Minecraft mod development
- KubeJS for recipe and gameplay scripting
- Packwiz for modpack management

Always inspect the actual repository configuration before assuming a version. This document may need updating when the project is upgraded.

## Relevant paths to grep

- DEV: /mnt/LinuxSSD/Games/Minecraft/dev/
- The test instance managed through packwiz: /mnt/LinuxSSD/Games/Minecraft/instances/1.21.1_dev_packwiz/minecraft
- Searchable kubejs syntax from probejs: /mnt/LinuxSSD/Games/Minecraft/instances/1.21.1_dev_packwiz/minecraft/.probe
- Searchable recipes, tags, and loot tables: /mnt/LinuxSSD/Games/Minecraft/instances/1.21.1_dev_packwiz/minecraft/kubejs/debug
- the logs at /mnt/LinuxSSD/Games/Minecraft/instances/1.21.1_dev_packwiz/minecraft/logs/, especially latest.log and kubejs/server.log

## Tools

- packwiz is available at ~/bin/packwiz
- for modifying TFC icons, imagemagick is available at ~/bin/magick 
- grep, sed, bash scripts, python3, etc
- ~/bin/prism, a wrapper script to call prismlauncher via CLI
- the content of the modpack/tools folder: reusable development scripts, coded in bash or python3
- ./modpack/tools/syncPackwiz.sh builds local mods and syncs the Packwiz pack directly to the test instance; it does not need a local HTTP server
- ./modpack/tools/start-world.sh [start|status|logs|stop] manages the test world and reports concise startup progress
- ./modpack/tools/reload-kubejs.py syncs `kubejs/server_scripts` and `kubejs/data` to the test instance, invokes KubeJS's authenticated local `/reload` endpoint, waits for completion, and reports server script errors. Use it while the test world is running for server-script or data changes; it does not reload startup scripts, client scripts, or assets.

## Design principles

Game design decisions belong to the project owner.

The agent may research, propose and compare solutions, but must not
make unilateral decisions about progression, resource availability,
recipe balance or gameplay mechanics.

When implementation requires an unresolved design decision, stop
and present the alternatives before proceeding.

When a design decision is ambiguous, explain the alternatives and ask before making a potentially consequential change.

## Implementation preferences

- Prefer KubeJS for recipes, loot tables and gameplay integration when the required functionality is supported, respecting the kubejs syntax defined by probejs
- Avoid introducing a new Java mod dependency for something that can be implemented cleanly in KubeJS.
- Reuse existing mod mechanics and machines rather than introducing arbitrary custom mechanics.
- Prefer clean, readable implementations over clever abstractions.
- For mod development, use Java records where appropriate.

- Discuss any departure from TFC mechanics
- Discuss any new mods that should be added or implemented
- Keep changes focused on the requested task.
- Preserve existing comments and documentation when possible.
- Run relevant tests or build tasks when available.
- Clearly report anything that could not be tested.


## Working practices

Do not guess at mod APIs, recipe schemas, item identifiers or tag contents. Inspect the installed mod versions and existing examples. Before implementing a feature:

Validate JSON syntax and recipe schemas from existing examples. Inspect the relevant existing recipes, tags, scripts and mod implementations. 

Identify which mod owns the relevant mechanic, and check it's code if needed. Relevant projects are cloned at DEV. Example: TerraFirmaCraft is cloned at DEV/TerraFirmaCraft. We don't modify these repos, but it can be inspected to gain context. If a repo that isn't yet cloned under DEV would be useful, request that it be cloned 

Create a git worktree if you have changes to make

When testing in Minecraft, run ./modpack/tools/syncPackwiz.sh to build and sync the pack, then use ./modpack/tools/start-world.sh to launch the test world. Use ./modpack/tools/start-world.sh wait to wait for the player to join and get a pass/fail exit status; status is only a snapshot and can still report startup in progress. Check progress with status or recent milestones with logs; use stop to request a graceful shutdown. Avoid reading the whole log; use focused filters and limited output.

For server-side KubeJS script or data changes in an already running test world, use `./modpack/tools/reload-kubejs.py` instead of restarting the game. Startup scripts, client scripts, and assets still require their respective restart or client resource-reload workflow.

## Research and scientific plausibility

When implementing scientific processes, distinguish between:
- Established scientific principles.
- Simplified representations suitable for gameplay.
- Speculative or hypothetical mechanisms.

Do not present speculative chemistry or geology as established fact.

When scientific accuracy materially affects a design decision,
research the underlying process before proposing an implementation.

The goal is scientifically informed gameplay, not a laboratory
simulation.

## Communication

Before making substantial changes:
- Explain the proposed approach.
- Identify relevant existing implementations.
- Identify any unresolved design decisions.
- Describe the expected files and systems affected.

Do not silently expand the scope of a task.

After implementation, report:
- Files changed.
- Implementation details.
- Tests and validation performed.
- Any remaining issues or assumptions.
- Any design decisions that still require approval.