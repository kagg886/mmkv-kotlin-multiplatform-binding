# MMKV-Kotlin-Multiplatform-Binding

The English Version is [Here](./README.md)

## 简介

**MMKV-Kotlin-Multiplatform-Binding**(后文简称**MKMB**)是[MMKV](https://github.com/Tencent/MMKV)的Kotlin封装。

## 版本

| core                                                         | core-java                                                    |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| ![](https://img.shields.io/maven-central/v/top.kagg886.mkmb/core) | ![](https://img.shields.io/maven-central/v/top.kagg886.mkmb/core-java) |

| platform-windows                                             | platform-linux                                               | platform-macos                                               | platform-android                                             | platform-ios                                                 |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| ![](https://img.shields.io/maven-central/v/top.kagg886.mkmb/platform-windows) | ![](https://img.shields.io/maven-central/v/top.kagg886.mkmb/platform-linux) | ![](https://img.shields.io/maven-central/v/top.kagg886.mkmb/platform-macos) | ![](https://img.shields.io/maven-central/v/top.kagg886.mkmb/platform-android) | ![](https://img.shields.io/maven-central/v/top.kagg886.mkmb/platform-ios) |

## 支持的平台

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
- [x] IOS

## 安装引入

1. 引入`core`模块，这是一个 `Kotlin-MultiPlatform` 模块，提供访问各个操作系统的门面。

   ```kotlin
   dependencies {
       implementation("top.kagg886.mkmb:core:${latest_version}")
   }
   ```

2. JVM平台需要引入不同的动态库：

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
   //不同操作系统引入不同的二进制文件，若要引入所有平台就写3个implementation。
   jvmMain.dependencies {
       implementation("top.kagg886.mkmb:platform-${hostTarget.name.lowercase()}:${latest_version}")
   }
   ```

   > 在Desktop平台使用了 [Project Panama](https://openjdk.org/projects/panama/) 做FFI绑定，因此使用该库要求你的**JAVA版本>=22**。

3. Android，iOS平台会自动随着`core`模块引入。

## 快速上手

与MMKV的生命周期一样，在全局使用之前需要进行初始化

```kotlin
MMKV.initialize("base-path")
```

随后便可以向C++的MMKVApi一样进行操作：

```kotlin
val mmkv = MMKV.defaultMMKV()
println(mmkv.getInt("qwq")) //0
mmkv.set("qwq",2)
println(mmkv.getInt("qwq")) //2
```

## 支持操作

MKMB计划支持：

### 实例化

- 获取默认MMKV实例
- 根据 `mmapID` 获取MMKV实例

### 类型存取

#### 1. Common平台

- Int

- String
- Boolean
- Long
- Float
- Double
- ByteArray
- List\<String\>

#### 2. Android平台

- 任何实现了`Parcelable`的对象

#### 3. iOS平台

- 任何实现了`NSCoding`协议的对象

### 集合操作

- 根据Key移除Value
- 清空
- 获取大小
- 获取键集合
- 检查key是否存在

### 实例操作

- 销毁MMKV及其本地存储
- 检测MMKV实例是否被销毁

### 迁移操作

- 从 `SharePreferences`迁移数据



