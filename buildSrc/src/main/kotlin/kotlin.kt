import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.jetbrains.compose.android.AndroidExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * ================================================
 * Author:     886kagg
 * Created on: 2025/8/25 14:17
 * ================================================
 */

fun Project.library(
    block: KotlinMultiplatformExtension.() -> Unit = {}
) {
    extensions.configure<KotlinMultiplatformExtension>("kotlin") {
        jvmToolchain(22)
        androidTarget { publishLibraryVariants("release") }
        jvm()
        iosArm64()
        iosSimulatorArm64()

        block()
    }
    android()
}


internal fun Project.android() = extensions.configure<LibraryExtension>("android") {
    namespace = group.toString()
    compileSdk = 35

    defaultConfig {
        minSdk = 28
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}
