package top.kagg886.mkmb

import platform.darwin.NSObject

interface NativeMMKV {
    fun defaultMMKV(): NSObject
    fun mmkvWithID(id: String): NSObject
    fun initialize(path: String, level: ULong, log: (ULong, String, String) -> Unit)

    fun setInt(handle: NSObject, key: String, value: Int)
    fun setString(handle: NSObject, key: String, value: String)
    fun setData(handle: NSObject, key: String, value: ByteArray)
    fun setStringList(handle: NSObject, key: String, value: List<String>)
    fun setBool(handle: NSObject, key: String, value: Boolean)
    fun setLong(handle: NSObject, key: String, value: Long)
    fun setFloat(handle: NSObject, key: String, value: Float)
    fun setDouble(handle: NSObject, key: String, value: Double)

    fun getInt(handle: NSObject, key: String): Int
    fun getString(handle: NSObject, key: String): String
    fun getData(handle: NSObject, key: String): ByteArray
    fun getStringList(handle: NSObject, key: String): List<String>
    fun getBool(handle: NSObject, key: String): Boolean
    fun getLong(handle: NSObject, key: String): Long
    fun getFloat(handle: NSObject, key: String): Float
    fun getDouble(handle: NSObject, key: String): Double

    fun remove(handle: NSObject, key: String): Boolean
    fun clear(handle: NSObject)
    fun destroy(handle: NSObject): Boolean
    fun isAlive(handle: NSObject): Boolean
    fun size(handle: NSObject): Int
    fun allKeys(handle: NSObject): List<String>
    fun exists(handle: NSObject, key: String): Boolean

}

expect val NativeMMKVImpl : NativeMMKV
