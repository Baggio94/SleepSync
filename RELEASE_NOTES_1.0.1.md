# SleepSync 1.0.1

A small reliability and polish update focused on setup, status feedback, and Android integration.

## What's new

- **Status refresh fix** — after tapping **Enable SleepSync**, `Enabled • service starting` now automatically changes to `✓ Active` once the foreground service has started.
- **Finish setup button** — backgrounds the SleepSync interface without stopping the service. Using Home / swipe-up from the bottom has the same effect.
- **Automatic recovery** — if SleepSync is still enabled but its service is no longer running, reopening the app starts the service again automatically instead of requiring Disable / Enable.
- **Android adaptive launcher icon** — proper adaptive icon layers, legacy fallbacks, and a monochrome themed-icon layer for supported Android versions.
- **Clearer background guidance** — on devices where dismissing SleepSync from Recents stops the service, leave the app normally instead of swiping it away from the task switcher.

## Core behavior

The Syncthing control logic is unchanged:

- **Screen OFF** → Syncthing `STOP`
- **Screen ON** → Syncthing `FOLLOW` after 1 second
- **Reboot** → SleepSync starts again automatically if it was enabled

The AYN Thor lid open/close flow and repeated STOP/FOLLOW cycles were stress-tested before this release.

## Installation

Download `SleepSync.apk` from this release and install it normally. If you already have SleepSync 1.0.0 installed, install 1.0.1 over it to keep your existing settings.

In Syncthing-Fork, **Settings → Behaviour → Service Control by Broadcast** must be enabled.
