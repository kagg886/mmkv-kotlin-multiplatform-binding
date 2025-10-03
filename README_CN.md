# MMKV-Kotlin-Multiplatform-Binding

[English Version is Here](./README.md)

## 简介

**MMKV-Kotlin-Multiplatform-Binding**（后文简称 **MKMB**）是 [MMKV](https://github.com/Tencent/MMKV) 的 Kotlin 多平台封装，为多个平台提供高性能的键值存储能力。

## 模块与版本

| 模块               | 版本                                                         | 说明                                  |
| ------------------ | ------------------------------------------------------------ | ------------------------------------- |
| `core`             | ![](https://img.shields.io/maven-central/v/top.kagg886.mkmb/core) | 核心 API，支持所有平台                |
| `core-java`        | ![](https://img.shields.io/maven-central/v/top.kagg886.mkmb/core-java) | Java 兼容封装                         |
| `ext`              | ![](https://img.shields.io/maven-central/v/top.kagg886.mkmb/ext) | 扩展功能（Flow、类型安全访问器等）    |
| `platform-windows` | ![](https://img.shields.io/maven-central/v/top.kagg886.mkmb/platform-windows) | Windows 平台原生库（JVM）             |
| `platform-linux`   | ![](https://img.shields.io/maven-central/v/top.kagg886.mkmb/platform-linux) | Linux 平台原生库（JVM）               |
| `platform-macos`   | ![](https://img.shields.io/maven-central/v/top.kagg886.mkmb/platform-macos) | macOS 平台原生库（JVM）               |
| `platform-android` | ![](https://img.shields.io/maven-central/v/top.kagg886.mkmb/platform-android) | Android 平台原生库（自动引入）        |
| `platform-ios`     | ![](https://img.shields.io/maven-central/v/top.kagg886.mkmb/platform-ios) | iOS 平台原生库（自动引入）            |

## 支持的平台

- [x] Windows（JVM）
- [x] Linux（JVM）
- [x] macOS（JVM）
- [x] Android（JVM）
- [x] iOS（Native）

## 安装引入

### 1. Core 模块（必需）

在项目中添加 `core` 模块：

```kotlin
dependencies {
    implementation("top.kagg886.mkmb:core:${latest_version}")
}
```

### 2. 平台特定依赖

#### JVM（桌面）平台

桌面平台需要引入平台特定的原生库。**需要 Java 22+**（使用 [Project Panama](https://openjdk.org/projects/panama/) 做 FFI 绑定）。

```kotlin
kotlin {
    sourceSets {
        jvmMain.dependencies {
            // 根据操作系统选择对应的平台库：
            implementation("top.kagg886.mkmb:platform-windows:${latest_version}") // Windows
            implementation("top.kagg886.mkmb:platform-linux:${latest_version}")   // Linux
            implementation("top.kagg886.mkmb:platform-macos:${latest_version}")   // macOS
            
            // 或者运行时自动检测：
            val platform = when {
                System.getProperty("os.name") == "Mac OS X" -> "macos"
                System.getProperty("os.name").startsWith("Win") -> "windows"
                System.getProperty("os.name").startsWith("Linux") -> "linux"
                else -> error("不支持的操作系统")
            }
            implementation("top.kagg886.mkmb:platform-${platform}:${latest_version}")
        }
    }
}
```

#### Android 与 iOS 平台

Android 和 iOS 平台的原生库会**自动随 `core` 模块引入**，无需额外配置。

### 3. 扩展模块（可选）

如需使用增强特性（如 Kotlin `Flow` 支持、类型安全访问器等）：

```kotlin
dependencies {
    implementation("top.kagg886.mkmb:ext:${latest_version}")
}
```

## 快速上手

```kotlin
// 1. 在应用启动时初始化一次
MMKV.initialize("/存储路径")

// 2. 获取实例
val kv = MMKV.defaultMMKV()

// 3. 存取值
kv.set("key", "value")
val value = kv.getString("key")
```

## API 概览

完整 API 文档请访问 **[mmkv.kagg886.top](https://mmkv.kagg886.top)**

### 核心功能
- ✅ 初始化与配置
- ✅ 多实例管理（`defaultMMKV`、`mmkvWithID`）
- ✅ 类型支持：`Int`、`Long`、`Float`、`Double`、`Boolean`、`String`、`ByteArray`、`List<String>`
- ✅ 键操作：`remove`、`clear`、`exists`、`allKeys`、`size`
- ✅ 实例生命周期：`destroy`、`isAlive`
- ✅ 加密支持
- ✅ 多进程模式

### 平台特定功能
- **Android**：`Parcelable` 对象存储、`SharedPreferences` 迁移
- **iOS**：`NSCoding` 对象存储

### 扩展功能（通过 `ext` 模块）

- 🔄 通过 `flow<T>(key)` 响应式监听变化
- 🎯 类型安全访问器
- 📝 更符合 Kotlin 习惯的 API

## 示例

```kotlin
import top.kagg886.mkmb.MMKV
import top.kagg886.mkmb.MMKVMode

// 初始化
MMKV.initialize("/data/mmkv") {
    logLevel = MMKVOptions.LogLevel.Info
}

// 基础用法
val kv = MMKV.defaultMMKV()
kv.set("count", 42)
println(kv.getInt("count")) // 42

// 加密存储
val encrypted = MMKV.mmkvWithID("secure", cryptKey = "my-secret-key")
encrypted.set("password", "p@ssw0rd")

// 多进程模式
val shared = MMKV.mmkvWithID("shared", mode = MMKVMode.MULTI_PROCESS)
```

## 许可证

详见 [LICENSE](./LICENSE)



