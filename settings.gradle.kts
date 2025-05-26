rootProject.name = "mmkv-multiplatform-binding"

pluginManagement {
    repositories {
        google {
            content {
              	includeGroupByRegex("com\\.android.*")
              	includeGroupByRegex("com\\.google.*")
              	includeGroupByRegex("androidx.*")
              	includeGroupByRegex("android.*")
            }
        }
        gradlePluginPortal()
        mavenCentral()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

dependencyResolutionManagement {
    repositories {
        google {
            content {
              	includeGroupByRegex("com\\.android.*")
              	includeGroupByRegex("com\\.google.*")
              	includeGroupByRegex("androidx.*")
              	includeGroupByRegex("android.*")
            }
        }
        mavenCentral()
    }
}
include(":core")
include(":core-java")
include(":benchmark")

include("platform:platform-windows")
findProject(":platform:platform-windows")?.name = "platform-windows"

include("platform:platform-linux")
findProject(":platform:platform-linux")?.name = "platform-linux"


include("platform:platform-macos")
findProject(":platform:platform-macos")?.name = "platform-macos"

include("platform:platform-android")
findProject(":platform:platform-android")?.name = "platform-android"

include("platform:platform-ios")
findProject(":platform:platform-ios")?.name = "platform-ios"
