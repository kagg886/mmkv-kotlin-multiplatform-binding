import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlinx.atomicfu)
    alias(libs.plugins.maven.publish)
}

val SKIP_SIGN = (System.getenv("SKIP_SIGN") ?: project.findProperty("SKIP_SIGN") as? String ?: "false").toBooleanStrict()
val APP_VERSION = System.getenv("APP_VERSION") ?: project.findProperty("APP_VERSION") as? String ?: "unsetted."
check(APP_VERSION.startsWith("v")) {
    "APP_VERSION not supported, current is $APP_VERSION"
}

group = "top.kagg886.mkmb"
version = APP_VERSION.substring(1)

println("LIB_CORE_VERSION: $version")

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

    androidTarget { publishLibraryVariants("release") }
    jvm()

//    iosX64()
//    iosArm64()
//    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.atomicfu)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(project(":platform:platform-${hostTarget.name.lowercase()}"))
        }

        androidMain.dependencies {
            implementation(project(":platform:platform-android"))
        }


        androidInstrumentedTest.dependencies {
            implementation(libs.androidx.junit)
            implementation(libs.junit)
            implementation(libs.androidx.espresso.core)
        }
    }
}

android {
    namespace = "top.kagg886.mkmb"
    compileSdk = 35

    defaultConfig {
        minSdk = 28
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

mavenPublishing {
    configure(
        KotlinMultiplatform(
            // whether to publish a sources jar
            sourcesJar = true,
        )
    )
    publishToMavenCentral(SonatypeHost.S01)
    if (!SKIP_SIGN) {
        signAllPublications()
    }
    coordinates(group.toString(), project.name, version.toString())
    pom {
        name = "MMKV-multiplatform-binding"
        description = "An api accesser wrapped tencent-mmkv by kotlin "
        inceptionYear = "2025"
        url = "https://github.com/kagg886/mmkv-kotlin-multiplatform-binding"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "kagg886"
                name = "kagg886"
                url = "https://github.com/kagg886/"
            }
        }
        scm {
            url = "https://github.com/kagg886/mmkv-kotlin-multiplatform-binding"
            connection = "scm:git:git://github.com/kagg886/mmkv-kotlin-multiplatform-binding.git"
            developerConnection = "scm:git:ssh://git@github.com/kagg886/mmkv-kotlin-multiplatform-binding.git"
        }
    }
}
