#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

export JAVA_HOME="${JAVA_HOME:-/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home}"
export ANDROID_HOME="${ANDROID_HOME:-/opt/homebrew/share/android-commandlinetools}"
export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:${PATH}"

if ! adb devices 2>/dev/null | grep -qE $'\t(device|emulator)'; then
  echo "No Android device or emulator connected."
  echo "Start the emulator (example):"
  echo "  \"$ANDROID_HOME/emulator/emulator\" -avd PawTracker_Pixel &"
  echo "Then wait for boot and run this script again."
  exit 1
fi

./gradlew :androidApp:installDebug
adb shell am start -n com.pawtracker.app/com.pawtracker.android.MainActivity
