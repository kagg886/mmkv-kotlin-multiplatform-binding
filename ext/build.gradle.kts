import com.vanniktech.maven.publish.KotlinMultiplatform


plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
    id("com.vanniktech.maven.publish")
    id("org.jetbrains.dokka")
    id("org.jetbrains.kotlin.plugin.parcelize")
}

group = "top.kagg886.mkmb"
val extVersion = version()

library {
    sourceSets {
        commonMain.dependencies {
            api(project(":core"))
            implementation(libs.kotlinx.io)
            implementation(libs.kotlinx.coroutines.core)
        }

        commonTest.dependencies {
            implementation(libs.okio)
            implementation(kotlin("test"))
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

dokka {
    moduleName.set("mmkv-multiplatform-binding:ext")
    pluginsConfiguration {
        versioning {
            version.set(extVersion)
        }
    }

    dokkaSourceSets.configureEach {
        includes.from("Module.md")
    }
}
