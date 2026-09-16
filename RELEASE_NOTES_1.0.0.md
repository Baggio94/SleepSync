# SleepSync 1.0.0

First public release of SleepSync.

SleepSync is an Android companion for Syncthing-Fork designed primarily to reduce standby battery drain by stopping Syncthing while the device is asleep, then sending `FOLLOW` as soon as the device wakes so Syncthing can resume normally.

## Why it exists

On some Android handhelds, Syncthing can keep running during sleep and cause substantial overnight battery drain.

In my own testing on a Retroid Pocket Classic, overnight battery drain dropped from about **14% to 2%** after using SleepSync to keep Syncthing stopped while the device was asleep. Actual battery savings will vary by device, configuration, network conditions, and battery health.

## Clamshell handhelds

SleepSync also works with **clamshell handhelds** and other Android devices with a lid.

If closing the lid turns the screen off:

- **Close the lid** → screen OFF → Syncthing `STOP`
- **Open the lid** → screen ON → `FOLLOW` → Syncthing resumes normally

So closing a compatible clamshell handheld has the same SleepSync effect as pressing the power button to turn the display off.

## Features

- Screen OFF → sends `STOP` to Syncthing-Fork
- Screen ON → sends `FOLLOW` after a short delay
- Automatically restarts after device reboot
- Runs in the background even when the SleepSync interface is closed
- Supports clamshell / lid-equipped Android handhelds when lid close turns the screen off
- Follows the Android system light/dark theme
- Shows the last activity directly in the app
- Includes manual `Test STOP` and `Test FOLLOW` buttons
- No account required
- No Internet permission
- No ADB or computer required for normal use

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

SleepSync is an independent, unofficial companion project and is not affiliated with or endorsed by the Syncthing or Syncthing-Fork projects.
