import com.vanniktech.maven.publish.KotlinMultiplatform


plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.vanniktech.maven.publish")
}

group = "top.kagg886.mkmb"
version()

kotlin {
    jvmToolchain(22)

    iosArm64 {
        compilations.all {
            cinterops {
                val mmkv by creating {
                    defFile("src/interop/libMMKV.def")
                    packageName("mmkv")
                    includeDirs(project.file("build/mmkv/Release-iphoneos/include"))
                }
            }
        }
    }

    iosSimulatorArm64 {
        compilations.all {
            cinterops {
                val mmkv by creating {
                    defFile("src/interop/libMMKV.def")
                    packageName("mmkv")
                    includeDirs(project.file("build/mmkv/Release-iphonesimulator/include"))
                }
            }
        }
    }

    sourceSets {
    }
}

val prepareMMKVIosArm64Build = tasks.register<Exec>("processIosArm64Build") {
    onlyIf {
        System.getProperty("os.name").startsWith("Mac")
    }
    workingDir(rootProject.file("MMKV/iOS/MMKV"))
    val cmd = "xcodebuild -configuration Release -sdk iphoneos ARCHS=arm64 BUILD_DIR=${project.file("build/mmkv")}"
    commandLine("zsh","-c",cmd)
}

tasks.named("cinteropMmkvIosArm64") {
    dependsOn(prepareMMKVIosArm64Build)
}

tasks.named("cinteropTestMmkvIosArm64") {
    dependsOn(prepareMMKVIosArm64Build)
}

val prepareMMKVIosSimulatorArm64Build = tasks.register<Exec>("prepareMMKVIosSimulatorArm64Build") {
    onlyIf {
        System.getProperty("os.name").startsWith("Mac")
    }
    workingDir(rootProject.file("MMKV/iOS/MMKV"))
    val cmd = "xcodebuild -configuration Release -sdk iphonesimulator ARCHS=arm64 BUILD_DIR=${project.file("build/mmkv")}"
    commandLine("zsh","-c",cmd)
}

tasks.named("cinteropMmkvIosSimulatorArm64") {
    dependsOn(prepareMMKVIosSimulatorArm64Build)
}

tasks.named("cinteropTestMmkvIosSimulatorArm64") {
    dependsOn(prepareMMKVIosSimulatorArm64Build)
}


//cinteropTestMmkvcIosSimulatorArm64
//cinteropTestMmkvcIosArm64
//cinterop    MmkvcIosSimulatorArm64
//cinterop    MmkvcIosArm64

//for (task in listOf("cinteropTestMmkvcIosSimulatorArm64", "cinteropTestMmkvcIosArm64", "cinteropMmkvcIosSimulatorArm64", "cinteropMmkvcIosArm64")) {
//    tasks.named(task) {
//        dependsOn(processIosArm64Build)
//    }
//}

publishing(KotlinMultiplatform(sourcesJar = true))
