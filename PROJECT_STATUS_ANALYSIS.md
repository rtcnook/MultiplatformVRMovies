# Project Status Analysis

Date: 2026-05-05

## Current Project Shape

This is a Kotlin Multiplatform / Compose Multiplatform project with these main modules:

- `composeApp`: shared Compose UI for Android, Desktop, iOS, and Web/WasmJS.
- `shared`: shared platform utilities and common Kotlin code.
- `server`: Ktor server module.
- `iosApp`: native iOS host project.

The repository history shows that Web/WasmJS support was disabled once because of dependency incompatibility, then re-enabled by moving image-loading network engines behind platform-specific `expect/actual` implementations.

## Web/WasmJS Verification

The Web/WasmJS development webpack build was verified successfully with:

```powershell
.\gradlew.bat :composeApp:wasmJsBrowserDevelopmentWebpack --stacktrace
```

Result:

- Build completed successfully.
- `composeApp.js` and the Wasm assets were generated.
- The remaining warning from webpack was `Critical dependency: the request of a dependency is an expression`.

This means the current Web target can compile and package. Runtime behavior should still be checked with `:composeApp:wasmJsBrowserDevelopmentRun` in a browser.

## Important Findings

1. Android no longer uses Firebase for movie data. It now uses `ServerMainRepository`, which requests the local Ktor server at `http://10.0.2.2:8080/api/movies` when running in an Android emulator.

2. The `server` module exposes movie data from `composeApp/src/commonMain/composeResources/files/database.json` through `GET /api/movies`. It also exposes the same files directory through `/files/*` for image/static asset access.

3. `composeApp/build.gradle.kts` uses Kotlin `2.3.20` from the version catalog, but applies `kotlin("plugin.serialization") version "2.0.21"` directly. This should be unified to avoid compiler/plugin mismatch risk.

4. `composeApp/src/commonMain/kotlin/org/example/project/ImageLoader.kt` declares `expect fun getAsyncImageLoader(...)`, but actual implementations currently exist for Android, JVM, and WasmJS only. iOS is missing an actual implementation, so iOS builds are likely broken after the Web image-loader change.

5. `composeApp` and `shared` manually create `webMain` and connect it to `wasmJsMain`. Gradle warns that this prevents the default Kotlin hierarchy template from being applied correctly.

6. `PROJECT_MIGRATION_INFO.md` and some source comments contain mojibake text. This does not necessarily block builds, but the project documentation and comments need an encoding cleanup pass.

7. `server/bin/` appears to be generated output. It is now ignored through `**/bin/` in `.gitignore`.

## Android Server Data Source

To run Android with the local server:

```powershell
.\gradlew.bat :server:run
```

Then install/run the Android app. In the Android emulator, `10.0.2.2` maps back to the host machine, so the app can reach the server at `http://10.0.2.2:8080`.

If running on a physical Android device, replace the Android server base URL with the host computer's LAN IP address.

Verified after the switch:

```powershell
.\gradlew.bat :server:test :composeApp:compileDebugKotlinAndroid --stacktrace
```

Result: build and tests completed successfully.

## Recommended Next Steps

1. Add the missing iOS `actual` for `getAsyncImageLoader`.
2. Move the serialization plugin version into `gradle/libs.versions.toml` or align it with the Kotlin plugin version.
3. Decide whether to keep the custom `webMain` source set or disable the default hierarchy warning explicitly.
4. Run `:composeApp:wasmJsBrowserDevelopmentRun` and verify the app in a browser.
5. Clean up mojibake in documentation and comments.
