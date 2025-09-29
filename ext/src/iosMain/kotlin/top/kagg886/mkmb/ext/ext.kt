package top.kagg886.mkmb.ext

import platform.Foundation.NSCodingProtocol
import top.kagg886.mkmb.MMKV
import top.kagg886.mkmb.ext.util.asNSCodingProtocol
import top.kagg886.mkmb.ext.util.getBuffer
import top.kagg886.mkmb.ext.util.set
import top.kagg886.mkmb.ext.util.toBuffer
import kotlin.reflect.KClass

/**
 * ================================================
 * Author:     886kagg
 * Created on: 2025/9/26 20:51
 * ================================================
 */

fun <T : NSCodingProtocol> MMKV.set(key: String, value: T, expire: Int = 0) =
    if (this !is DelegatedMMKV) error("") else delegate.set(key, value.toBuffer(), expire).apply {
        center[mmapID]?.listener?.forEach { it(key) }
    }


inline fun <reified T: NSCodingProtocol> MMKV.getNSCoding(key: String, default:T? = null) = getNSCoding(key,T::class, default)

fun <T : NSCodingProtocol> MMKV.getNSCoding(key: String, clazz: KClass<T>, default: T? = null): T? {
    if (!exists(key)) {
        return default
    }

    return getFromPlatform(clazz,getBuffer(key))
}
