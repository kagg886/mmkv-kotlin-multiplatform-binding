package top.kagg886.mkmb

import kotlinx.cinterop.*
import mmkv.MMKV
import mmkv.MMKVHandlerProtocol
import mmkv.MMKVLogLevel
import platform.Foundation.NSMutableArray
import platform.darwin.NSObject


@OptIn(ExperimentalForeignApi::class)
actual object NativeMMKVImpl : NativeMMKV {
    override fun defaultMMKV(): NSObject {
        return MMKV.defaultMMKV()!!
    }

    override fun mmkvWithID(id: String): NSObject {
        return MMKV.mmkvWithID(id)!!
    }

    override fun initialize(path: String, level: ULong, log: (ULong, String, String) -> Unit) {
        MMKV.initializeMMKV(rootDir = path, logLevel = level, handler = MMKVHandler(log))
    }

    override fun setInt(handle: NSObject, key: String, value: Int) {
        val mmkv = handle as MMKV
        mmkv.setInt32(value,key)
    }

    override fun setString(handle: NSObject, key: String, value: String) {
        val mmkv = handle as MMKV
        mmkv.setString(value,key)
    }

    override fun setData(handle: NSObject, key: String, value: ByteArray) {
        val mmkv = handle as MMKV

        value.useAsNSData {
            mmkv.setData(this, key)
        }
    }

    override fun setStringList(handle: NSObject, key: String, value: List<String>) {
        val mmkv = handle as MMKV
        val arr = NSMutableArray()
        for (i in value) {
            arr.addObject(i)
        }
        mmkv.setObject(arr,key)
    }

    override fun setBool(handle: NSObject, key: String, value: Boolean) {
        val mmkv = handle as MMKV
        mmkv.setBool(value, key)
    }

    override fun setLong(handle: NSObject, key: String, value: Long) {
        val mmkv = handle as MMKV
        mmkv.setInt64(value, key)
    }

    override fun setFloat(handle: NSObject, key: String, value: Float) {
        val mmkv = handle as MMKV
        mmkv.setFloat(value, key)
    }

    override fun setDouble(handle: NSObject, key: String, value: Double) {
        val mmkv = handle as MMKV
        mmkv.setDouble(value, key)
    }

    override fun getInt(handle: NSObject, key: String): Int {
        val mmkv = handle as MMKV
        return mmkv.getInt32ForKey(key)
    }

    override fun getString(handle: NSObject, key: String): String {
        val mmkv = handle as MMKV
        return mmkv.getStringForKey(key) ?: ""
    }

    override fun getData(handle: NSObject, key: String): ByteArray {
        val mmkv = handle as MMKV
        val data = mmkv.getDataForKey(key) ?: return byteArrayOf()
        return data.bytes()!!.readBytes(data.length.toInt())
    }

    @OptIn(BetaInteropApi::class)
    override fun getStringList(handle: NSObject, key: String): List<String> {
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

    override fun getBool(handle: NSObject, key: String): Boolean {
        val mmkv = handle as MMKV
        return mmkv.getBoolForKey(key)
    }

    override fun getLong(handle: NSObject, key: String): Long {
        val mmkv = handle as MMKV
        return mmkv.getInt64ForKey(key)
    }

    override fun getFloat(handle: NSObject, key: String): Float {
        val mmkv = handle as MMKV
        return mmkv.getFloatForKey(key)
    }

    override fun getDouble(handle: NSObject, key: String): Double {
        val mmkv = handle as MMKV
        return mmkv.getDoubleForKey(key)
    }

    override fun remove(handle: NSObject, key: String): Boolean {
        val mmkv = handle as MMKV
        if (!mmkv.containsKey(key)) {
            return false
        }
        mmkv.removeValueForKey(key)
        return true
    }

    override fun clear(handle: NSObject) {
        val mmkv = handle as MMKV
        mmkv.clearAll()
    }

    override fun destroy(handle: NSObject): Boolean {
        val mmkv = handle as MMKV
        return MMKV.removeStorage(mmkv.mmapID(),MMKV.mmkvBasePath())
    }

    override fun isAlive(handle: NSObject): Boolean {
        val mmkv = handle as MMKV
        return MMKV.checkExist(mmkv.mmapID(),MMKV.mmkvBasePath())
    }

    override fun size(handle: NSObject): Int {
        val mmkv = handle as MMKV
        return mmkv.count().toInt()
    }

    override fun allKeys(handle: NSObject): List<String> {
        val mmkv = handle as MMKV
        val list = mutableListOf<String>()
        mmkv.enumerateKeys { s, _ ->
            list.add(s!!)
        }
        return list
    }

    override fun exists(handle: NSObject, key: String): Boolean {
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
