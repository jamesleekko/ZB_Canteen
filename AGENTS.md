# AGENTS.md

## Cursor Cloud specific instructions

### Project overview

ZB_Canteen (智能化食堂) is a native Android app (Kotlin + Jetpack Compose) for smart canteen IoT device management. It is a client-only app with no backend code in this repo — it talks to three remote APIs (`www.hnxtzb.com`, `open.ys7.com`, `api.dayufeng.cn`).

### Environment

- **JDK**: System JDK 21 (pre-installed). AGP 8.13.1 requires JDK 17+.
- **Android SDK**: Installed at `$HOME/android-sdk`. The `ANDROID_HOME` env var and `PATH` additions are in `~/.bashrc`.
- **`local.properties`**: Must exist at project root with `sdk.dir=$HOME/android-sdk`. It is gitignored. The update script recreates it each session.

### Build / Test / Lint commands

| Task | Command |
|------|---------|
| Debug build | `./gradlew assembleDebug` |
| Unit tests | `./gradlew testDebugUnitTest` |
| Lint | `./gradlew lintDebug` (exits non-zero due to pre-existing lint warnings) |
| Clean | `./gradlew clean` |

### Known caveats

- **No KVM in Cloud Agent VMs**: The Android emulator requires KVM hardware acceleration, which is unavailable in the Firecracker-based Cloud Agent environment. You cannot run the emulator or connected (instrumented) tests here. The app can only be validated via `assembleDebug`, unit tests, and lint.
- **Lint baseline**: `lintDebug` fails with pre-existing warnings (deprecated APIs, unused parameters). This is not caused by agent changes.
- **NDK ABI filter**: The app ships native `.so` libraries for `armeabi-v7a` and `arm64-v8a` only (EZVIZ SDK). No x86/x86_64 native libs are bundled.
- **First build is slow**: Gradle downloads ~500MB+ of dependencies on first run. Subsequent builds use cache.
