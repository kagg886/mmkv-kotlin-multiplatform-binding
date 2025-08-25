plugins {
    id("org.jetbrains.kotlin.multiplatform").apply(false)
    id("com.android.library").apply(false)
    id("org.jetbrains.kotlin.plugin.parcelize").apply(false)
    id("org.jetbrains.kotlin.android").apply(false)
    id("org.jetbrains.kotlin.jvm").apply(false)
    id("com.android.application").apply(false)
    alias(libs.plugins.kotlinx.atomicfu).apply(false)
    id("com.vanniktech.maven.publish").apply(false)
}
