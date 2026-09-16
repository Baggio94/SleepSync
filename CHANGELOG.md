# Changelog

## 1.0.1

- Fix the SleepSync status UI after enabling the service.
- The app now refreshes the status shortly after foreground-service startup so `Enabled • service starting` automatically becomes `✓ Active` without leaving and reopening the app.
- If SleepSync is still enabled but the service is no longer running, reopening the app starts the service again automatically instead of requiring Disable / Enable.
- Clarify that leaving the app normally with Home / swipe-up is fine, but swiping SleepSync away from Recents may stop the background service on some Android builds.
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
