package top.kagg886.mkmb.ext

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import top.kagg886.mkmb.MMKV

/**
 * ================================================
 * Author:     886kagg
 * Created on: 2025/9/26 13:51
 * ================================================
 */

/**
 * Creates a cold Flow that emits the current value for [key] and
 * subsequently emits on every change to that key in this `MMKV` instance.
 *
 * Emission is based on the internal `addEventListener` mechanism provided by
 * the wrapped `DelegatedMMKV`. The flow sends `default` when the key
 * does not exist.
 *
 * @param key The key to observe.
 * @param default The value to emit when the key does not exist.
 * @return A `Flow<T?>` that observes changes for [key].
 * @throws IllegalStateException if the value cannot be decoded as `T`.
 */
inline fun <reified T : Any> MMKV.flow(key: String, default: T? = null) = callbackFlow {
    val dispose = addEventListener {
        val data = get<T>(key) ?: default
        if (trySend(data).isFailure) {
            error("send failed")
        }
    }

    trySend(get<T>(key) ?: default)

    awaitClose {
        dispose()
    }
}
