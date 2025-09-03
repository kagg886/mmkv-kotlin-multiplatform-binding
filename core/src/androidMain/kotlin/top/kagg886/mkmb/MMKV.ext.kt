/**
 * Directly forked from MMKV.java
 */
package top.kagg886.mkmb

import android.content.SharedPreferences
import android.os.Parcelable
import kotlin.reflect.KClass

/**
 * Set a Parcelable value
 * @param key key
 * @param value value
 * @param expire expiration in seconds; 0 means never expires
 */
fun <T : Parcelable> MMKV.set(key: String, value: T, expire: Int = 0) {
    set(key, value.writeToParcelable(), expire)
}

/**
 * Import data from SharedPreferences
 * @param preferences SharedPreferences data
 */
fun MMKV.importFromSharedPreferences(preferences: SharedPreferences): Int {
    val kvs = preferences.all
    if (kvs.isNullOrEmpty()) return 0

    for ((key, value) in kvs) {
        if (key == null || value == null) continue

        when(value) {
            is Boolean -> set(key, value)
            is Int -> set(key, value)
            is Long -> set(key, value)
            is Float -> set(key, value)
            is Double -> set(key, value)
            is String -> set(key, value)
            is Set<*> -> set(key, value.map { it.toString() })
        }
    }

    return kvs.size
}

/**
 * Get a Parcelable value
 * @param key key
 * @return corresponding value
 */
inline fun <reified T : Parcelable> MMKV.get(key: String): T? = get(key, T::class)

fun <T : Parcelable> MMKV.get(key: String, clazz: KClass<T>): T? {
    val bytes = getByteArray(key)
    if (bytes.isEmpty()) {
        return null
    }
    return bytes.readFromParcelable(clazz)
}
