# Module mmkv-multiplatform-binding:ext

## Quick Start

This module provides Kotlin Multiplatform extensions for MMKV, including:
- Initialization helpers
- Typed `get`/`set` extensions
- Default instance helpers (`defaultMMKV`, `mmkvWithID`)
- Reactive `Flow` for key changes
- Platform-specific helpers (Android `Parcelable`, iOS `NSCoding`)

### 1) Add dependency

- Gradle (Kotlin DSL):
```kotlin
dependencies {
    implementation(project(":ext"))
}
```

### 2) Initialize MMKV

Initialize once on app startup. Use an app-writable directory.

- Common/JVM (tests use Okio for a temp dir):
```kotlin
import okio.FileSystem
import okio.Path.Companion.toPath
import top.kagg886.mkmb.MMKV
import top.kagg886.mkmb.MMKVOptions

val baseDir = "mmkv-sample".toPath().let { dir ->
    if (FileSystem.SYSTEM.exists(dir)) FileSystem.SYSTEM.deleteRecursively(dir)
    FileSystem.SYSTEM.createDirectory(dir)
    FileSystem.SYSTEM.canonicalize(dir.normalized()).toString()
}
MMKV.initialize(baseDir) {
    logLevel = MMKVOptions.LogLevel.Debug
}
```

- Android:
```kotlin
import androidx.test.platform.app.InstrumentationRegistry
import top.kagg886.mkmb.MMKV

val context = InstrumentationRegistry.getInstrumentation().targetContext
MMKV.initialize(context.cacheDir.absolutePath) {
    logFunc = { _, tag, msg -> println("$tag : $msg") }
}
```

- iOS:
```kotlin
import okio.FileSystem
import okio.Path.Companion.toPath
import top.kagg886.mkmb.MMKV
import top.kagg886.mkmb.MMKVOptions

val baseDir = "mmkv-ios".toPath().let { dir ->
    if (FileSystem.SYSTEM.exists(dir)) FileSystem.SYSTEM.deleteRecursively(dir)
    FileSystem.SYSTEM.createDirectory(dir)
    FileSystem.SYSTEM.canonicalize(dir.normalized()).toString()
}
MMKV.initialize(baseDir) {
    logLevel = MMKVOptions.LogLevel.Debug
}
```

### 3) Get an instance
```kotlin
import top.kagg886.mkmb.ext.defaultMMKV
import top.kagg886.mkmb.ext.mmkvWithID

val def = MMKV.defaultMMKV()
val scoped = MMKV.mmkvWithID("user-profile")
```

### 4) Read & Write values

Typed `get`/`set` are provided. If a default is supplied and key is missing, default is returned.

```kotlin
import top.kagg886.mkmb.ext.get
import top.kagg886.mkmb.ext.set

val kv = MMKV.mmkvWithID("example")

// primitives
kv.set("int", 2)
val i: Int = kv.get("int")
val iOr0: Int = kv.get("missing", 0)

kv.set("bool", true)
val b: Boolean = kv.get("bool")

kv.set("long", 2L)
val l: Long = kv.get("long")

kv.set("float", 2f)
val f: Float = kv.get("float")

kv.set("double", 2.0)
val d: Double = kv.get("double")

// String
kv.set("str", "hello")
val s: String = kv.get("str")
val sOrEmpty: String = kv.get("missing", "")

// ByteArray
kv.set("bytes", byteArrayOf(1,2,3))
val ba: ByteArray = kv.get("bytes")

// List<String>
kv.set("tags", listOf("k", "mmkv", "ext"))
val tags: List<String>? = kv.get("tags")
```

### 5) Observe changes with Flow

Collect a `Flow<T>` to react to updates on a key. Emissions occur when the value changes and can throw if the stored type mismatches `T`.

```kotlin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import top.kagg886.mkmb.ext.flow

val kv = MMKV.mmkvWithID("flow-demo")
runBlocking {
    val job = launch {
        kv.flow<String>("message").collect { latest ->
            println("latest: $latest")
        }
    }

    kv.set("message", "1")
    kv.set("message", "2")
    job.cancel()
}
```

Type mismatch example (will emit an exception):
```kotlin
val kv = MMKV.mmkvWithID("flow-error")
// collectors expect String
val flow = kv.flow<String>("k")
kv.set("k", "1")
kv.set("k", 5) // later read as String -> throws with a clear message
```

### 6) Android Parcelable (platform)

On Android you can store and retrieve `Parcelable` values.
```kotlin
@Parcelize
data class TestParcel(
    val firstName: String,
    val lastName: String,
    val age: Int,
    val isMarried: Boolean,
    val height: Float,
    val weight: Double,
) : Parcelable

val kv = MMKV.mmkvWithID("parcel")
kv.set("key", TestParcel("886","kagg",21,false,55.4f,123.456))
val back: TestParcel? = kv.get("key")
```

### 7) iOS NSCoding (platform)

On iOS you can store and retrieve `NSCoding` objects.
```kotlin
import platform.Foundation.NSCodingProtocol
import platform.Foundation.NSURL
import top.kagg886.mkmb.ext.getNSCoding

val kv = MMKV.mmkvWithID("nscoding")
val url = NSURL.URLWithString("https://google.com")
kv.set("data", url as NSCodingProtocol)
val back: NSURL? = kv.getNSCoding("data")
```

### Notes
- Always call `MMKV.initialize(baseDir)` once before use.
- `defaultMMKV()` returns a shared default instance; `mmkvWithID(id)` creates/opens a namespaced one.
- `flow<T>(key)` follows the declared generic type; mixing types for the same key will cause read errors.
