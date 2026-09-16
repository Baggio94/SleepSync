# SleepSync

<p align="left">
  <img src="assets/sleepsync-icon.png" width="160" alt="SleepSync icon">
</p>

**SleepSync** is a small Android companion app for **Syncthing-Fork** designed to reduce standby battery drain by stopping Syncthing while your device is asleep and returning it to its normal run-condition behavior as soon as the device wakes.

> SleepSync is an independent, unofficial companion project and is not affiliated with or endorsed by the Syncthing or Syncthing-Fork projects.

No computer, ADB, account, or manual service restart is required for normal use.

## Why SleepSync?

Syncthing can continue running while an Android handheld is asleep, which may cause significant overnight battery drain on some devices.

SleepSync solves this by stopping Syncthing whenever the screen turns off, then immediately returning Syncthing-Fork to its normal run conditions when the screen wakes.

In my own testing on an **AYN Thor**, overnight battery drain dropped from about **14% to 2%** after using SleepSync to keep Syncthing stopped during sleep. Battery savings will vary by device, configuration, network conditions, and battery health, but reducing unnecessary background activity during sleep can make a major difference.

## What it does

- **Screen OFF** → sends `STOP` to Syncthing-Fork
- **Screen ON** → waits 1 second → sends `FOLLOW`
- **Reboot** → automatically starts again if SleepSync was enabled
- Runs even when the SleepSync interface is closed
- Follows Android's **light / dark theme**
- Shows the **last activity** directly in the app
- Includes manual **Test STOP** and **Test FOLLOW** buttons

## Clamshell and lid-equipped handhelds

SleepSync also works naturally with **clamshell handhelds** and other Android devices with a lid — including the **AYN Thor**.

On the AYN Thor, closing the lid turns the display off, so SleepSync treats it exactly like pressing the power button:

- **Close the AYN Thor lid** → screen OFF → Syncthing `STOP`
- **Open the AYN Thor lid** → screen ON → `FOLLOW` → Syncthing resumes normally

The same behavior applies to other Android clamshell handhelds when their lid action turns the screen off/on. This means simply closing the handheld also stops Syncthing and helps avoid unnecessary battery drain while the device is sleeping.

## Requirements

You need **Syncthing-Fork** installed.

In Syncthing-Fork, enable:

**Settings → Behaviour → Service Control by Broadcast**

That setting is required because SleepSync controls Syncthing-Fork through its built-in broadcast interface.

## Installation

1. Download `SleepSync.apk` from the latest GitHub Release.
2. Install the APK on your Android device.
3. Open **SleepSync**.
4. Confirm that Syncthing-Fork is detected.
5. Make sure **Service Control by Broadcast** is enabled in Syncthing-Fork.
6. Tap **Enable SleepSync**.
7. You can now close SleepSync.

That's it.

## How to test it

### Manual test

Open SleepSync and use the buttons under **Advanced test**:

- **Test STOP** should stop Syncthing-Fork.
- **Test FOLLOW** should let Syncthing-Fork resume according to its own run conditions.

### Real-world test

1. Leave Syncthing-Fork connected to another device.
2. Turn the Android screen off, or close the lid on a clamshell device such as the AYN Thor.
3. Syncthing-Fork should stop and disconnect.
4. Turn the screen back on, or reopen the lid.
5. After about one second, SleepSync sends `FOLLOW` and Syncthing-Fork can reconnect.

You can reopen SleepSync at any time to see its **Last activity**.

## Background behavior

SleepSync uses an Android foreground service so screen state monitoring remains reliable after the UI is closed.

On Android 13+, SleepSync does not request notification permission. Android may still list the service in the system **Active apps** / foreground-service manager. Some customized Android versions may show a minimal service notification.

## Privacy

SleepSync:

- has **no Internet permission**;
- does not read or write your files;
- does not access Syncthing credentials, device IDs, folders, or configuration;
- only monitors Android screen ON/OFF events and sends `STOP` / `FOLLOW` broadcasts to Syncthing-Fork.

## Important notes

- Force-stopping SleepSync in Android Settings prevents it from running until you open the app again.
- Manufacturer-specific battery management varies. If a device aggressively kills background services, exempt SleepSync from that device's battery restrictions.
- Disabling SleepSync sends `FOLLOW` once so Syncthing-Fork is not accidentally left stopped.
- On clamshell devices, SleepSync relies on the lid action causing Android to turn the screen off/on.

## Compatibility

- Minimum Android: **Android 6.0 / API 23**
- Target SDK: **35**
- Syncthing-Fork package: `com.github.catfriend1.syncthingfork`
- SleepSync package: `com.med.sleepsync`

SleepSync 1.0.0 has been validated on:

- **Retroid Pocket Classic** — screen OFF/ON behavior, app updates, and automatic restart after reboot
- **AYN Thor** — clamshell lid close/open behavior with Syncthing STOP/FOLLOW, including the overnight battery-drain test described above

## Building from source on macOS

The repository includes a lightweight build script that uses the Android SDK installed on the Mac.

```bash
./build_apk_mac.sh
```

The resulting APK is created as:

```text
SleepSync.apk
```

For development / device testing, see [`DEV_TEST.md`](DEV_TEST.md).

## Version

Current release: **1.0.0**
