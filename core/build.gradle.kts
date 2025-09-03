import com.vanniktech.maven.publish.KotlinMultiplatform
import org.jetbrains.dokka.gradle.DokkaTask


plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.parcelize")
    alias(libs.plugins.kotlinx.atomicfu)
    id("com.vanniktech.maven.publish")
    id("org.jetbrains.dokka")
}

group = "top.kagg886.mkmb"
version()

library {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.atomicfu)
        }

        commonTest.dependencies {
            implementation(libs.okio)
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.core)
        }

        jvmTest.dependencies {
            implementation(project(":platform:platform-${hostTarget.name.lowercase()}"))
        }

        androidMain.dependencies {
            implementation(project(":platform:platform-android"))
        }

        iosMain.dependencies {
            implementation(project(":platform:platform-ios"))
        }


        androidInstrumentedTest.dependencies {
            implementation(libs.androidx.junit)
            implementation(libs.junit)
            implementation(libs.androidx.espresso.core)
        }
    }
}

publishing(KotlinMultiplatform(sourcesJar = true))
