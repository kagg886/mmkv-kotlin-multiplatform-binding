package top.kagg886.mkmb

import kotlinx.cinterop.*
import platform.Foundation.*
import platform.darwin.NSObject
import platform.darwin.NSObjectMeta

@OptIn(ExperimentalForeignApi::class)
fun ByteArray.toNSData(): NSData {
    return NSData.dataWithBytes(
        bytes = memScoped {
            allocArrayOf(this@toNSData)
        },
        length = this.size.toULong()
    )
}

@OptIn(ExperimentalForeignApi::class)
class MMKVStringList(private val list: List<String>) : NSObject(), NSCodingProtocol, List<String> by list {
    override fun encodeWithCoder(coder: NSCoder) {
        coder.encodeInt(list.size, "size")
        for (i in list.indices) {
            val data = list[i].encodeToByteArray()
            data.usePinned { pinned->
                coder.encodeBytes(pinned.addressOf(0).reinterpret(), data.size.toULong(), "data[$i]")
            }
        }
    }
    override fun initWithCoder(coder: NSCoder): NSCodingProtocol? {
        val size = coder.decodeIntForKey("size")
        if (size == 0) {
            return null
        }
        val list = mutableListOf<String>()
        for (i in 0 until size) {
            memScoped {
                val len = alloc<ULongVar>()
                val data = coder.decodeBytesForKey("data[$i]",len.ptr)!!
                list.add(data.readBytes(len.value.toInt()).decodeToString())
            }
        }
        return MMKVStringList(list)
    }

    companion object: NSObjectMeta() {
        @BetaInteropApi
        override fun `class`() = super.`class`()!!
    }
}
