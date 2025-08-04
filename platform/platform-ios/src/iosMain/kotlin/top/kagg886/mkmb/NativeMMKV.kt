package top.kagg886.mkmb

import kotlinx.cinterop.*
import mmkv.MMKV
import mmkv.MMKVExpireNever
import mmkv.MMKVHandlerProtocol
import mmkv.MMKVLogLevel
import mmkv.MMKVMode
import platform.Foundation.NSMutableArray
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
object NativeMMKVImpl {
    fun defaultMMKV(mode: MMKVMode, cryptKey: String?): NSObject = mmkvWithID("mmkv.default", mode, cryptKey)

    fun mmkvWithID(id: String, mode: MMKVMode, cryptKey: String?): NSObject {
        val mmkv =  cryptKey?.encodeToByteArray()?.useAsNSData {
            MMKV.mmkvWithID(mmapID = id, cryptKey = this, mode = mode)!!
        } ?: MMKV.mmkvWithID(mmapID = id, cryptKey = null, mode = mode)!!

        mmkv.enableAutoKeyExpire(MMKVExpireNever)

        return mmkv
    }

    fun initialize(path: String, level: ULong, log: (ULong, String, String) -> Unit) {
        MMKV.initializeMMKV(rootDir = path, logLevel = level, handler = MMKVHandler(log))
    }

    fun setInt(handle: NSObject, key: String, value: Int, expire: Int) {
        val mmkv = handle as MMKV
        mmkv.setInt32(value, key, expire.toUInt())
    }

    fun setString(handle: NSObject, key: String, value: String, expire: Int) {
        val mmkv = handle as MMKV
        mmkv.setString(value, key, expire.toUInt())
    }

    fun setData(handle: NSObject, key: String, value: ByteArray, expire: Int) {
        val mmkv = handle as MMKV

        value.useAsNSData {
            mmkv.setData(this, key, expire.toUInt())
        }
    }

    fun setStringList(handle: NSObject, key: String, value: List<String>, expire: Int) {
        val mmkv = handle as MMKV
        val arr = NSMutableArray()
        for (i in value) {
            arr.addObject(i)
        }
        mmkv.setObject(arr, key, expire.toUInt())
    }

    fun setBool(handle: NSObject, key: String, value: Boolean, expire: Int) {
        val mmkv = handle as MMKV
        mmkv.setBool(value, key, expire.toUInt())
    }

    fun setLong(handle: NSObject, key: String, value: Long, expire: Int) {
        val mmkv = handle as MMKV
        mmkv.setInt64(value, key, expire.toUInt())
    }

    fun setFloat(handle: NSObject, key: String, value: Float, expire: Int) {
        val mmkv = handle as MMKV
        mmkv.setFloat(value, key, expire.toUInt())
    }

    fun setDouble(handle: NSObject, key: String, value: Double, expire: Int) {
        val mmkv = handle as MMKV
        mmkv.setDouble(value, key, expire.toUInt())
    }

    fun getInt(handle: NSObject, key: String): Int {
        val mmkv = handle as MMKV
        return mmkv.getInt32ForKey(key)
    }

    fun getString(handle: NSObject, key: String): String {
        val mmkv = handle as MMKV
        return mmkv.getStringForKey(key) ?: ""
    }

    fun getData(handle: NSObject, key: String): ByteArray {
        val mmkv = handle as MMKV
        val data = mmkv.getDataForKey(key) ?: return byteArrayOf()
        return data.bytes()!!.readBytes(data.length.toInt())
    }

    @OptIn(BetaInteropApi::class)
    fun getStringList(handle: NSObject, key: String): List<String> {
        val mmkv = handle as MMKV

        val arr = mmkv.getObjectOfClass(NSMutableArray.`class`()!!, key) as NSMutableArray?
        if (arr == null) {
            return emptyList()
        }

        val list = mutableListOf<String>()

        for (i in 0.toULong()..<arr.count()) {
            val data = arr.objectAtIndex(i) as String
            list.add(data)
        }
        return list
    }

    fun getBool(handle: NSObject, key: String): Boolean {
        val mmkv = handle as MMKV
        return mmkv.getBoolForKey(key)
    }

    fun getLong(handle: NSObject, key: String): Long {
        val mmkv = handle as MMKV
        return mmkv.getInt64ForKey(key)
    }

    fun getFloat(handle: NSObject, key: String): Float {
        val mmkv = handle as MMKV
        return mmkv.getFloatForKey(key)
    }

    fun getDouble(handle: NSObject, key: String): Double {
        val mmkv = handle as MMKV
        return mmkv.getDoubleForKey(key)
    }

    @OptIn(BetaInteropApi::class)
    fun getNSCoding(handle: NSObject, key: String, clazz: ObjCClass): Any? {
        val mmkv = handle as MMKV
        return mmkv.getObjectOfClass(clazz, key)
    }

    fun setNSCoding(handle: NSObject, key: String, value: NSObject?, expire: Int) {
        val mmkv = handle as MMKV

        mmkv.setObject(value, key, expire.toUInt())
    }

    fun remove(handle: NSObject, key: String): Boolean {
        val mmkv = handle as MMKV
        if (!mmkv.containsKey(key)) {
            return false
        }
        mmkv.removeValueForKey(key)
        return true
    }

    fun clear(handle: NSObject) {
        val mmkv = handle as MMKV
        mmkv.clearAll()
    }

    fun destroy(handle: NSObject): Boolean {
        val mmkv = handle as MMKV
        return MMKV.removeStorage(mmkv.mmapID(), MMKV.mmkvBasePath())
    }

    fun isAlive(handle: NSObject): Boolean {
        val mmkv = handle as MMKV
        return MMKV.checkExist(mmkv.mmapID(), MMKV.mmkvBasePath())
    }

    fun size(handle: NSObject): Int {
        val mmkv = handle as MMKV
        return mmkv.count().toInt()
    }

    fun allKeys(handle: NSObject): List<String> {
        val mmkv = handle as MMKV
        val list = mutableListOf<String>()
        mmkv.enumerateKeys { s, _ ->
            list.add(s!!)
        }
        return list
    }

    fun exists(handle: NSObject, key: String): Boolean {
        val mmkv = handle as MMKV
        return mmkv.containsKey(key)
    }
}


@OptIn(ExperimentalForeignApi::class)
private class MMKVHandler(private val log: (ULong, String, String) -> Unit) : NSObject(), MMKVHandlerProtocol {
    override fun mmkvLogWithLevel(
        level: MMKVLogLevel,
        file: CPointer<ByteVar>?,
        line: Int,
        func: CPointer<ByteVar>?,
        message: String?
    ) {
        log(level, file?.toKString() ?: "", message ?: "")
    }
}
