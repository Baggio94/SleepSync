# SleepSync 1.0.0

First public release of SleepSync.

SleepSync is an Android companion for Syncthing-Fork that automatically pauses Syncthing when the screen turns off and sends `FOLLOW` when the screen turns back on.

## Setup

1. In Syncthing-Fork, enable **Settings → Behaviour → Service Control by Broadcast**.
2. Install `SleepSync.apk`.
3. Open SleepSync and tap **Enable SleepSync**.

After that, the SleepSync interface can be closed. If enabled, the service starts again automatically after a reboot.

## Tested

Validated on Retroid Pocket Classic for:

- screen OFF → STOP;
- screen ON → FOLLOW;
- service operation while the UI is closed;
- app update with settings preserved;
- automatic restart after reboot.
