# SleepSync 1.1.0 validation

Validated on Android 15 emulator before release.

## Syncthing-Fork targets

- Current release: `com.github.catfriend1.syncthingfork` — Syncthing-Fork 2.1.5.0
- Debug / root: `com.github.catfriend1.syncthingfork.debug` — refactorRoot build 2.1.5.0
- Legacy: `com.github.catfriend1.syncthingandroid` — Syncthing-Fork 1.30.0.5

For each installed target, the automatic screen-state flow was verified end to end:

- Screen OFF → SleepSync sends `STOP` → `AppConfigReceiver: forceStop by intent`
- Screen ON → SleepSync schedules `FOLLOW`, waits about 1 second, sends it → `AppConfigReceiver: followRunConditions by intent`

Multiple builds were installed side by side and SleepSync correctly detected them and allowed the selected target to be changed.
