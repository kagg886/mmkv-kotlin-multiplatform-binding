package top.kagg886.mkmb

import kotlinx.cinterop.*
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes

@OptIn(ExperimentalForeignApi::class)
fun ByteArray.useAsNSData(block: NSData.() -> Unit) {
    usePinned {
        val data = NSData.dataWithBytes(
            bytes = it.addressOf(0),
            length = this.size.toULong(),
        )
        block(data)
    }
}
