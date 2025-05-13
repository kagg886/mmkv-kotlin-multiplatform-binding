import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.SonatypeHost
import java.io.FileInputStream
import java.security.MessageDigest

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.maven.publish)
}

val SKIP_SIGN = (System.getenv("SKIP_SIGN") ?: project.findProperty("SKIP_SIGN") as? String ?: "false").toBooleanStrict()
val APP_VERSION = System.getenv("APP_VERSION") ?: project.findProperty("APP_VERSION") as? String ?: "unsetted."
check(APP_VERSION.startsWith("v")) {
    "APP_VERSION not supported, current is $APP_VERSION"
}

group = "top.kagg886.mkmb"
version = APP_VERSION.substring(1)

println("LIB_PLATFORM_LINUX_VERSION: $version")

kotlin {
    jvmToolchain(22)
}

val processBuild = tasks.register<Exec>("processBuild") {
    onlyIf {
        System.getProperty("os.name").startsWith("Linux")
    }
    workingDir = project.file("native-binding-linux")
    commandLine(
        "bash", "-c",
        """
            mkdir -p build && \
            cd build && \
            cmake .. && \
            make
        """.trimIndent()
    )

    doLast {
        val file = project.file("native-binding-linux/build/libmmkvc.so")
        check(file.exists()) {
            "libmmkvc.so not found, please check your build environment."
        }
        val hash = file.sha256()
        val hashFile = project.file("native-binding-linux/build/build-linux.hash")
        if (hashFile.exists()) hashFile.delete()
        hashFile.createNewFile()
        hashFile.writeText(hash)
    }
}

// 配置JVM的processResources任务
tasks.named<ProcessResources>("processResources") {
    dependsOn(processBuild)
    from(project.file("native-binding-linux/build/libmmkvc.so"))
    from(project.file("native-binding-linux/build/build-linux.hash"))
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

fun File.sha256(): String {
    val buffer = ByteArray(8192)
    val digest = MessageDigest.getInstance("SHA-256")

    FileInputStream(this).use { inputStream ->
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            digest.update(buffer, 0, bytesRead)
        }
    }

    return digest.digest().joinToString("") { "%02x".format(it) }
}
