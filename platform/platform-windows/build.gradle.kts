import com.vanniktech.maven.publish.KotlinJvm

import java.io.FileInputStream
import java.security.MessageDigest

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.maven.publish)
}

val SKIP_SIGN = (System.getenv("SKIP_SIGN") ?: project.findProperty("SKIP_SIGN") as? String ?: "false").toBooleanStrict()
val LIB_PLATFORM_JVM_WINDOWS_VERSION = System.getenv("LIB_PLATFORM_JVM_WINDOWS_VERSION") ?: project.findProperty("LIB_PLATFORM_JVM_WINDOWS_VERSION") as? String ?: "unsetted."
check(LIB_PLATFORM_JVM_WINDOWS_VERSION.startsWith("v")) {
    "LIB_PLATFORM_JVM_WINDOWS_VERSION not supported, current is $LIB_PLATFORM_JVM_WINDOWS_VERSION"
}

group = "top.kagg886.mkmb"
version = LIB_PLATFORM_JVM_WINDOWS_VERSION.substring(1)

println("LIB_PLATFORM_JVM_WINDOWS_VERSION: $version")

kotlin {
    jvmToolchain(22)
}

val processBuild = tasks.register<Exec>("processBuild") {
    onlyIf {
        System.getProperty("os.name").startsWith("Win")
    }
    workingDir = project.file("native-binding-windows")
    commandLine("pwsh","-c","""
        msbuild native-binding-windows.sln /p:Configuration=Release
        (Get-FileHash -Algorithm SHA256 -Path "x64/Release/mmkvc.dll").Hash | Out-File -FilePath "x64/Release/build-windows.hash"
    """.trimIndent())
}

// 配置JVM的processResources任务
tasks.named<ProcessResources>("processResources") {
    dependsOn(processBuild)
    from(project.file("native-binding-windows/x64/Release/mmkvc.dll"))
    from(project.file("native-binding-windows/x64/Release/build-windows.hash"))
}

mavenPublishing {
    configure(KotlinJvm())
    publishToMavenCentral(true)
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
