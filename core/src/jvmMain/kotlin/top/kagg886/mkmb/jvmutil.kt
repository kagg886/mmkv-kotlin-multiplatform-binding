package top.kagg886.mkmb

import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

internal fun <T> String.makeCString(block: (MemorySegment) -> T) = with(Arena.ofConfined()) {
    use {
        val data = it.allocateFrom(this@makeCString)
        block(data)
    }
}

internal fun <T> useArena(block: Arena.() -> T): T = with(Arena.ofConfined()) {
    use(block)
}

internal val jvmTarget by lazy {
    val osName = System.getProperty("os.name")
    when {
        osName == "Mac OS X" -> JvmTarget.MACOS
        osName.startsWith("Win") -> JvmTarget.WINDOWS
        osName.startsWith("Linux") -> JvmTarget.LINUX
        else -> error("Unsupported OS: $osName")
    }
}

enum class JvmTarget {
    MACOS,
    WINDOWS,
    LINUX;
}
