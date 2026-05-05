import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    kotlin("plugin.serialization") version "2.0.21"
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    jvm()
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

/*
    js {
        browser()
        binaries.executable()
    }
*/
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
            implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.4")
            implementation("io.ktor:ktor-client-cio:3.0.3")
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(projects.shared)
            
            // Coil 3 基础库 (无网络扩展)
            implementation("io.coil-kt.coil3:coil-compose:3.0.4")
            
            // Ktor 核心 (无引擎)
            implementation("io.ktor:ktor-client-core:3.0.3")
            
            // Kotlin Serialization
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
            
            // 协程跨平台
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            // 显式指定 JVM 版 GitLive Firebase，防止误用 Android 版
            implementation("dev.gitlive:firebase-app-jvm:1.13.0")
            implementation("dev.gitlive:firebase-database-jvm:1.13.0")
            // Ktor OkHttp 引擎用于 Desktop 图片加载
            implementation("io.ktor:ktor-client-okhttp:3.0.3")
            // Ktor CIO for JVM
            implementation("io.ktor:ktor-client-cio:3.0.3")
            // Coil Network extension for JVM
            implementation("io.coil-kt.coil3:coil-network-ktor3:3.0.4")
        }

        wasmJsMain.dependencies {
            implementation(libs.compose.ui)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.components.resources)
            // Ktor JS/Wasm engine
            implementation("io.ktor:ktor-client-js:3.0.3")
            // Coil Network support for WasmJS
            implementation("io.coil-kt.coil3:coil-network-ktor3:3.0.4")
        }
        
        val webMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                implementation(libs.compose.ui)
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.components.resources)
            }
        }
        
        wasmJsMain.get().dependsOn(webMain)
        // jsMain.get().dependsOn(webMain)
    }
}

android {
    namespace = "org.example.project"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.example.project"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "org.example.project.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.example.project"
            packageVersion = "1.0.0"
        }
    }
}
