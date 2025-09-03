import com.vanniktech.maven.publish.KotlinJvm

import java.io.FileInputStream
import java.security.MessageDigest

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.vanniktech.maven.publish")
}

group = "top.kagg886.mkmb"
version()

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

// Configure JVM processResources task
tasks.named<ProcessResources>("processResources") {
    dependsOn(processBuild)
    from(project.file("native-binding-windows/x64/Release/mmkvc.dll"))
    from(project.file("native-binding-windows/x64/Release/build-windows.hash"))
}

publishing(KotlinJvm())
