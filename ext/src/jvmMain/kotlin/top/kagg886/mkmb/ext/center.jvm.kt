package top.kagg886.mkmb.ext

import kotlinx.io.Buffer
import kotlin.reflect.KClass

internal actual fun <T : Any> getFromPlatform(clazz: KClass<T>, buffer: Buffer): T = error("unsupported type : ${clazz.simpleName}")
