import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.maven.publish)
}

val SKIP_SIGN = (System.getenv("SKIP_SIGN") ?: project.findProperty("SKIP_SIGN") as? String ?: "false").toBooleanStrict()
val APP_VERSION = System.getenv("APP_VERSION") ?: project.findProperty("APP_VERSION") as? String ?: "unsetted."
check(APP_VERSION.startsWith("v")) {
    "APP_VERSION not supported, current is $APP_VERSION"
}

group = "top.kagg886.mkmb"
version = APP_VERSION.substring(1)

println("LIB_PLATFORM_IOS_VERSION: $version")

kotlin {
    jvmToolchain(22)

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { targets->
        targets.apply {
            compilations.all {
                cinterops {
                    val mmkvc by creating {
                        defFile("src/interop/libmmkvc.def")
                        packageName("mmkvc")
                        includeDirs("src/interop/include")
                    }
                }
            }
        }
    }

    sourceSets {
    }
}

val processIosArm64Build = tasks.register<Exec>("processIosArm64Build") {
    onlyIf {
        System.getProperty("os.name").startsWith("Mac")
    }
    workingDir(project.file("native-binding-ios"))
    val cmd = "xcodebuild -scheme native-binding-ios -configuration Release -sdk iphoneos ARCHS=arm64 BUILD_DIR=${project.file("build/mmkvc")}"
    commandLine("bash","-c",cmd)
}
//cinteropTestMmkvcIosSimulatorArm64
//cinteropTestMmkvcIosArm64
//cinterop    MmkvcIosSimulatorArm64
//cinterop    MmkvcIosArm64

for (task in listOf("cinteropTestMmkvcIosSimulatorArm64", "cinteropTestMmkvcIosArm64", "cinteropMmkvcIosSimulatorArm64", "cinteropMmkvcIosArm64")) {
    tasks.named(task) {
        dependsOn(processIosArm64Build)
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
