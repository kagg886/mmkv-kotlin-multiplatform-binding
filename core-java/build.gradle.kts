import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar


plugins {
    java
    id("com.vanniktech.maven.publish")
}

group = "top.kagg886.mkmb"
version()

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

publishing(JavaLibrary(javadocJar = JavadocJar.Empty()))
