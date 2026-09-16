# SleepSync 1.0.0 device validation

## Normal user test

1. Syncthing-Fork → Settings → Behaviour → Service Control by Broadcast → ON.
2. Install SleepSync.apk.
3. Open SleepSync and tap Enable SleepSync.
4. Confirm status becomes Active.
5. Test STOP and FOLLOW.
6. Screen OFF for 5 seconds → Syncthing should disconnect.
7. Screen ON → Syncthing should reconnect according to its normal run conditions.
8. Reboot device without manually opening SleepSync → behavior should continue.

## Clamshell validation

1. Leave SleepSync enabled.
2. Close lid and wait 5 seconds.
3. Confirm Syncthing disconnects from a peer/hub.
4. Open lid and wait a few seconds.
5. Confirm Syncthing reconnects.
6. Reboot once and repeat without opening SleepSync.

## Optional ADB diagnostics

Run:

```bash
./test_device_mac.sh
```

The persistent `shared_prefs/sleep_sync.xml` diagnostic is useful on devices where firmware hides INFO-level app Logcat output.
