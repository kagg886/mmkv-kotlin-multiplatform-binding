import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.application)
}

private fun props(key: String): String = System.getenv(key) ?: project.findProperty(key) as? String ?: throw NullPointerException("Property $key not found")

//# SYSTEM
//LIB_CORE_VERSION=v1.1.0
//LIB_CORE_JAVA_VERSION=v1.1.0
//LIB_PLATFORM_JVM_WINDOWS_VERSION=v1.1.0
//LIB_PLATFORM_JVM_LINUX_VERSION=v1.1.0
//LIB_PLATFORM_JVM_MACOS_VERSION=v1.1.0
//LIB_PLATFORM_ANDROID_VERSION=v1.1.0
//LIB_PLATFORM_IOS_VERSION=v1.1.0

val coreVersion = props("LIB_CORE_VERSION").substring(1)
val coreJavaVersion = props("LIB_CORE_JAVA_VERSION").substring(1)
val platformJvmWindowsVersion = props("LIB_PLATFORM_JVM_WINDOWS_VERSION").substring(1)
val platformJvmLinuxVersion = props("LIB_PLATFORM_JVM_LINUX_VERSION").substring(1)
val platformJvmMacosVersion = props("LIB_PLATFORM_JVM_MACOS_VERSION").substring(1)

kotlin {
    jvmToolchain(22)

    androidTarget()
    jvm()
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)


            implementation(project(":core"))
//            implementation("top.kagg886.mkmb:core:$coreVersion")
        }

        //不同操作系统引入不同的二进制文件，若要引入所有平台就写3个implementation。
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(project(":platform:platform-windows"))
            implementation(project(":platform:platform-linux"))
            implementation(project(":platform:platform-macos"))
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }
    }
}

android {
    namespace = "sample.app"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
        targetSdk = 35

        applicationId = "sample.app.androidApp"
        versionCode = 1
        versionName = "1.0.0"
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "sample"
            packageVersion = "1.0.0"
        }
    }
}
