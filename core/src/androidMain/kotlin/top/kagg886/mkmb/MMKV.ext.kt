/**
 * 直接 fork 自 MMKV.java
 */
package top.kagg886.mkmb

import android.os.Parcel
import android.os.Parcelable
import kotlin.reflect.KClass

fun <T : Parcelable> MMKV.set(key: String, value: T) {
    set(key, value.writeToParcelable())
}

internal fun Parcelable.writeToParcelable(): ByteArray {
    val parcel = Parcel.obtain()
    writeToParcel(parcel, 0)
    val bytes = parcel.marshall()
    parcel.recycle()
    return bytes
}


inline fun <reified T : Parcelable> MMKV.get(key: String): T? = get(key, T::class)

fun <T : Parcelable> MMKV.get(key: String, clazz: KClass<T>): T? {
    val bytes = getByteArray(key)
    if (bytes.isEmpty()) {
        return null
    }
    return bytes.readFromParcelable(clazz)
}

private fun <T : Parcelable> ByteArray.readFromParcelable(clazz: KClass<T>): T? {
    val source = Parcel.obtain()

    val data = try {
        source.unmarshall(this, 0, this.size)
        source.setDataPosition(0)

        val jClass = clazz.java
        val creator = jClass.getField("CREATOR").get(null)

        if (creator !is Parcelable.Creator<*>) {
            error("CREATOR is not a Parcelable.Creator<*>")
        }
        creator.createFromParcel(source)
    } finally {
        source.recycle()
    }

    return data as? T
}
