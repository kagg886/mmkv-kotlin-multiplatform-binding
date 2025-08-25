import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.Platform
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension

/**
 * ================================================
 * Author:     886kagg
 * Created on: 2025/8/25 14:07
 * ================================================
 */

private val Project.SKIP_SIGN
    get() = (System.getenv("SKIP_SIGN") ?: project.findProperty("SKIP_SIGN") as? String ?: "false").toBooleanStrict()

fun Project.publishing(platform: Platform) = extensions.configure<MavenPublishBaseExtension>("mavenPublishing") {
    configure(platform)

    publishToMavenCentral(true)
    if (!SKIP_SIGN) {
        signAllPublications()
    } else {
        println("SKIP SIGN enabled")
    }
    coordinates(group.toString(), project.name, version.toString())
    pom {
        name.set("MMKV-multiplatform-binding")
        description.set("An api accesser wrapped tencent-mmkv by kotlin ")
        inceptionYear.set("2025")
        url.set("https://github.com/kagg886/mmkv-kotlin-multiplatform-binding")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("kagg886")
                name.set("kagg886")
                url.set("https://github.com/kagg886/")
            }
        }
        scm {
            url.set("https://github.com/kagg886/mmkv-kotlin-multiplatform-binding")
            connection.set("scm:git:git://github.com/kagg886/mmkv-kotlin-multiplatform-binding.git")
            developerConnection.set("scm:git:ssh://git@github.com/kagg886/mmkv-kotlin-multiplatform-binding.git")
        }
    }
}
