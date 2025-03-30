package top.kagg886.mkmb

import kotlinx.atomicfu.atomic

actual val MMKV.Companion.defaultLoader: MMKVOptions.MMKVCLibLoader by lazy {
    MMKVOptions.MMKVCLibLoader { "mmkvc" }
}

@OptIn(ExperimentalStdlibApi::class)
class JNIPointerMMKV(private val handle: Long) : MMKV {
    private var alive by atomic(true)

    override fun set(key: String, value: Int) {
        if (!alive) throw MMKVException("MMKV instance ${handle.toHexString()} was destroyed")
        NativeMMKV.mmkvc_setInt(handle, key, value)
    }

    override fun set(key: String, value: String) {
        if (!alive) throw MMKVException("MMKV instance ${handle.toHexString()} was destroyed")
        NativeMMKV.mmkvc_setString(handle, key, value)
    }

    override fun set(key: String, value: ByteArray) {
        if (!alive) throw MMKVException("MMKV instance ${handle.toHexString()} was destroyed")
        NativeMMKV.mmkvc_setByteArray(handle, key, value)
    }

    override fun set(key: String, value: List<String>) {
        if (!alive) throw MMKVException("MMKV instance ${handle.toHexString()} was destroyed")
        NativeMMKV.mmkvc_setStringList(handle, key, value)
    }

    override fun set(key: String, value: Boolean) {
        if (!alive) throw MMKVException("MMKV instance ${handle.toHexString()} was destroyed")
        NativeMMKV.mmkvc_setBoolean(handle, key, value)
    }

    override fun set(key: String, value: Long) {
        if (!alive) throw MMKVException("MMKV instance ${handle.toHexString()} was destroyed")
        NativeMMKV.mmkvc_setLong(handle, key, value)
    }


    override fun set(key: String, value: Float) {
        if (!alive) throw MMKVException("MMKV instance ${handle.toHexString()} was destroyed")
        NativeMMKV.mmkvc_setFloat(handle, key, value)
    }

    override fun set(key: String, value: Double) {
        if (!alive) throw MMKVException("MMKV instance ${handle.toHexString()} was destroyed")
        NativeMMKV.mmkvc_setDouble(handle, key, value)
    }

    override fun getInt(key: String): Int {
        if (!alive) throw MMKVException("MMKV instance ${handle.toHexString()} was destroyed")
        return NativeMMKV.mmkvc_getInt(handle, key)
    }

    override fun getString(key: String): String {
        if (!alive) throw MMKVException("MMKV instance ${handle.toHexString()} was destroyed")
        return NativeMMKV.mmkvc_getString(handle, key)
    }

    override fun getByteArray(key: String): ByteArray {
        if (!alive) throw MMKVException("MMKV instance ${handle.toHexString()} was destroyed")
        return NativeMMKV.mmkvc_getByteArray(handle, key)
    }

    override fun getStringList(key: String): List<String> {
        if (!alive) throw MMKVException("MMKV instance ${handle.toHexString()} was destroyed")
        return NativeMMKV.mmkvc_getStringList(handle, key)
    }

    override fun getBoolean(key: String): Boolean {
        if (!alive) throw MMKVException("MMKV instance ${handle.toHexString()} was destroyed")
        return NativeMMKV.mmkvc_getBoolean(handle, key)
    }

    override fun getLong(key: String): Long {
        if (!alive) throw MMKVException("MMKV instance ${handle.toHexString()} was destroyed")
        return NativeMMKV.mmkvc_getLong(handle, key)
    }

    override fun getFloat(key: String): Float {
        if (!alive) throw MMKVException("MMKV instance ${handle.toHexString()} was destroyed")
        return NativeMMKV.mmkvc_getFloat(handle, key)
    }

    override fun getDouble(key: String): Double {
        if (!alive) throw MMKVException("MMKV instance ${handle.toHexString()} was destroyed")
        return NativeMMKV.mmkvc_getDouble(handle, key)
    }

    override fun remove(key: String): Boolean {
        if (!alive) throw MMKVException("MMKV instance ${handle.toHexString()} was destroyed")
        return NativeMMKV.mmkvc_remove(handle, key)
    }

    override fun clear() {
        if (!alive) throw MMKVException("MMKV instance ${handle.toHexString()} was destroyed")
        NativeMMKV.mmkvc_clear(handle)
    }

    override fun destroy(): Boolean {
        if (!alive) throw MMKVException("MMKV instance ${handle.toHexString()} was destroyed")
        alive = false
        return NativeMMKV.mmkvc_destroy(handle)
    }

    override fun isAlive(): Boolean {
        alive = NativeMMKV.mmkvc_isAlive(handle)
        return alive
    }

    override fun size(): Int {
        if (!alive) throw MMKVException("MMKV instance ${handle.toHexString()} was destroyed")
        return NativeMMKV.mmkvc_size(handle)
    }

    override fun allKeys(): List<String> {
        if (!alive) throw MMKVException("MMKV instance ${handle.toHexString()} was destroyed")
        return NativeMMKV.mmkvc_allKeys(handle)
    }

    override fun exists(key: String): Boolean {
        if (!alive) throw MMKVException("MMKV instance ${handle.toHexString()} was destroyed")
        return NativeMMKV.mmkvc_exists(handle, key)
    }

}


actual fun MMKV.Companion.initialize(
    path: String,
    options: MMKVOptions
) {
    NativeMMKV.initialize(options.libLoader.load())
    val data = MMKVInternalLog { level, tag, message ->
        options.logFunc.invoke(MMKVOptions.LogLevel.from(level), tag, message)
    }

    NativeMMKV.mmkvc_init(path, options.logLevel.ordinal, data)
}

actual fun MMKV.Companion.defaultMMKV(): MMKV = JNIPointerMMKV(NativeMMKV.mmkvc_defaultMMKV())

actual fun MMKV.Companion.mmkvWithID(id: String): MMKV =
    JNIPointerMMKV(NativeMMKV.mmkvc_mmkvWithID(id))
