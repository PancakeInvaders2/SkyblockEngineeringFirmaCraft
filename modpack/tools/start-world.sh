#!/bin/bash

#Usage: prismlauncher [-h] [-v] [-d dir] [-l launch] [-s server] [-a profile] [-I import] [--show show] [--alive] [--help-all]

#Options:
#  -d, --dir directory    Use a custom path as application root (use '.' for current directory)
#  -l, --launch instance  Launch the specified instance (by instance ID)
#  -s, --server address   Join the specified server on launch (only valid in combination with --launch)
#  -a, --profile profile  Use the account specified by its profile name (only valid in combination with --launch)
#  --alive                Write a small 'live.check' file after the launcher starts
#  -I, --import file      Import instance from specified zip (local path or URL)
#  --show show            Opens the window for the specified instance (by instance ID)
#  -h, --help             Displays help on commandline options.
#  --help-all             Displays help, including generic Qt options.
#  -v, --version          Displays version information.

mkdir -p /tmp/skyblocktfc/logs
rm -f /tmp/skyblocktfc/logs/start-world.log

flatpak run org.prismlauncher.PrismLauncher \
    --launch 1.21.1_dev_packwiz \
    --world "SkyblockTest" > /tmp/skyblocktfc/logs/start-world.log 2>&1

# the previous command finishes in a few seconds, but then the minecraft loading starts, then the server starts, etc, and if there's no crash, we don't have an easy way to check that everything is going ok. We don't want to read the whole log, but we need a way to check progress