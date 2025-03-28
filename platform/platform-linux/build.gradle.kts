plugins {
    alias(libs.plugins.multiplatform)
}

group = "top.kagg886.mkmb"
version = "1.0.0"


kotlin {
    jvmToolchain(22)
    jvm()
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
}

// 配置JVM的processResources任务
tasks.named<ProcessResources>("jvmProcessResources") {
    dependsOn(processBuild)
    from(project.file("native-binding-linux/build/libmmkvc.so"))
}
