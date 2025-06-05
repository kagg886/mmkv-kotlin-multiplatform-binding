package top.kagg886.mkmb

import kotlinx.atomicfu.atomic
import platform.darwin.NSObject

class AppleMMKV(internal val handle: NSObject): MMKV {
    private var alive by atomic(true)

    override fun set(key: String, value: Int) {
        if (!alive) throw MMKVException("MMKV instance $handle was destroyed")
        NativeMMKVImpl.setInt(handle, key, value)
    }

    override fun set(key: String, value: String) {
        if (!alive) throw MMKVException("MMKV instance $handle was destroyed")
        NativeMMKVImpl.setString(handle, key, value)
    }

    override fun set(key: String, value: ByteArray) {
        if (!alive) throw MMKVException("MMKV instance $handle was destroyed")
        NativeMMKVImpl.setData(handle, key, value)
    }

    override fun set(key: String, value: List<String>) {
        if (!alive) throw MMKVException("MMKV instance $handle was destroyed")
        NativeMMKVImpl.setStringList(handle, key, value)
    }

    override fun set(key: String, value: Boolean) {
        if (!alive) throw MMKVException("MMKV instance $handle was destroyed")
        NativeMMKVImpl.setBool(handle, key, value)
    }

    override fun set(key: String, value: Long) {
        if (!alive) throw MMKVException("MMKV instance $handle was destroyed")
        NativeMMKVImpl.setLong(handle, key, value)
    }

    override fun set(key: String, value: Float) {
        if (!alive) throw MMKVException("MMKV instance $handle was destroyed")
        NativeMMKVImpl.setFloat(handle, key, value)
    }

    override fun set(key: String, value: Double) {
        if (!alive) throw MMKVException("MMKV instance $handle was destroyed")
        NativeMMKVImpl.setDouble(handle, key, value)
    }

    override fun getInt(key: String): Int {
        if (!alive) throw MMKVException("MMKV instance $handle was destroyed")
        return NativeMMKVImpl.getInt(handle, key)
    }

    override fun getString(key: String): String {
        if (!alive) throw MMKVException("MMKV instance $handle was destroyed")
        return NativeMMKVImpl.getString(handle, key)
    }

    override fun getByteArray(key: String): ByteArray {
        if (!alive) throw MMKVException("MMKV instance $handle was destroyed")
        return NativeMMKVImpl.getData(handle, key)
    }

    override fun getStringList(key: String): List<String> {
        if (!alive) throw MMKVException("MMKV instance $handle was destroyed")
        return NativeMMKVImpl.getStringList(handle, key)
    }

    override fun getBoolean(key: String): Boolean {
        if (!alive) throw MMKVException("MMKV instance $handle was destroyed")
        return NativeMMKVImpl.getBool(handle, key)
    }

    override fun getLong(key: String): Long {
        if (!alive) throw MMKVException("MMKV instance $handle was destroyed")
        return NativeMMKVImpl.getLong(handle, key)
    }

    override fun getFloat(key: String): Float {
        if (!alive) throw MMKVException("MMKV instance $handle was destroyed")
        return NativeMMKVImpl.getFloat(handle, key)
    }

    override fun getDouble(key: String): Double {
        if (!alive) throw MMKVException("MMKV instance $handle was destroyed")
        return NativeMMKVImpl.getDouble(handle, key)
    }

    override fun remove(key: String): Boolean {
        if (!alive) throw MMKVException("MMKV instance $handle was destroyed")
        return NativeMMKVImpl.remove(handle, key)
    }

    override fun clear() {
        if (!alive) throw MMKVException("MMKV instance $handle was destroyed")
        return NativeMMKVImpl.clear(handle)
    }

    override fun destroy(): Boolean {
        if (!alive) throw MMKVException("MMKV instance $handle was destroyed")
        val bool =  NativeMMKVImpl.destroy(handle)
        if (bool) {
            alive = false
        }
        return bool
    }

    override fun isAlive(): Boolean {
        alive = NativeMMKVImpl.isAlive(handle)
        return alive
    }

    override fun size(): Int {
        if (!alive) throw MMKVException("MMKV instance $handle was destroyed")
        return NativeMMKVImpl.size(handle)
    }

    override fun allKeys(): List<String> {
        if (!alive) throw MMKVException("MMKV instance $handle was destroyed")
        return NativeMMKVImpl.allKeys(handle)
    }

    override fun exists(key: String): Boolean {
        if (!alive) throw MMKVException("MMKV instance $handle was destroyed")
        return NativeMMKVImpl.exists(handle, key)
    }

}

actual fun MMKV.Companion.defaultMMKV(): MMKV {
    return AppleMMKV(NativeMMKVImpl.defaultMMKV())
}

actual fun MMKV.Companion.mmkvWithID(id: String): MMKV {
    return AppleMMKV(NativeMMKVImpl.mmkvWithID(id))
}

actual fun MMKV.Companion.initialize(path: String, options: MMKVOptions) {
    NativeMMKVImpl.initialize(path, options.logLevel.ordinal.toULong()) { level, tag, it ->
        options.logFunc(MMKVOptions.LogLevel.from(level.toInt()), tag, it)
    }
}

actual val MMKV.Companion.defaultLoader: MMKVOptions.MMKVCLibLoader by lazy {
    MMKVOptions.MMKVCLibLoader {
        ""
    }
}
