package top.kagg886.mkmb.ext

import android.os.Parcelable
import kotlinx.io.Buffer
import top.kagg886.mkmb.ext.util.asParcelable
import kotlin.reflect.KClass

internal actual fun <T : Any> getFromPlatform(clazz: KClass<T>, buffer: Buffer): T {
    if (Parcelable::class.java.isAssignableFrom(clazz.java)) {
        return buffer.asParcelable(clazz as KClass<Parcelable>) as T
    }
    error("unsupported type : ${clazz.simpleName}")
}
