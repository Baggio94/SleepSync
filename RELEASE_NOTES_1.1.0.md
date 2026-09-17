# SleepSync 1.1.0

SleepSync 1.1.0 expands Syncthing-Fork compatibility and adds support for multiple installed Syncthing-Fork variants.

## What's new

- **Current Syncthing-Fork builds** — supports `com.github.catfriend1.syncthingfork`.
- **Root / debug builds** — supports `com.github.catfriend1.syncthingfork.debug`, including the `refactorRoot` GitHub Actions build.
- **Legacy Syncthing-Fork builds** — supports `com.github.catfriend1.syncthingandroid` and its `.debug` variant.
- **Multiple builds installed at once** — when more than one compatible Syncthing-Fork build is installed, SleepSync lets you choose which one to control.
- **Target persistence** — the selected Syncthing-Fork target is remembered across app restarts and device reboots.
- **Dynamic control** — Open Syncthing-Fork, Test STOP, Test FOLLOW, and the automatic screen OFF/ON behavior all use the selected package.
- **Safe target switching** — when SleepSync is enabled and you switch targets, the previous target is released with `FOLLOW` before SleepSync starts controlling the new one.

## Verified compatibility

The full screen-state flow was tested on an Android 15 emulator with all three builds installed side by side:

- **Syncthing-Fork 2.1.5.0** — current release package
- **Syncthing-Fork 2.1.5.0 refactorRoot/debug** — GitHub Actions root/debug package
- **Syncthing-Fork 1.30.0.5** — legacy package ID

For each target, the complete path was verified:

- **Screen OFF** → SleepSync sends `STOP` → Syncthing-Fork receives `forceStop by intent`
- **Screen ON** → SleepSync waits 1 second and sends `FOLLOW` → Syncthing-Fork receives `followRunConditions by intent`

The existing AYN Thor and Retroid Pocket Classic behavior remains unchanged.

## Installation

Download `SleepSync.apk` from this release and install it normally. Existing SleepSync users can install 1.1.0 over their current version to keep their settings.

In current Syncthing-Fork builds, enable **Settings → Behaviour → Service Control by Broadcast**. On older v1 builds, this option may be under **Settings → Experimental**.

## Core behavior

- **Screen OFF** → Syncthing `STOP`
- **Screen ON** → Syncthing `FOLLOW` after 1 second
- **Reboot** → SleepSync starts again automatically if it was enabled

SleepSync remains independent and unofficial and is not affiliated with or endorsed by Syncthing or Syncthing-Fork.
