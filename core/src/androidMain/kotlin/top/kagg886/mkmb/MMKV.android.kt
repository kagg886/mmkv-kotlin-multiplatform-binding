package top.kagg886.mkmb

actual val MMKV.Companion.defaultLoader: MMKVOptions.MMKVCLibLoader by lazy {
    MMKVOptions.MMKVCLibLoader { "mmkvc" }
}

class JNIPointerMMKV(private val handle:Long) : MMKV {
    override fun set(key: String, value: Int) = NativeMMKV.mmkvc_setInt(handle, key, value)

    override fun set(key: String, value: String) {
        TODO("Not yet implemented")
    }

    override fun set(key: String, value: ByteArray) {
        TODO("Not yet implemented")
    }

    override fun set(key: String, value: List<String>) {
        TODO("Not yet implemented")
    }

    override fun set(key: String, value: Boolean) {
        TODO("Not yet implemented")
    }

    override fun set(key: String, value: Long) {
        TODO("Not yet implemented")
    }

    override fun set(key: String, value: Float) {
        TODO("Not yet implemented")
    }

    override fun set(key: String, value: Double) {
        TODO("Not yet implemented")
    }

    override fun getInt(key: String): Int = NativeMMKV.mmkvc_getInt(handle, key)

    override fun getString(key: String): String {
        TODO("Not yet implemented")
    }

    override fun getByteArray(key: String): ByteArray {
        TODO("Not yet implemented")
    }

    override fun getStringList(key: String): List<String> {
        TODO("Not yet implemented")
    }

    override fun getBoolean(key: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getLong(key: String): Long {
        TODO("Not yet implemented")
    }

    override fun getFloat(key: String): Float {
        TODO("Not yet implemented")
    }

    override fun getDouble(key: String): Double {
        TODO("Not yet implemented")
    }

    override fun remove(key: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

    override fun destroy(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isAlive(): Boolean {
        TODO("Not yet implemented")
    }

    override fun size(): Int {
        TODO("Not yet implemented")
    }

    override fun allKeys(): List<String> {
        TODO("Not yet implemented")
    }

    override fun exists(key: String): Boolean {
        TODO("Not yet implemented")
    }

}


actual fun MMKV.Companion.initialize(
    path: String,
    conf: MMKVOptions.() -> Unit
) {
    val options = MMKVOptions().apply(conf)
    NativeMMKV.initialize(options.libLoader.load())
    val data = MMKVInternalLog { level, tag, message ->
        options.logFunc.invoke(MMKVOptions.LogLevel.from(level), tag, message)
    }

    NativeMMKV.mmkvc_init(path, options.logLevel.ordinal, data)
}

actual fun MMKV.Companion.defaultMMKV(): MMKV = JNIPointerMMKV(NativeMMKV.mmkvc_defaultMMKV())

actual fun MMKV.Companion.mmkvWithID(id: String): MMKV =JNIPointerMMKV( NativeMMKV.mmkvc_mmkvWithID(id))
