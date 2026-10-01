#!/bin/bash
# Local APK Build Script for MAAN-TERMINAL
echo "=== MAAN-TERMINAL APK Builder ==="
echo "1. Downloading proot..."
bash scripts/download_proot.sh

echo "2. Checking Android SDK..."
if [ -z "$ANDROID_HOME" ]; then
  echo "ANDROID_HOME not set. Please install Android Studio or set SDK path"
  echo "In Android Studio: Open project -> Build -> Build APK"
  exit 1
fi

echo "3. Building APK..."
./gradlew assembleDebug

echo "APK built at: app/build/outputs/apk/debug/app-debug.apk"
ls -lh app/build/outputs/apk/debug/
