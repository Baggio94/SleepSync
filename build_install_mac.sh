#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
"$ROOT/build_apk_mac.sh"

if ! command -v adb >/dev/null 2>&1; then
  echo "adb not found. Install Android platform-tools first."
  exit 1
fi

if ! adb get-state >/dev/null 2>&1; then
  echo "No authorized Android device detected."
  echo "Connect the device, enable USB debugging, accept the authorization prompt, then run this script again."
  exit 1
fi

adb install -r "$ROOT/SleepSync.apk"
adb shell monkey -p com.med.sleepsync -c android.intent.category.LAUNCHER 1 >/dev/null

echo
echo "SleepSync 1.0.1 installed and opened."
echo "Enable Syncthing-Fork → Settings → Behaviour → Service Control by Broadcast, then tap Enable SleepSync."
