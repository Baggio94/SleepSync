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

SleepSync keeps working when its interface is no longer on screen.

### Clamshell handhelds

SleepSync also works with **clamshell Android handhelds** such as the **AYN Thor**.

- **Close the lid** → screen OFF → Syncthing `STOP`
- **Open the lid** → screen ON → `FOLLOW`

The same behavior should apply to other Android handhelds where closing the lid turns the screen off.

## Installation

1. Install **Syncthing-Fork**.
2. In Syncthing-Fork, enable **Settings → Behaviour → Service Control by Broadcast**. On older v1 builds, this setting may appear under **Settings → Experimental**.
3. Download and install `SleepSync.apk` from the latest GitHub Release.
4. Open **SleepSync** and tap **Enable SleepSync**.
5. Once the status shows **✓ Active**, tap **Finish setup** or leave normally with Home / swipe-up.

That's it. No computer, ADB, account, or manual service restart is required for normal use.

> **Important:** on some Android builds, dismissing SleepSync from the recent-apps / task-switcher screen can stop its background service. Leave the app normally instead.

## Syncthing-Fork compatibility

SleepSync 1.1.0 can detect and control multiple Syncthing-Fork package variants:

- Current release builds: `com.github.catfriend1.syncthingfork`
- GitHub Actions / debug builds, including `refactorRoot`: `com.github.catfriend1.syncthingfork.debug`
- Legacy builds: `com.github.catfriend1.syncthingandroid`
- Legacy debug builds: `com.github.catfriend1.syncthingandroid.debug`

If only one compatible build is installed, SleepSync selects it automatically. If several are installed side by side, SleepSync lets you choose which one it should control and remembers that choice.

### Compatibility tested for 1.1.0

The complete screen OFF → `STOP` / screen ON → `FOLLOW` flow was verified on an Android 15 emulator with:

- **Syncthing-Fork 2.1.5.0** — current release package
- **Syncthing-Fork 2.1.5.0 refactorRoot/debug build** — GitHub Actions package
- **Syncthing-Fork 1.30.0.5** — legacy package ID

## Tested on

- **AYN Thor** — lid close/open behavior, repeated Syncthing STOP/FOLLOW cycles, reboot startup, and overnight battery testing
- **Retroid Pocket Classic** — screen OFF/ON behavior, background operation, app updates, and automatic restart after reboot

## Useful extras

- Automatically detects supported Syncthing-Fork package variants
- Lets you choose which Syncthing-Fork build to control when several are installed
- Remembers the selected Syncthing-Fork target
- Follows Android's **light / dark theme**
- Shows the **last activity** directly in the app
- Includes manual **Test STOP** and **Test FOLLOW** buttons
- **Finish setup** backgrounds the interface without stopping the service
- If SleepSync is still enabled but the service was stopped, reopening the app starts it again automatically
- Restarts automatically after reboot when enabled
- Uses Android adaptive launcher icons, including themed monochrome icons on supported Android versions

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

**Current version: 1.1.0**
