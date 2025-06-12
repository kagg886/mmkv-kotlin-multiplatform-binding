# MMKV-Kotlin-Multiplatform-Binding

[中文版本请参看这里](./README_CN.md)

## Introduction

**MMKV-Kotlin-Multiplatform-Binding** (abbreviated as **MKMB**) is a Kotlin wrapper for [MMKV](https://github.com/Tencent/MMKV).

## Version

| core                                                         | core-java                                                    |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| ![](https://img.shields.io/maven-central/v/top.kagg886.mkmb/core) | ![](https://img.shields.io/maven-central/v/top.kagg886.mkmb/core-java) |

| platform-windows                                             | platform-linux                                               | platform-macos                                               | platform-android                                             | platform-ios                                                 |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| ![](https://img.shields.io/maven-central/v/top.kagg886.mkmb/platform-windows) | ![](https://img.shields.io/maven-central/v/top.kagg886.mkmb/platform-linux) | ![](https://img.shields.io/maven-central/v/top.kagg886.mkmb/platform-macos) | ![](https://img.shields.io/maven-central/v/top.kagg886.mkmb/platform-android) | ![](https://img.shields.io/maven-central/v/top.kagg886.mkmb/platform-ios) |

## Supported Platforms

- [x] Windows
  - [x] JVM
  - [ ] Native
- [x] Linux
  - [x] JVM
  - [ ] Native
- [x] macOS
  - [x] JVM
  - [ ] Native
- [x] Android(JVM)
- [x] iOS

## Installation

1. Import the `core` module, which is a `Kotlin-MultiPlatform` module that provides a facade for accessing various operating systems.

   ```kotlin
   dependencies {
       implementation("top.kagg886.mkmb:core:${latest_version}")
   }
   ```

2. JVM platforms need to import different dynamic libraries:

   ```kotlin
   enum class JvmTarget {
       MACOS,
       WINDOWS,
       LINUX;
   }
   
   val hostTarget by lazy {
       val osName = System.getProperty("os.name")
       when {
           osName == "Mac OS X" -> JvmTarget.MACOS
           osName.startsWith("Win") -> JvmTarget.WINDOWS
           osName.startsWith("Linux") -> JvmTarget.LINUX
           else -> error("Unsupported OS: $osName")
       }
   }
   // Import different binary files for different operating systems.
   // If you want to import all platforms, write 3 implementation statements.
   jvmMain.dependencies {
       implementation("top.kagg886.mkmb:platform-${hostTarget.name.lowercase()}:${latest_version}")
   }
   ```

   > On the Desktop platform, [Project Panama](https://openjdk.org/projects/panama/) is used for FFI binding, so using this library requires your **JAVA version >= 22**.

3. Android and iOS platforms will be automatically imported along with the `core` module.

## Quick Start

Like MMKV's lifecycle, initialization is required before global use.

```kotlin
MMKV.initialize("base-path")
```

Then you can operate it like the C++ MMKV API:

```kotlin
val mmkv = MMKV.defaultMMKV()
println(mmkv.getInt("qwq")) //0
mmkv.set("qwq",2)
println(mmkv.getInt("qwq")) //2
```

## Supported Operations

MKMB plans to support:

### Instantiation

- Get default MMKV instance
- Get MMKV instance based on `mmapID`

### Type Storage and Retrieval

#### 1. Common

- Int
- String
- Boolean
- Long
- Float
- Double
- ByteArray
- List\<String\>

#### 2. Android

- An object which implements `Parcelable` interface

#### 3. iOS

- An object which implements `NSCoding` protocol

### Collection Operations

- Remove Value by Key
- Clear
- Get size
- Get key set
- Check if key exists

### Instance Operations

- Destroy MMKV and its local storage
- Check if MMKV instance has been destroyed

### Migrate Operation

- migrate from `SharePreferences`
