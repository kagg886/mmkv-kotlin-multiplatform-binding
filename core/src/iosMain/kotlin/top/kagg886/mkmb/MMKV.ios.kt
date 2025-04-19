package top.kagg886.mkmb

import platform.darwin.NSObject

class AppleMMKV(private val handle: NSObject): MMKV {
    override fun set(key: String, value: Int) {
        NativeMMKVImpl.setInt(handle, key, value)
    }

    override fun set(key: String, value: String) {
        NativeMMKVImpl.setString(handle, key, value)
    }

    override fun set(key: String, value: ByteArray) {
        NativeMMKVImpl.setData(handle, key, value)
    }

    override fun set(key: String, value: List<String>) {
        NativeMMKVImpl.setStringList(handle, key, value)
    }

    override fun set(key: String, value: Boolean) {
        NativeMMKVImpl.setBool(handle, key, value)
    }

    override fun set(key: String, value: Long) {
        NativeMMKVImpl.setLong(handle, key, value)
    }

    override fun set(key: String, value: Float) {
        NativeMMKVImpl.setFloat(handle, key, value)
    }

    override fun set(key: String, value: Double) {
        NativeMMKVImpl.setDouble(handle, key, value)
    }

    override fun getInt(key: String): Int {
        return NativeMMKVImpl.getInt(handle, key)
    }

    override fun getString(key: String): String {
        return NativeMMKVImpl.getString(handle, key)
    }

    override fun getByteArray(key: String): ByteArray {
        return NativeMMKVImpl.getData(handle, key)
    }

    override fun getStringList(key: String): List<String> {
        return NativeMMKVImpl.getStringList(handle, key)
    }

    override fun getBoolean(key: String): Boolean {
        return NativeMMKVImpl.getBool(handle, key)
    }

    override fun getLong(key: String): Long {
        return NativeMMKVImpl.getLong(handle, key)
    }

    override fun getFloat(key: String): Float {
        return NativeMMKVImpl.getFloat(handle, key)
    }

    override fun getDouble(key: String): Double {
        return NativeMMKVImpl.getDouble(handle, key)
    }

    override fun remove(key: String): Boolean {
        return NativeMMKVImpl.remove(handle, key)
    }

    override fun clear() {
        return NativeMMKVImpl.clear(handle)
    }

    override fun destroy(): Boolean {
        return NativeMMKVImpl.destroy(handle)
    }

    override fun isAlive(): Boolean {
        return NativeMMKVImpl.isAlive(handle)
    }

    override fun size(): Int {
        return NativeMMKVImpl.size(handle)
    }

    override fun allKeys(): List<String> {
        return NativeMMKVImpl.allKeys(handle)
    }

    override fun exists(key: String): Boolean {
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
