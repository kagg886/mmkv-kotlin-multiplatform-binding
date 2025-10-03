# MMKV-Kotlin-Multiplatform-Binding

[中文版本请参看这里](./README_CN.md)

## Introduction

**MMKV-Kotlin-Multiplatform-Binding** (abbreviated as **MKMB**) is a Kotlin Multiplatform wrapper for [MMKV](https://github.com/Tencent/MMKV), providing high-performance key-value storage across multiple platforms.

## Modules & Versions

| Module           | Version                                                      | Description                                       |
| ---------------- | ------------------------------------------------------------ | ------------------------------------------------- |
| `core`           | ![](https://img.shields.io/maven-central/v/top.kagg886.mkmb/core) | Core API for all platforms                        |
| `core-java`      | ![](https://img.shields.io/maven-central/v/top.kagg886.mkmb/core-java) | Java-compatible wrapper                           |
| `ext`            | ![](https://img.shields.io/maven-central/v/top.kagg886.mkmb/ext) | Extensions (Flow, typed accessors, etc.)          |
| `platform-windows` | ![](https://img.shields.io/maven-central/v/top.kagg886.mkmb/platform-windows) | Native binaries for Windows (JVM)                 |
| `platform-linux` | ![](https://img.shields.io/maven-central/v/top.kagg886.mkmb/platform-linux) | Native binaries for Linux (JVM)                   |
| `platform-macos` | ![](https://img.shields.io/maven-central/v/top.kagg886.mkmb/platform-macos) | Native binaries for macOS (JVM)                   |
| `platform-android` | ![](https://img.shields.io/maven-central/v/top.kagg886.mkmb/platform-android) | Native binaries for Android (auto-included)       |
| `platform-ios`   | ![](https://img.shields.io/maven-central/v/top.kagg886.mkmb/platform-ios) | Native binaries for iOS (auto-included)           |

## Supported Platforms

- [x] Windows (JVM)
- [x] Linux (JVM)
- [x] macOS (JVM)
- [x] Android (JVM)
- [x] iOS (Native)

## Installation

### 1. Core Module (Required)

Add the `core` module to your project:

```kotlin
dependencies {
    implementation("top.kagg886.mkmb:core:${latest_version}")
}
```

### 2. Platform-Specific Dependencies

#### For JVM (Desktop) Platforms

Desktop platforms require platform-specific native libraries. **Requires Java 22+** (uses [Project Panama](https://openjdk.org/projects/panama/) for FFI).

```kotlin
kotlin {
    sourceSets {
        jvmMain.dependencies {
            // Choose the platform library that matches your OS:
            implementation("top.kagg886.mkmb:platform-windows:${latest_version}") // Windows
            implementation("top.kagg886.mkmb:platform-linux:${latest_version}")   // Linux
            implementation("top.kagg886.mkmb:platform-macos:${latest_version}")   // macOS
            
            // Or detect at runtime:
            val platform = when {
                System.getProperty("os.name") == "Mac OS X" -> "macos"
                System.getProperty("os.name").startsWith("Win") -> "windows"
                System.getProperty("os.name").startsWith("Linux") -> "linux"
                else -> error("Unsupported OS")
            }
            implementation("top.kagg886.mkmb:platform-${platform}:${latest_version}")
        }
    }
}
```

#### For Android & iOS

Android and iOS platform binaries are **automatically included** with the `core` module. No additional dependencies needed.

### 3. Extension Module (Optional)

For enhanced features like Kotlin `Flow` support, typed accessors, and more:

```kotlin
dependencies {
    implementation("top.kagg886.mkmb:ext:${latest_version}")
}
```

## Quick Start

```kotlin
// 1. Initialize once at app startup
MMKV.initialize("/path/to/storage")

// 2. Get an instance
val kv = MMKV.defaultMMKV()

// 3. Store and retrieve values
kv.set("key", "value")
val value = kv.getString("key")
```

## API Overview

For complete API documentation, visit **[mmkv.kagg886.top](https://mmkv.kagg886.top)**

### Core Features
- ✅ Initialization & configuration
- ✅ Multi-instance management (`defaultMMKV`, `mmkvWithID`)
- ✅ Type support: `Int`, `Long`, `Float`, `Double`, `Boolean`, `String`, `ByteArray`, `List<String>`
- ✅ Key operations: `remove`, `clear`, `exists`, `allKeys`, `size`
- ✅ Instance lifecycle: `destroy`, `isAlive`
- ✅ Encryption support
- ✅ Multi-process mode

### Platform-Specific Features
- **Android**: `Parcelable` storage, `SharedPreferences` migration
- **iOS**: `NSCoding` object storage

### Extension Features (via `ext` module)
- 🔄 Reactive updates via `flow<T>(key)`
- 🎯 Type-safe accessors
- 📝 More idiomatic Kotlin API

## Example

```kotlin
import top.kagg886.mkmb.MMKV
import top.kagg886.mkmb.MMKVMode

// Initialize
MMKV.initialize("/data/mmkv") {
    logLevel = MMKVOptions.LogLevel.Info
}

// Basic usage
val kv = MMKV.defaultMMKV()
kv.set("count", 42)
println(kv.getInt("count")) // 42

// With encryption
val encrypted = MMKV.mmkvWithID("secure", cryptKey = "my-secret-key")
encrypted.set("password", "p@ssw0rd")

// Multi-process
val shared = MMKV.mmkvWithID("shared", mode = MMKVMode.MULTI_PROCESS)
```

## License

See [LICENSE](./LICENSE)
