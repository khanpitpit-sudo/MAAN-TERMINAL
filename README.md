# MaAn Terminal

Complete Android terminal emulator with Android Shell + Linux (proot) support.

## Features
- Android Shell (no root)
- Linux Shell via proot + Alpine
- Jetpack Compose UI, Material 3
- Keyboard toolbar: TAB, CTRL, ESC, arrows, CTRL+C/D/L/Z
- Dark green-on-black theme

## Build on Phone (SmartIDE)
1. Install SmartIDE from Play Store
2. Clone: https://github.com/khanpitpit-sudo/MAAN-TERMINAL
3. Switch branch: feature/complete-android-project
4. Run scripts/download_proot.sh
4. Sync Gradle → Build → Assemble Debug APK

## Build on PC (Android Studio)
```bash
git clone https://github.com/khanpitpit-sudo/MAAN-TERMINAL
cd MAAN-TERMINAL
git checkout feature/complete-android-project
bash scripts/download_proot.sh
./gradlew assembleDebug
```

## Requirements
- Android 8.0+ (API 26)
- Storage permission
- Internet for proot/Alpine (~35 MB)

## Package
com.maan.terminal