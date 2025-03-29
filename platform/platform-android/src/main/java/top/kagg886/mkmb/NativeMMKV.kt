package top.kagg886.mkmb

fun interface MMKVInternalLog {
    fun invoke(level: Int, tag: String, message: String)
}


object NativeMMKV {
    fun initialize(path: String) = System.loadLibrary(path)

    @JvmStatic
    external fun mmkvc_init(path: String, level: Int, callback: MMKVInternalLog)

    @JvmStatic
    external fun mmkvc_defaultMMKV(): Long

    @JvmStatic
    external fun mmkvc_mmkvWithID(id: String): Long


    @JvmStatic
    external fun mmkvc_getInt(handle: Long, key: String): Int

    @JvmStatic
    external fun mmkvc_setInt(handle: Long, key: String, value: Int)
    
    @JvmStatic
    external fun mmkvc_setString(handle: Long, key: String, value: String)

    @JvmStatic
    external fun mmkvc_setByteArray(handle: Long, key: String, value: ByteArray)

    @JvmStatic
    external fun mmkvc_setStringList(handle: Long, key: String, value: List<String>) 

    @JvmStatic
    external fun mmkvc_setBoolean(handle: Long, key: String, value: Boolean)

    @JvmStatic
    external fun mmkvc_setLong(handle: Long, key: String, value: Long)

    @JvmStatic
    external fun mmkvc_setFloat(handle: Long, key: String, value: Float)

    @JvmStatic
    external fun mmkvc_setDouble(handle: Long, key: String, value: Double)


    @JvmStatic
    external fun mmkvc_getString(handle: Long, key: String): String

    @JvmStatic
    external fun mmkvc_getByteArray(handle: Long, key: String): ByteArray

    @JvmStatic
    external fun mmkvc_getStringList(handle: Long, key: String): List<String>

    @JvmStatic
    external fun mmkvc_getBoolean(handle: Long, key: String): Boolean

    @JvmStatic
    external fun mmkvc_getLong(handle: Long, key: String): Long

    @JvmStatic
    external fun mmkvc_getFloat(handle: Long, key: String): Float

    @JvmStatic
    external fun mmkvc_getDouble(handle: Long, key: String): Double

    @JvmStatic
    external fun mmkvc_remove(handle: Long, key: String): Boolean

    @JvmStatic
    external fun mmkvc_clear(handle: Long)

    @JvmStatic
    external fun mmkvc_destroy(handle: Long): Boolean


    @JvmStatic
    external fun mmkvc_isAlive(handle: Long): Boolean

    @JvmStatic
    external fun mmkvc_size(handle: Long): Int

    @JvmStatic
    external fun mmkvc_allKeys(handle: Long): List<String>

    @JvmStatic
    external fun mmkvc_exists(handle: Long, key: String): Boolean


}