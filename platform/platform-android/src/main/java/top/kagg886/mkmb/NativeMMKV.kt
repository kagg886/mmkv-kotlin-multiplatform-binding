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
    external fun mmkvc_setInt(handle: Long, key: String, value: Int)

    @JvmStatic
    external fun mmkvc_getInt(handle: Long, key: String): Int
}