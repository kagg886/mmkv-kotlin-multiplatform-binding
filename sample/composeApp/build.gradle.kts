import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("com.android.application")
}

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
        minSdk = 28
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
