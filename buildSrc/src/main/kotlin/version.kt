import org.gradle.api.Project

/**
 * ================================================
 * Author:     886kagg
 * Created on: 2025/8/25 14:33
 * ================================================
 */

fun Project.version(): String {
    val key = "LIB_${name.replace("-", "_").uppercase()}_VERSION"
    val version = System.getenv(key) ?: project.findProperty(key) as? String ?: "unsetted."
    check(version.startsWith("v")) {
        "$key not supported, current is $version"
    }
    this.version = version.removePrefix("v")
    println("$key version: $version")
    return version.removePrefix("v")
}
