#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
SDK_ROOT="${ANDROID_SDK_ROOT:-$HOME/Library/Android/sdk}"
PLATFORM="android-35"
BUILD_TOOLS_VERSION="35.0.0"

need_cmd() { command -v "$1" >/dev/null 2>&1; }

if ! need_cmd brew; then
  echo "Homebrew is required: https://brew.sh"
  exit 1
fi

if ! need_cmd java || ! need_cmd javac || ! need_cmd keytool; then
  brew install openjdk@21
  export PATH="$(brew --prefix openjdk@21)/bin:$PATH"
fi

if ! need_cmd sdkmanager; then
  brew install --cask android-commandlinetools
fi

mkdir -p "$SDK_ROOT"
export ANDROID_SDK_ROOT="$SDK_ROOT"
SDKMANAGER="$(command -v sdkmanager)"

yes | "$SDKMANAGER" --sdk_root="$SDK_ROOT" --licenses >/dev/null 2>&1 || true
"$SDKMANAGER" --sdk_root="$SDK_ROOT" "platforms;$PLATFORM" "build-tools;$BUILD_TOOLS_VERSION"

BT="$SDK_ROOT/build-tools/$BUILD_TOOLS_VERSION"
ANDROID_JAR="$SDK_ROOT/platforms/$PLATFORM/android.jar"
AAPT2="$BT/aapt2"
D8="$BT/d8"
ZIPALIGN="$BT/zipalign"
APKSIGNER="$BT/apksigner"

rm -rf "$ROOT/build"
mkdir -p "$ROOT/build/classes" "$ROOT/build/dex" "$ROOT/build/resources"
find "$ROOT/src" -name '*.java' -print > "$ROOT/build/sources.txt"

javac --release 8 -classpath "$ANDROID_JAR" -d "$ROOT/build/classes" @"$ROOT/build/sources.txt"

CLASS_FILES=()
while IFS= read -r -d '' file; do CLASS_FILES+=("$file"); done < <(find "$ROOT/build/classes" -name '*.class' -print0)

"$D8" --lib "$ANDROID_JAR" --min-api 23 --output "$ROOT/build/dex" "${CLASS_FILES[@]}"

RESOURCE_ARGS=()
if [ -d "$ROOT/res" ]; then
  "$AAPT2" compile --dir "$ROOT/res" -o "$ROOT/build/resources/resources.zip"
  RESOURCE_ARGS+=("$ROOT/build/resources/resources.zip")
fi

"$AAPT2" link \
  -o "$ROOT/build/base-unsigned.apk" \
  -I "$ANDROID_JAR" \
  --manifest "$ROOT/AndroidManifest.xml" \
  --min-sdk-version 23 \
  --target-sdk-version 35 \
  "${RESOURCE_ARGS[@]}"

cp "$ROOT/build/base-unsigned.apk" "$ROOT/build/with-dex.apk"
(
  cd "$ROOT/build/dex"
  zip -q -j "$ROOT/build/with-dex.apk" classes.dex
)

"$ZIPALIGN" -f 4 "$ROOT/build/with-dex.apk" "$ROOT/build/aligned.apk"

KEYSTORE="$ROOT/sleepsync.keystore"
if [ ! -f "$KEYSTORE" ]; then
  keytool -genkeypair -v \
    -keystore "$KEYSTORE" \
    -storepass android \
    -keypass android \
    -alias sleepsync \
    -keyalg RSA -keysize 2048 -validity 10000 \
    -dname "CN=SleepSync,O=SleepSync,C=FR" >/dev/null
fi

OUT="$ROOT/SleepSync.apk"
"$APKSIGNER" sign \
  --ks "$KEYSTORE" \
  --ks-key-alias sleepsync \
  --ks-pass pass:android \
  --key-pass pass:android \
  --out "$OUT" \
  "$ROOT/build/aligned.apk"

"$APKSIGNER" verify --verbose "$OUT"
echo "APK built: $OUT"
