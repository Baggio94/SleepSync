#!/usr/bin/env bash
set -euo pipefail
PKG="com.med.sleepsync"

echo "Connected Android devices:"
adb devices -l

echo
echo "SleepSync package/version:"
adb shell dumpsys package "$PKG" | grep -E 'versionName=|versionCode=' | head -n 4 || true

echo
echo "SleepSync service state:"
adb shell dumpsys activity services "$PKG" | grep -E 'SleepSyncService|isForeground|foregroundId|allowStartForeground|app=' | head -n 40 || true

echo
echo "SleepSync preferences / persistent diagnostic:"
adb shell run-as "$PKG" cat shared_prefs/sleep_sync.xml 2>/dev/null || true

echo
echo "Recent SleepSync / Syncthing-Fork logs (if firmware exposes them):"
adb logcat -d | grep -E 'SleepSync|AppConfigReceiver|SyncthingService|shouldRun decision|onServiceStateChange|Finished mSyncthingRunnableThread' | tail -n 100 || true
