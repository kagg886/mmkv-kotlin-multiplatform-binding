package top.kagg886.mkmb

import kotlinx.atomicfu.atomic

interface MMKV {
    companion object {
        var initialized: Boolean by atomic(false)
            internal set
    }

    fun set(key: String, value: Int)
    fun getInt(key: String): Int

    fun set(key: String, value: String)
    fun getString(key: String): String

    fun set(key: String, value: ByteArray)
    fun getByteArray(key: String): ByteArray

    fun set(key: String, value: List<String>)
    fun getStringList(key: String): List<String>

    fun set(key: String, value: Boolean)
    fun getBoolean(key: String): Boolean

    fun set(key: String, value: Long)
    fun getLong(key: String): Long

    fun set(key: String, value: Float)
    fun getFloat(key: String): Float

    fun set(key: String, value: Double)
    fun getDouble(key: String): Double

    fun remove(key: String): Boolean
    fun clear()
    fun destroy(): Boolean
    fun isAlive(): Boolean

    fun size(): Int
    fun allKeys(): List<String>
    fun exists(key: String): Boolean
}

class MMKVOptions {

    fun interface MMKVCLibLoader {
        fun load(): String
    }

    enum class LogLevel(val level: Int) {
        Debug(0),
        Info(1),
        Warning(2),
        Error(3),
        None(4);

        companion object {
            fun from(level: Int): LogLevel =
                entries.find { it.level == level } ?: throw IllegalArgumentException("Invalid log level: $level")
        }
    }

    var libLoader: MMKVCLibLoader = MMKV.defaultLoader
    var logLevel: LogLevel = LogLevel.Debug
    var logFunc: (LogLevel, String, String) -> Unit = { level, tag, it -> println("[$tag]: $level - $it") }
}

expect val MMKV.Companion.defaultLoader: MMKVOptions.MMKVCLibLoader
expect fun MMKV.Companion.initialize(path: String, conf: MMKVOptions.() -> Unit = {})
expect fun MMKV.Companion.defaultMMKV(): MMKV
expect fun MMKV.Companion.mmkvWithID(id: String): MMKV
