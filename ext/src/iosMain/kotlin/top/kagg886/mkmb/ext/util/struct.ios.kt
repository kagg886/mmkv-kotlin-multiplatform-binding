package top.kagg886.mkmb.ext.util

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCClass
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import platform.Foundation.NSCodingProtocol
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.NSKeyedArchiver
import platform.Foundation.NSKeyedUnarchiver
import platform.Foundation.dataWithBytes
import platform.posix.memcpy

/**
 * ================================================
 * Author:     886kagg
 * Created on: 2025/9/26 20:01
 * ================================================
 */

private const val TAG_OBJC_OBJECT: Byte = 9


@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal fun <T : NSCodingProtocol> T.toBuffer(): Buffer = Buffer().apply {
    writeByte(TAG_OBJC_OBJECT)

    memScoped {
        // 使用 NSKeyedArchiver 序列化对象
        val error = alloc<ObjCObjectVar<NSError?>>()
        val data = NSKeyedArchiver.archivedDataWithRootObject(this@toBuffer, requiringSecureCoding = false, error = error.ptr)

        if (error.value != null || data == null) {
            error("can't read NSCoding: ${error.value?.localizedDescription}")
        }

        val byt = data.toByteArray()
        writeInt(byt.size)
        write(byt)
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal fun <T : NSCodingProtocol> Buffer.asNSCodingProtocol(objcClass: ObjCClass): T = readByte().let {
    if (it != TAG_OBJC_OBJECT) error("can't be read as nscoding because the tag is ${it.tagToString()}")

    val len = readInt()
    val byteArray = readByteArray(len)
    val nsData = byteArray.toNSData()

    memScoped {
        // 反序列化
        val error = alloc<ObjCObjectVar<NSError?>>()
        val obj = NSKeyedUnarchiver.unarchivedObjectOfClass(
            cls = objcClass,
            fromData = nsData,
            error = error.ptr
        ) ?: throw IllegalStateException("Can't unarchive object: ${error.value?.localizedDescription}")

        if (error.value != null) {
            error("can't read NSCoding: ${error.value?.localizedDescription}")
        }

        @Suppress("UNCHECKED_CAST")
        obj as T
    }
}




@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val length = this.length.toInt()
    if (length == 0) return ByteArray(0)

    return ByteArray(length).usePinned {
        memcpy(it.addressOf(0), this@toByteArray.bytes?.reinterpret<ByteVar>(), length.toULong())
        it.get()
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun ByteArray.toNSData(): NSData = usePinned {
    NSData.dataWithBytes(
        bytes = it.addressOf(0),
        length = this.size.toULong(),
    )
}
