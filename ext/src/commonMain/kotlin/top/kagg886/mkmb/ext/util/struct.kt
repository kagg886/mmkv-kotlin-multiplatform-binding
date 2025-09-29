package top.kagg886.mkmb.ext.util

import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.io.readDouble
import kotlinx.io.readFloat
import kotlinx.io.readString
import kotlinx.io.readTo
import kotlinx.io.writeDouble
import kotlinx.io.writeFloat
import kotlinx.io.writeString
import top.kagg886.mkmb.MMKV
import top.kagg886.mkmb.ext.DelegatedMMKV


/**
 * ================================================
 * Author:     886kagg
 * Created on: 2025/9/26 14:20
 * ================================================
 */

private const val TAG_INT: Byte = 1
private const val TAG_LONG: Byte = 2
private const val TAG_FLOAT: Byte = 3
private const val TAG_DOUBLE: Byte = 4
private const val TAG_STRING: Byte = 5

private const val TAG_STRING_LIST: Byte = 6
private const val TAG_BYTE_ARRAY: Byte = 7
private const val TAG_BOOLEAN: Byte = 8

private const val TAG_PARCELABLE: Byte = 8

private const val TAG_OBJC_OBJECT: Byte = 9

internal fun Byte.tagToString() = when (this) {
    TAG_INT -> "INT"
    TAG_LONG -> "LONG"
    TAG_FLOAT -> "FLOAT"
    TAG_DOUBLE -> "DOUBLE"
    TAG_STRING -> "STRING"
    TAG_STRING_LIST -> "STRING_LIST"
    TAG_BOOLEAN -> "BOOLEAN"
    TAG_BYTE_ARRAY -> "BYTE_ARRAY"
    TAG_PARCELABLE -> "PARCELABLE"
    TAG_OBJC_OBJECT -> "OBJECTIVE_C_OBJECT"
    else -> error("unknown tag: $this")
}


internal fun Int.toBuffer() = Buffer().apply {
    writeByte(TAG_INT)
    writeInt(this@toBuffer)
}

internal fun Long.toBuffer() = Buffer().apply {
    writeByte(TAG_LONG)
    writeLong(this@toBuffer)
}

internal fun Float.toBuffer() = Buffer().apply {
    writeByte(TAG_FLOAT)
    writeFloat(this@toBuffer)
}

internal fun Double.toBuffer() = Buffer().apply {
    writeByte(TAG_DOUBLE)
    writeDouble(this@toBuffer)
}
internal fun String.toBuffer() = Buffer().apply {
    writeByte(TAG_STRING)
    writeString(this@toBuffer)
}
internal fun List<String>.toBuffer() = Buffer().apply {
    writeByte(TAG_STRING_LIST)
    writeInt(this@toBuffer.size)
    for (string in this@toBuffer) {
        val byt = string.encodeToByteArray()
        writeInt(byt.size)
        write(byt)
    }
}

internal fun ByteArray.toBuffer() = Buffer().apply {
    writeByte(TAG_BYTE_ARRAY)
    write(this@toBuffer)
}

internal fun Boolean.toBuffer() = Buffer().apply {
    writeByte(TAG_BOOLEAN)
    writeByte(if (this@toBuffer) 1.toByte() else 0.toByte())
}

internal fun Buffer.asInt() = readByte().let {
    if (it == TAG_INT) readInt() else error("can't be read as long because the tag is ${it.tagToString()}")
}

internal fun Buffer.asLong() = readByte().let {
    if (it == TAG_LONG) readLong() else error("can't be read as long because the tag is ${it.tagToString()}")
}
internal fun Buffer.asFloat() = readByte().let {
    if (it == TAG_FLOAT) readFloat() else error("can't be read as float because the tag is ${it.tagToString()}")
}
internal fun Buffer.asDouble() = readByte().let {
    if (it == TAG_DOUBLE) readDouble() else error("can't be read as double because the tag is ${it.tagToString()}")
}

internal fun Buffer.asString() = readByte().let {
    if (it == TAG_STRING) readString() else error("can't be read as string because the tag is ${it.tagToString()}")
}

internal fun Buffer.asStringList() = readByte().let {
    if (it != TAG_STRING_LIST) {
        error("can't be read as string-list because the tag is $TAG_STRING_LIST")
    }
    val size = readInt()
    println("size: $size")

    List(size) {
        readByteArray(readInt()).decodeToString()
    }
}

internal fun Buffer.asByteArray() = readByte().let {
    if (it == TAG_BYTE_ARRAY) readByteArray() else error("can't be read as byte-array because the tag is ${it.tagToString()}")
}

internal fun Buffer.asBoolean() = readByte().let {
    if (it == TAG_BOOLEAN) readByte() != 0.toByte() else error("can't be read as boolean because the tag is ${it.tagToString()}")
}

internal fun MMKV.set(key: String, value: Buffer, expire: Int = 0) {
    val arr = ByteArray(value.size.toInt()).apply {
        value.readTo(this)
    }

    set(key, arr, expire)
}

internal fun MMKV.getBuffer(key: String): Buffer = Buffer().apply {
    check(this@getBuffer is DelegatedMMKV) {
        "MMKV [$this] does not support this method, have you called top.kagg886.mkmb.ext.MMKV.(defaultMMKV / mmkvWithID)?"
    }
    write(this@getBuffer.delegate.getByteArray(key))
}
