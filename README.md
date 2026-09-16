# SleepSync

<p align="left">
  <img src="assets/sleepsync-icon.png" width="160" alt="SleepSync icon">
</p>

**SleepSync helps reduce standby battery drain from Syncthing-Fork on Android handhelds.**

When your device goes to sleep, SleepSync stops Syncthing. As soon as the device wakes, it sends `FOLLOW` so Syncthing can resume according to its normal run conditions.

On my **AYN Thor**, overnight battery drain dropped from about **14% to 2%** after using SleepSync to keep Syncthing stopped during sleep. Battery savings will vary by device and setup.

## How it works

| Device state | SleepSync action |
| --- | --- |
| **Screen OFF** | Sends `STOP` to Syncthing-Fork |
| **Screen ON** | Waits 1 second, then sends `FOLLOW` |
| **Device reboot** | Starts again automatically if SleepSync was enabled |

SleepSync keeps working even when its interface is closed.

### Clamshell handhelds

SleepSync also works with **clamshell Android handhelds** such as the **AYN Thor**.

- **Close the lid** → screen OFF → Syncthing `STOP`
- **Open the lid** → screen ON → `FOLLOW`

The same behavior should apply to other Android handhelds where closing the lid turns the screen off.

## Installation

1. Install **Syncthing-Fork**.
2. In Syncthing-Fork, enable **Settings → Behaviour → Service Control by Broadcast**.
3. Download and install `SleepSync.apk` from the latest GitHub Release.
4. Open **SleepSync** and tap **Enable SleepSync**.

That's it. You can close the SleepSync interface after enabling it.

No computer, ADB, account, or manual service restart is required for normal use.

## Tested on

- **AYN Thor** — lid close/open behavior, Syncthing STOP/FOLLOW, and the overnight battery test above
- **Retroid Pocket Classic** — screen OFF/ON behavior, background operation, app updates, and automatic restart after reboot

## Useful extras

- Follows Android's **light / dark theme**
- Shows the **last activity** directly in the app
- Includes manual **Test STOP** and **Test FOLLOW** buttons
- Restarts automatically after reboot when enabled

## Privacy

SleepSync:

- has **no Internet permission**;
- does not read or write your files;
- does not access Syncthing credentials, device IDs, folders, or configuration;
- only monitors Android screen ON/OFF events and sends `STOP` / `FOLLOW` broadcasts to Syncthing-Fork.

## Background behavior

SleepSync uses an Android foreground service so screen-state monitoring remains reliable after the interface is closed.

On Android 13+, SleepSync does not request notification permission. Android may still show it under **Active apps** / the foreground-service manager, and some Android builds may display a minimal service notification.

If you **Force stop** SleepSync in Android Settings, open the app again to reactivate it. On devices with aggressive battery management, you may also need to exempt SleepSync from battery restrictions.

## Compatibility

- Minimum Android: **Android 6.0 / API 23**
- Target SDK: **35**
- Syncthing-Fork package: `com.github.catfriend1.syncthingfork`
- SleepSync package: `com.med.sleepsync`

## Building from source

On macOS with the Android SDK installed:

```bash
./build_apk_mac.sh
```

The resulting APK is created as `SleepSync.apk`.

For development and device testing, see [`DEV_TEST.md`](DEV_TEST.md).

---

SleepSync is an independent, unofficial companion project and is not affiliated with or endorsed by the Syncthing or Syncthing-Fork projects.

**Current version: 1.0.1**
