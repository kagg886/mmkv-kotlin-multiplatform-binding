package top.kagg886.mkmb.ext.util

import android.os.Parcel
import android.os.Parcelable
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlin.reflect.KClass

/**
 * ================================================
 * Author:     886kagg
 * Created on: 2025/9/26 20:01
 * ================================================
 */


private const val TAG_PARCELABLE: Byte = 9

internal fun <T : Parcelable> T.toBuffer(): Buffer = Buffer().apply {
    writeByte(TAG_PARCELABLE)
    val b = writeToParcelable()
    writeInt(b.size)
    write(b)
}

internal fun <T : Parcelable> Buffer.asParcelable(clazz: KClass<T>): T = readByte().let {
    if (it != TAG_PARCELABLE) {
        error("can't be read as parcelable because the tag is ${it.tagToString()}")
    }
    val b = readInt()
    readByteArray(b).readFromParcelable(clazz) ?: error("can't be read as ${clazz.qualifiedName}.")
}


internal fun Parcelable.writeToParcelable(): ByteArray {
    val parcel = Parcel.obtain()
    writeToParcel(parcel, 0)
    val bytes = parcel.marshall()
    parcel.recycle()
    return bytes
}


internal fun <T : Parcelable> ByteArray.readFromParcelable(clazz: KClass<T>): T? {
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
