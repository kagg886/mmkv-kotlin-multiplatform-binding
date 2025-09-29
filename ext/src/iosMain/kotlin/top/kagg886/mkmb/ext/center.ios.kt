@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")


package top.kagg886.mkmb.ext

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ObjCClass
import kotlinx.io.Buffer
import platform.Foundation.NSClassFromString
import top.kagg886.mkmb.ext.util.asNSCodingProtocol
import kotlin.native.internal.InternalForKotlinNative
import kotlin.native.internal.reflect.objCNameOrNull
import kotlin.reflect.KClass

@OptIn(InternalForKotlinNative::class, BetaInteropApi::class)
actual fun <T : Any> getFromPlatform(clazz: KClass<T>, buffer: Buffer): T {
    if (clazz.objCNameOrNull != null) { //FIXME：只对kotlin类有效
        val objClass = NSClassFromString(clazz.objCNameOrNull!!) ?: error("only the @BindClassToObjCName annotation can work correctly")

        return buffer.asNSCodingProtocol(objClass) as T
    }

    error("unsupported type : ${clazz.simpleName}")
}
