import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.SonatypeHost
import java.io.FileInputStream
import java.security.MessageDigest

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.maven.publish)
}

val SKIP_SIGN = (System.getenv("SKIP_SIGN") ?: project.findProperty("SKIP_SIGN") as? String ?: "false").toBooleanStrict()
val LIB_PLATFORM_JVM_MACOS_VERSION = System.getenv("LIB_PLATFORM_JVM_MACOS_VERSION") ?: project.findProperty("LIB_PLATFORM_JVM_MACOS_VERSION") as? String ?: "unsetted."
check(LIB_PLATFORM_JVM_MACOS_VERSION.startsWith("v")) {
    "LIB_PLATFORM_JVM_MACOS_VERSION not supported, current is $LIB_PLATFORM_JVM_MACOS_VERSION"
}

group = "top.kagg886.mkmb"
version = LIB_PLATFORM_JVM_MACOS_VERSION.substring(1)

println("LIB_PLATFORM_JVM_MACOS_VERSION: $version")

kotlin {
    jvmToolchain(22)
}

val processBuild = tasks.register<Exec>("processBuild") {
    onlyIf {
        System.getProperty("os.name").startsWith("Mac")
    }
    workingDir = project.file("native-binding-macos")
    commandLine(
        "zsh", "-c",
        """
            mkdir -p build && \
            cd build && \
            cmake .. && \
            make &&\
            echo $(shasum -a 256 libmmkvc.dylib | cut -d ' ' -f 1) > build-macos.hash
        """.trimIndent()
    )
}

// 配置JVM的processResources任务
tasks.named<ProcessResources>("processResources") {
    dependsOn(processBuild)
    from(project.file("native-binding-macos/build/libmmkvc.dylib"))
    from(project.file("native-binding-macos/build/build-macos.hash"))
}


mavenPublishing {
    configure(KotlinJvm())
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
