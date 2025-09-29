package top.kagg886.mkmb.ext

import kotlinx.io.Buffer
import top.kagg886.mkmb.MMKV
import top.kagg886.mkmb.MMKVMode
import top.kagg886.mkmb.defaultMMKV
import top.kagg886.mkmb.ext.util.*
import top.kagg886.mkmb.mmkvWithID
import kotlin.reflect.KClass

/**
 * ================================================
 * Author:     886kagg
 * Created on: 2025/9/26 10:26
 * ================================================
 */

internal class MMKVWithConfig(
    val mmkv: MMKV,
    val listener: MutableList<(String) -> Unit> = mutableListOf()
)

internal val center = mutableMapOf<String, MMKVWithConfig>()

/**
 * Returns a singleton `MMKV` instance backed by an internal center with event dispatching capability.
 *
 * - If it's the first call, creates a `DelegatedMMKV` wrapping the real default instance and registers it.
 * - Subsequent calls return the same wrapped instance for the key `"MMKV.default"`.
 *
 * @param mode The storage mode for MMKV. Defaults to [MMKVMode.SINGLE_PROCESS].
 * @param cryptKey Optional encryption key. Passing `null` means no encryption.
 * @return The default `MMKV` instance enhanced with change notifications.
 */
fun MMKV.Companion.defaultMMKV(mode: MMKVMode = MMKVMode.SINGLE_PROCESS, cryptKey: String? = null): MMKV =
    center.getOrPut("MMKV.default") {
        MMKVWithConfig(DelegatedMMKV("MMKV.default", MMKV.defaultMMKV(mode, cryptKey)))
    }.mmkv

/**
 * Returns (or creates) a named `MMKV` instance identified by [mmapID], wrapped with event dispatching.
 *
 * - Uses an internal center map to return the same wrapped instance for the same [mmapID].
 * - The wrapped instance enables `addEventListener` notifications for key changes.
 *
 * @param mmapID The unique identifier of the MMKV instance.
 * @param mode The storage mode for MMKV. Defaults to [MMKVMode.SINGLE_PROCESS].
 * @param cryptKey Optional encryption key. Passing `null` means no encryption.
 * @return The `MMKV` instance associated with [mmapID], enhanced with change notifications.
 */
fun MMKV.Companion.mmkvWithID(mmapID: String, mode: MMKVMode = MMKVMode.SINGLE_PROCESS, cryptKey: String? = null) =
    center.getOrPut(mmapID) {
        MMKVWithConfig(DelegatedMMKV(mmapID, MMKV.mmkvWithID(mmapID, mode, cryptKey)))
    }.mmkv

/**
 * Type-safe inline convenience for [get] using reified type [T].
 *
 * @param key The key to read.
 * @param default The value to return when the key does not exist or the buffer is empty.
 * @return The decoded value as [T], or [default] when absent.
 */
inline fun <reified T : Any> MMKV.get(key: String, default: T): T = get(key, T::class) ?: default

/**
 * Type-safe inline convenience for [get] using reified type [T].
 *
 * @param key The key to read.
 * @return The decoded value as [T], or null when absent.
 */
inline fun <reified T : Any> MMKV.get(key: String): T? = get(key, T::class)

/**
 * Reads a value from MMKV and decodes it as the given Kotlin class [clazz].
 *
 * Supported types: Int, Long, Float, Double, Boolean, String, List<String>, Iterable<String>, ByteArray.
 * Values are stored as tagged binary buffers by this extension layer.
 *
 * @param key The key to read.
 * @param clazz The expected Kotlin class of the value.
 * @param default The value to return when the key does not exist or the buffer is empty.
 * @return The decoded value cast to [T], or [default] when absent.
 * @throws IllegalStateException if the stored buffer tag doesn't match [clazz] or type unsupported.
 */
fun <T : Any> MMKV.get(key: String, clazz: KClass<T>, default: T? = null): T? {
    if (!exists(key)) {
        return default
    }

    val buf = getBuffer(key)
    if (buf.size == 0L) return default

    return when (clazz) {
        Int::class -> buf.asInt()
        Long::class -> buf.asLong()
        Float::class -> buf.asFloat()
        Double::class -> buf.asDouble()
        Boolean::class -> buf.asBoolean()
        String::class -> buf.asString()
        List::class -> buf.asStringList()
        Iterable::class -> buf.asStringList()
        ByteArray::class -> buf.asByteArray()
        else -> getFromPlatform(clazz, buf)
    } as T
}

/**
 * Adds a listener to receive callbacks when any key in this `MMKV` instance changes.
 *
 * The listener is invoked with the changed key. Returned function can be called to remove the listener.
 *
 * Note: Only `MMKV` instances created via `top.kagg886.mkmb.ext.MMKV.defaultMMKV` or
 * `top.kagg886.mkmb.ext.MMKV.mmkvWithID` support this method. Otherwise an error is thrown.
 *
 * @param listener Callback receiving the changed key.
 * @return A function that removes this listener when invoked.
 * @throws IllegalStateException if this `MMKV` is not a `DelegatedMMKV`.
 */
fun MMKV.addEventListener(listener: (String) -> Unit): () -> Unit {
    check(this is DelegatedMMKV) {
        "MMKV [$this] does not support addEventListener, have you called top.kagg886.mkmb.ext.MMKV.(defaultMMKV / mmkvWithID)?"
    }

    center[mmapID]?.listener?.add(listener) ?: error("MMKV [$mmapID] has been destroyed or not exist.")
    return {
        center[mmapID]?.listener?.remove(listener)
    }
}

expect fun <T: Any> getFromPlatform(clazz: KClass<T>,buffer: Buffer): T
