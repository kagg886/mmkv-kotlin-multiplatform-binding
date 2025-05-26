plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "top.kagg886.mkmb.benchmark"
version = "1.0"
repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation(project(":platform:platform-windows"))
}
tasks.test {
    useJUnitPlatform()
}
