# SleepSync 1.0.1

Small UI polish release.

## Fixed

- After tapping **Enable SleepSync**, the status now refreshes automatically once the foreground service has started.
- `Enabled • service starting` now changes to `✓ Active` without having to leave and reopen the app.

## Background behavior

No changes were made to the core SleepSync behavior:

- **Screen OFF** → Syncthing `STOP`
- **Screen ON** → Syncthing `FOLLOW` after 1 second
- **Reboot** → SleepSync starts again automatically if it was enabled

The AYN Thor lid open/close flow and repeated STOP/FOLLOW cycles were stress-tested before this release.
