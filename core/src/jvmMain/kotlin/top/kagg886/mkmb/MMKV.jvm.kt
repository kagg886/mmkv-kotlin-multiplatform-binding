package top.kagg886.mkmb

import kotlinx.atomicfu.atomic
import top.kagg886.mkmb.JvmTarget.*
import java.io.File
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.SymbolLookup

internal class PanamaMMKV(private val ptr: MemorySegment) : MMKV {
    private var alive by atomic(true)
    override fun set(key: String, value: Int) {
        if (!alive) throw MMKVException("MMKV instance $ptr was destroyed")
        NativeMMKV.mmkvc_setInt(ptr, key, value)
    }

    override fun set(key: String, value: String) {
        if (!alive) throw MMKVException("MMKV instance $ptr was destroyed")
        NativeMMKV.mmkvc_setString(ptr, key, value)
    }

    override fun set(key: String, value: ByteArray) {
        if (!alive) throw MMKVException("MMKV instance $ptr was destroyed")
        NativeMMKV.mmkvc_setByteArray(ptr, key, value)
    }

    override fun set(key: String, value: List<String>) {
        if (!alive) throw MMKVException("MMKV instance $ptr was destroyed")
        NativeMMKV.mmkvc_setStringList(ptr, key, value)
    }

    override fun set(key: String, value: Boolean) {
        if (!alive) throw MMKVException("MMKV instance $ptr was destroyed")
        NativeMMKV.mmkvc_setBoolean(ptr, key, value)
    }

    override fun set(key: String, value: Long) {
        if (!alive) throw MMKVException("MMKV instance $ptr was destroyed")
        NativeMMKV.mmkvc_setLong(ptr, key, value)
    }

    override fun set(key: String, value: Float) {
        if (!alive) throw MMKVException("MMKV instance $ptr was destroyed")
        NativeMMKV.mmkvc_setFloat(ptr, key, value)
    }

    override fun set(key: String, value: Double) {
        if (!alive) throw MMKVException("MMKV instance $ptr was destroyed")
        NativeMMKV.mmkvc_setDouble(ptr, key, value)
    }

    override fun getInt(key: String): Int {
        if (!alive) throw MMKVException("MMKV instance $ptr was destroyed")
        return NativeMMKV.mmkvc_getInt(ptr, key)
    }

    override fun getString(key: String): String {
        if (!alive) throw MMKVException("MMKV instance $ptr was destroyed")
        return NativeMMKV.mmkvc_getString(ptr, key)
    }

    override fun getByteArray(key: String): ByteArray {
        if (!alive) throw MMKVException("MMKV instance $ptr was destroyed")
        return NativeMMKV.mmkvc_getByteArray(ptr, key)
    }

    override fun getStringList(key: String): List<String> {
        if (!alive) throw MMKVException("MMKV instance $ptr was destroyed")
        return NativeMMKV.mmkvc_getStringList(ptr, key)
    }

    override fun getBoolean(key: String): Boolean {
        if (!alive) throw MMKVException("MMKV instance $ptr was destroyed")
        return NativeMMKV.mmkvc_getBoolean(ptr, key)
    }

    override fun getLong(key: String): Long {
        if (!alive) throw MMKVException("MMKV instance $ptr was destroyed")
        return NativeMMKV.mmkvc_getLong(ptr, key)
    }

    override fun getFloat(key: String): Float {
        if (!alive) throw MMKVException("MMKV instance $ptr was destroyed")
        return NativeMMKV.mmkvc_getFloat(ptr, key)
    }

    override fun getDouble(key: String): Double {
        if (!alive) throw MMKVException("MMKV instance $ptr was destroyed")
        return NativeMMKV.mmkvc_getDouble(ptr, key)
    }

    override fun remove(key: String): Boolean {
        if (!alive) throw MMKVException("MMKV instance $ptr was destroyed")
        return NativeMMKV.mmkvc_remove(ptr, key)
    }

    override fun clear() {
        if (!alive) throw MMKVException("MMKV instance $ptr was destroyed")
        NativeMMKV.mmkvc_clear(ptr)
    }

    override fun destroy(): Boolean {
        if (!alive) return false
        return alive.apply { alive = !NativeMMKV.mmkvc_destroy(ptr) }
    }

    override fun isAlive(): Boolean {
        if (!alive) return false
        return alive.apply { alive = NativeMMKV.mmkvc_isAlive(ptr) }
    }

    override fun size(): Int {
        if (!alive) throw MMKVException("MMKV instance $ptr was destroyed")
        return NativeMMKV.mmkvc_size(ptr)
    }

    override fun allKeys(): List<String> {
        if (!alive) throw MMKVException("MMKV instance $ptr was destroyed")
        return NativeMMKV.mmkvc_allKeys(ptr)
    }

    override fun exists(key: String): Boolean {
        if (!alive) throw MMKVException("MMKV instance $ptr was destroyed")
        return NativeMMKV.mmkvc_exists(ptr, key)
    }


}


actual fun MMKV.Companion.initialize(path: String, options: MMKVOptions) {
    NativeMMKV.global = Arena.ofConfined()
    NativeMMKV.dll = SymbolLookup.libraryLookup(options.libLoader.load(), NativeMMKV.global)
    NativeMMKV.mmkvc_init(path, options.logLevel.level) { level, tag, it ->
        options.logFunc(MMKVOptions.LogLevel.from(level), tag, it)
    }
    initialized = true
}

actual fun MMKV.Companion.defaultMMKV(): MMKV {
    return NativeMMKV.mmkvc_defaultMMKV()
}

actual fun MMKV.Companion.mmkvWithID(id: String): MMKV {
    return NativeMMKV.mmkvc_mmkvWithID(id)
}

actual val MMKV.Companion.defaultLoader: MMKVOptions.MMKVCLibLoader by lazy {
    MMKVOptions.MMKVCLibLoader {
        val name = when (jvmTarget) {
            MACOS, LINUX -> "libmmkvc"
            WINDOWS -> "mmkvc"
        }
        val ext = when (jvmTarget) {
            MACOS -> "dylib"
            LINUX -> "so"
            WINDOWS -> "dll"
        }
        val tmp = File(System.getProperty("user.home")).resolve(".cache").resolve("$name.$ext")

        if (tmp.exists()) {
            val sha256 = tmp.sha256()
            val resourceSha256 = MMKV::class.java.getResourceAsStream("/build-${jvmTarget.name.lowercase()}.hash")!!.readAllBytes().decodeToString()
            if (sha256 != resourceSha256) {
                tmp.parentFile!!.mkdirs()
                val stream = MMKV::class.java.getResourceAsStream("/$name.$ext")!!
                tmp.createNewFile()
                stream.use {
                    tmp.writeBytes(it.readAllBytes())
                }
            }
        } else {
            tmp.parentFile!!.mkdirs()
            val stream = MMKV::class.java.getResourceAsStream("/$name.$ext")!!
            tmp.createNewFile()
            stream.use {
                tmp.writeBytes(it.readAllBytes())
            }
        }

        tmp.absolutePath
    }
}

