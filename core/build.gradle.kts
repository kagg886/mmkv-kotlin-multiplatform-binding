plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlinx.atomicfu)
}

group = "top.kagg886.mkmb"
version = "1.0.0"

enum class JvmTarget {
    MACOS,
    WINDOWS,
    LINUX;
}

val hostTarget by lazy {
    val osName = System.getProperty("os.name")
    when {
        osName == "Mac OS X" -> JvmTarget.MACOS
        osName.startsWith("Win") -> JvmTarget.WINDOWS
        osName.startsWith("Linux") -> JvmTarget.LINUX
        else -> error("Unsupported OS: $osName")
    }
}


kotlin {
    jvmToolchain(22)

//    androidTarget { publishLibraryVariants("release") }
    jvm()

//    iosX64()
//    iosArm64()
//    iosSimulatorArm64()

    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(project(":platform:platform-${hostTarget.name.lowercase()}"))
        }
    }
}

android {
    namespace = "top.kagg886.mkmb"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
    }
}
