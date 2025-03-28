# MMKV-Kotlin-Multiplatform-Binding

The English Version is [Here](./README.md)



## 简介

**MMKV-Kotlin-Multiplatform-Binding**(后文简称**MKMB**)是[MMKV](https://github.com/Tencent/MMKV)的Kotlin封装。



## 支持的平台

- [ ] Windows
  - [ ] JVM
  - [ ] Native
- [ ] Linux
  - [ ] JVM
  - [ ] Native
- [ ] macOS
  - [ ] JVM
  - [ ] Native
- [ ] Android(JVM)
- [ ] IOS



## 安装引入(正在施工，发版后可以正常使用)

建议您使用 `Gradle KTS` 完成配置：

```
dependencies {
    implementation("top.kagg886.mkmb:mkmb:1.0.0")
}
```

> 必须使用JDK22+



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



以下类型的存/取：

- Int
- String
- ByteArray
- List\<String\>
- Boolean
- Long
- Float
- Double

下列集合操作：

- 根据Key移除Value
- 清空
- 获取大小
- 获取键集合
- 检查key是否存在



