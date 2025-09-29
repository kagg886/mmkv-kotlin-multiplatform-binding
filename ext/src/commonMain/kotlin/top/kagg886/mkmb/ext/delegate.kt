package top.kagg886.mkmb.ext

import top.kagg886.mkmb.MMKV
import top.kagg886.mkmb.ext.util.asBoolean
import top.kagg886.mkmb.ext.util.asByteArray
import top.kagg886.mkmb.ext.util.asDouble
import top.kagg886.mkmb.ext.util.asFloat
import top.kagg886.mkmb.ext.util.asInt
import top.kagg886.mkmb.ext.util.asLong
import top.kagg886.mkmb.ext.util.asString
import top.kagg886.mkmb.ext.util.asStringList
import top.kagg886.mkmb.ext.util.getBuffer
import top.kagg886.mkmb.ext.util.set
import top.kagg886.mkmb.ext.util.toBuffer

/**
 * ================================================
 * Author:     886kagg
 * Created on: 2025/9/26 10:29
 * ================================================
 */

internal class DelegatedMMKV(val mmapID: String, val delegate: MMKV) : MMKV by delegate {
    override fun equals(other: Any?): Boolean = other is DelegatedMMKV && delegate == other.delegate
    override fun hashCode(): Int = delegate.hashCode()

    override fun destroy(): Boolean = delegate.destroy().apply {
        center.remove(mmapID)
    }

    override fun set(key: String, value: String, expire: Int) = delegate.set(key, value.toBuffer(), expire).apply {
        center[mmapID]?.listener?.forEach { it(key) }
    }

    override fun set(key: String, value: Boolean, expire: Int) = delegate.set(key, value.toBuffer(), expire).apply {
        center[mmapID]?.listener?.forEach { it(key) }
    }

    override fun set(key: String, value: ByteArray, expire: Int) = delegate.set(key, value.toBuffer(), expire).apply {
        center[mmapID]?.listener?.forEach { it(key) }
    }

    override fun set(key: String, value: Double, expire: Int) = delegate.set(key, value.toBuffer(), expire).apply {
        center[mmapID]?.listener?.forEach { it(key) }
    }

    override fun set(key: String, value: Float, expire: Int) = delegate.set(key, value.toBuffer(), expire).apply {
        center[mmapID]?.listener?.forEach { it(key) }
    }

    override fun set(key: String, value: Int, expire: Int) = delegate.set(key, value.toBuffer(), expire).apply {
        center[mmapID]?.listener?.forEach { it(key) }
    }

    override fun set(key: String, value: Long, expire: Int) = delegate.set(key, value.toBuffer(), expire).apply {
        center[mmapID]?.listener?.forEach { it(key) }
    }

    override fun set(key: String, value: List<String>, expire: Int) =
        delegate.set(key, value.toBuffer(), expire).apply {
            center[mmapID]?.listener?.forEach { it(key) }
        }

    override fun getInt(key: String): Int = delegate.getBuffer(key).asInt()
    override fun getLong(key: String): Long = delegate.getBuffer(key).asLong()
    override fun getFloat(key: String): Float = delegate.getBuffer(key).asFloat()
    override fun getDouble(key: String): Double = delegate.getBuffer(key).asDouble()
    override fun getString(key: String): String = delegate.getBuffer(key).asString()
    override fun getBoolean(key: String): Boolean = delegate.getBuffer(key).asBoolean()
    override fun getByteArray(key: String): ByteArray = delegate.getBuffer(key).asByteArray()
    override fun getStringList(key: String): List<String> = delegate.getBuffer(key).asStringList()

    override fun remove(key: String) = delegate.remove(key).apply {
        center[mmapID]?.listener?.forEach { it(key) }
    }

    override fun clear() {
        val keys = allKeys()
        delegate.clear()
        center[mmapID]?.listener?.forEach {
            keys.forEach { key ->
                it(key)
            }
        }
    }
}
