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
internal actual fun <T : Any> getFromPlatform(clazz: KClass<T>, buffer: Buffer): T {
    val nsClass = (clazz.objCNameOrNull ?: clazz.qualifiedName ?: clazz.simpleName)?.let {
        NSClassFromString(it)
    }

    if (nsClass != null) {
        return buffer.asNSCodingProtocol(nsClass) as T
    }

    error("unsupported type : ${clazz.simpleName}")
}
