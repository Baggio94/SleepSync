# Changelog

## 1.1.0

- Add support for multiple Syncthing-Fork package variants instead of assuming a single package ID.
- Detect the current release package: `com.github.catfriend1.syncthingfork`.
- Detect GitHub Actions / debug builds such as the `refactorRoot` build: `com.github.catfriend1.syncthingfork.debug`.
- Add compatibility with older Syncthing-Fork package IDs: `com.github.catfriend1.syncthingandroid` and `com.github.catfriend1.syncthingandroid.debug`.
- Automatically use the only compatible Syncthing-Fork build when one is installed.
- When several compatible builds are installed side by side, allow the user to choose which one SleepSync controls.
- Persist the selected Syncthing-Fork target across app restarts and device reboots.
- `Open Syncthing-Fork`, `STOP`, and `FOLLOW` now use the selected/detected package dynamically.
- When switching targets while SleepSync is enabled, release the previous target with `FOLLOW` before controlling the new target.
- Preserve the existing screen OFF → `STOP`, screen ON → `FOLLOW`, background service, reboot restart, theme, and 1.0.x settings behavior.

## 1.0.1

- Fix the SleepSync status UI after enabling the service.
- The app now refreshes the status shortly after foreground-service startup so `Enabled • service starting` automatically becomes `✓ Active` without leaving and reopening the app.
- If SleepSync is still enabled but the service is no longer running, reopening the app starts the service again automatically instead of requiring Disable / Enable.
- Add a **Finish setup** button that backgrounds the SleepSync interface without stopping the foreground service. Using Home / swipe-up from the bottom has the same effect.
- Clarify that dismissing SleepSync from the recent-apps screen may stop the background service on some Android builds and should be avoided on affected devices.
- Add proper Android adaptive launcher icons, legacy density fallbacks, and a monochrome themed-icon layer for supported Android versions.
- No changes to the Syncthing STOP / FOLLOW logic.

## 1.0.0

Initial public release.

- Pause Syncthing-Fork when the Android screen turns off.
- Send `FOLLOW` when the screen turns back on.
- Automatic restart after device reboot when enabled.
- Enable / disable directly from the Android app; no ADB required for normal use.
- Syncthing-Fork detection and quick-launch button.
- Manual STOP / FOLLOW test controls.
- Last-activity status shown in the app.
- Automatic Android light / dark theme support.
- Foreground-service implementation for reliable background monitoring.
- New SleepSync application icon.
