# Module mmkv-multiplatform-binding:core

This document summarizes the public API surface that you will most commonly use, distilled from the library interfaces. It only includes APIs defined in `core/src/commonMain/kotlin/top/kagg886/mkmb/MMKV.kt`, `core/src/androidMain/kotlin/top/kagg886/mkmb/MMKV.ext.kt` (Android), and `core/src/iosMain/kotlin/top/kagg886/mkmb/MMKV.ext.kt` (iOS).

## Initialization

- Initialize once before use:
```kotlin
MMKV.initialize("/your/storage/path")
```

- With options:
```kotlin
MMKV.initialize("/your/storage/path") {
    // Optional: replace C lib loader
    libLoader = MMKVOptions.MMKVCLibLoader { /* return loaded lib path */ "lib-path" }

    // Optional: logging
    logLevel = MMKVOptions.LogLevel.Info
    logFunc = { level, tag, msg -> println("[$tag]: $level - $msg") }
}
```

Notes:
- `MMKV.initialized` indicates whether initialization has happened.

## Getting Instances

- Default instance:
```kotlin
val kv = MMKV.defaultMMKV()
```

- Instance by ID:
```kotlin
val kv = MMKV.mmkvWithID("my-space")
```

- Optional parameters:
```kotlin
val kv = MMKV.defaultMMKV(
    mode = MMKVMode.SINGLE_PROCESS, // or MULTI_PROCESS, READ_ONLY
    cryptKey = null                  // non-null to enable encryption
)
```

`MMKVMode` values:
- `SINGLE_PROCESS`
- `MULTI_PROCESS`
- `READ_ONLY`

## Store and Retrieve Values

All `set(...)` APIs accept an optional `expire: Int = 0` in seconds (`0` means never expire). Matching `get(...)` APIs return the stored value.

- Int:
```kotlin
kv.set("kInt", 1)
val v: Int = kv.getInt("kInt")
```

- String:
```kotlin
kv.set("kStr", "hello")
val v: String = kv.getString("kStr")
```

- Boolean:
```kotlin
kv.set("kBool", true)
val v: Boolean = kv.getBoolean("kBool")
```

- Long / Float / Double:
```kotlin
kv.set("kLong", 1L)
kv.set("kFloat", 1.0f)
kv.set("kDouble", 1.0)
val a: Long = kv.getLong("kLong")
val b: Float = kv.getFloat("kFloat")
val c: Double = kv.getDouble("kDouble")
```

- ByteArray:
```kotlin
kv.set("kBytes", byteArrayOf(1, 2))
val v: ByteArray = kv.getByteArray("kBytes")
```

- List<String>:
```kotlin
kv.set("kList", listOf("a", "b"))
val v: List<String> = kv.getStringList("kList")
```

## Key/Space Operations

- Remove a key:
```kotlin
val removed: Boolean = kv.remove("k")
```

- Clear current instance data (does not delete files on disk):
```kotlin
kv.clear()
```

- Destroy the instance and delete files on disk:
```kotlin
val ok: Boolean = kv.destroy()
```

- Instance state & metadata:
```kotlin
val alive: Boolean = kv.isAlive()
val count: Int = kv.size()
val keys: List<String> = kv.allKeys()
val hasKey: Boolean = kv.exists("k")
```

## Configuration Types

- `MMKVOptions`:
  - `libLoader: MMKVOptions.MMKVCLibLoader`
  - `logLevel: MMKVOptions.LogLevel`
  - `logFunc: (LogLevel, String, String) -> Unit`

- `MMKVOptions.LogLevel` values: `Debug`, `Info`, `Warning`, `Error`, `None`

## Android-only Extensions (`MMKV.ext.kt` in Android source set)

- Store a `Parcelable`:
```kotlin
kv.set("user", myParcelable)
```

- Retrieve a `Parcelable`:
```kotlin
val user: MyParcelable? = kv.get("user")
```

- Import from `SharedPreferences` (returns number of migrated entries):
```kotlin
val migratedCount: Int = kv.importFromSharedPreferences(sharedPreferences)
```

## iOS-only Extensions (`MMKV.ext.kt` in iOS source set)

- Store an `NSObject` (NSCoding-capable object):
```kotlin
kv.set("obj", nsObject)
```

- Retrieve an NSCoding object using Objective-C class:
```kotlin
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ObjCClass

@OptIn(BetaInteropApi::class)
val value: Any? = kv.getNSCoding("obj", clazz = myObjCClass /* ObjCClass */)
```

---

This guide focuses strictly on the APIs exposed in `MMKV.kt` and the platform extension files for Android and iOS.
