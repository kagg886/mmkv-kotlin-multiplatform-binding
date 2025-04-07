# MMKV-Kotlin-Multiplatform-Binding

The English Version is [Here](./README.md)



## 简介

**MMKV-Kotlin-Multiplatform-Binding**(后文简称**MKMB**)是[MMKV](https://github.com/Tencent/MMKV)的Kotlin封装。



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
- [ ] IOS



## 安装引入(正在施工，发版后可以正常使用)

1. 引入`core`模块，这是一个 `Kotlin-MultiPlatform` 模块，提供访问各个操作系统的门面。

   ```kotlin
   dependencies {
       implementation("top.kagg886.mkmb:core:1.0.0")
   }
   ```

2. 如果是JVM平台，需要引入不同的动态库：

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
   //不同操作系统引入不同的二进制文件
   jvmMain.dependencies {
       implementation("top.kagg886.mkmb:platform-${hostTarget.name.lowercase()}:1.0.0")
   }
   ```

   



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

- Int
- String
- Boolean
- Long
- Float
- Double
- ByteArray
- List\<String\>

### 集合操作

- 根据Key移除Value
- 清空
- 获取大小
- 获取键集合
- 检查key是否存在

### 实例操作

- 销毁MMKV及其本地存储
- 检测MMKV实例是否被销毁





