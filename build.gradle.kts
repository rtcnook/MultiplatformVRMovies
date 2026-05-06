plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.ktor) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}

tasks.register("packageDesktopExe") {
    group = "distribution"
    description = "Builds the desktop distributable with a native executable launcher."
    dependsOn(":composeApp:createDistributable")
}

tasks.register("packageDesktopInstaller") {
    group = "distribution"
    description = "Builds the desktop installer for the current operating system."
    dependsOn(":composeApp:packageDistributionForCurrentOS")
}

tasks.register("packageDesktopCurrent") {
    group = "distribution"
    description = "Builds desktop executable files and the installer for the current operating system."
    dependsOn("packageDesktopExe", "packageDesktopInstaller")
}

tasks.register("packageAndroid") {
    group = "distribution"
    description = "Builds Android debug/release APKs and the release app bundle."
    dependsOn(
        ":composeApp:assembleDebug",
        ":composeApp:assembleRelease",
        ":composeApp:bundleRelease"
    )
}

tasks.register("packageWeb") {
    group = "distribution"
    description = "Builds the WasmJS browser distribution."
    dependsOn(":composeApp:wasmJsBrowserDistribution")
}

tasks.register("packageIosFrameworks") {
    group = "distribution"
    description = "Builds iOS release frameworks. Creating an IPA still requires Xcode on macOS."
    dependsOn(
        ":composeApp:linkReleaseFrameworkIosArm64",
        ":composeApp:linkReleaseFrameworkIosSimulatorArm64"
    )
}

tasks.register("packageAllLocal") {
    group = "distribution"
    description = "Builds all packages that the current machine can produce locally."
    dependsOn(
        "packageAndroid",
        "packageWeb",
        "packageDesktopCurrent"
    )
}
