package top.kagg886.mkmb

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCClass
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.toKString
import mmkv.MMKV
import mmkv.MMKVHandlerProtocol
import mmkv.MMKVLogLevel
import platform.Foundation.NSMutableArray
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
object NativeMMKVImpl {
    fun defaultMMKV(): NSObject {
        return MMKV.defaultMMKV()!!
    }

    fun mmkvWithID(id: String): NSObject {
        return MMKV.mmkvWithID(id)!!
    }

    fun initialize(path: String, level: ULong, log: (ULong, String, String) -> Unit) {
        MMKV.initializeMMKV(rootDir = path, logLevel = level, handler = MMKVHandler(log))
    }

    fun setInt(handle: NSObject, key: String, value: Int) {
        val mmkv = handle as MMKV
        mmkv.setInt32(value,key)
    }

    fun setString(handle: NSObject, key: String, value: String) {
        val mmkv = handle as MMKV
        mmkv.setString(value,key)
    }

    fun setData(handle: NSObject, key: String, value: ByteArray) {
        val mmkv = handle as MMKV

        value.useAsNSData {
            mmkv.setData(this, key)
        }
    }

    fun setStringList(handle: NSObject, key: String, value: List<String>) {
        val mmkv = handle as MMKV
        val arr = NSMutableArray()
        for (i in value) {
            arr.addObject(i)
        }
        mmkv.setObject(arr,key)
    }

    fun setBool(handle: NSObject, key: String, value: Boolean) {
        val mmkv = handle as MMKV
        mmkv.setBool(value, key)
    }

    fun setLong(handle: NSObject, key: String, value: Long) {
        val mmkv = handle as MMKV
        mmkv.setInt64(value, key)
    }

    fun setFloat(handle: NSObject, key: String, value: Float) {
        val mmkv = handle as MMKV
        mmkv.setFloat(value, key)
    }

    fun setDouble(handle: NSObject, key: String, value: Double) {
        val mmkv = handle as MMKV
        mmkv.setDouble(value, key)
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

        val arr = mmkv.getObjectOfClass(NSMutableArray.`class`()!!,key) as NSMutableArray?
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
        return mmkv.getObjectOfClass(clazz,key)
    }

    fun setNSCoding(handle: NSObject,key: String,value: NSObject?) {
        val mmkv = handle as MMKV

        mmkv.setObject(value,key)
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
        return MMKV.removeStorage(mmkv.mmapID(),MMKV.mmkvBasePath())
    }

    fun isAlive(handle: NSObject): Boolean {
        val mmkv = handle as MMKV
        return MMKV.checkExist(mmkv.mmapID(),MMKV.mmkvBasePath())
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
