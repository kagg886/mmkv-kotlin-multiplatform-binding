/**
 * 直接 fork 自 MMKV.java
 */
package top.kagg886.mkmb

import android.content.SharedPreferences
import android.os.Parcelable
import kotlin.reflect.KClass

/**
 * 设置Parcelable对象值
 * @param key 键
 * @param value 值
 * @param expire 过期时间，单位：秒。默认为0，表示不过期
 */
fun <T : Parcelable> MMKV.set(key: String, value: T, expire: Int = 0) {
    set(key, value.writeToParcelable(), expire)
}

/**
 * 导入SharedPreferences数据
 * @param preferences SharedPreferences数据
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
 * 获取Parcelable对象值
 * @param key 键
 * @return 对应的值
 */
inline fun <reified T : Parcelable> MMKV.get(key: String): T? = get(key, T::class)

fun <T : Parcelable> MMKV.get(key: String, clazz: KClass<T>): T? {
    val bytes = getByteArray(key)
    if (bytes.isEmpty()) {
        return null
    }
    return bytes.readFromParcelable(clazz)
}
