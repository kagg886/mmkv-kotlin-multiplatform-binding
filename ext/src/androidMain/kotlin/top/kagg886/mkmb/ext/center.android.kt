package top.kagg886.mkmb.ext

import android.os.Parcelable
import kotlinx.io.Buffer
import top.kagg886.mkmb.ext.util.asParcelable
import kotlin.reflect.KClass

actual fun <T : Any> getFromPlatform(clazz: KClass<T>, buffer: Buffer): T {
    if (clazz.java.isAssignableFrom(Parcelable::class.java)) {
        return buffer.asParcelable(clazz as KClass<Parcelable>) as T
    }
    error("unsupported type : ${clazz.simpleName}")
}
