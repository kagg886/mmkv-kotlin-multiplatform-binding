package top.kagg886.mkmb

import android.os.Parcel
import android.os.Parcelable
import kotlin.reflect.KClass

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
