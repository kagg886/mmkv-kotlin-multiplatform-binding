package top.kagg886.mkmb

import java.io.File
import java.io.FileInputStream
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.security.MessageDigest

internal fun <T> useArena(arena: Arena = Arena.ofConfined(), block: Arena.() -> T): T = with(arena) {
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

internal fun File.sha256(): String {
    val buffer = ByteArray(8192)
    val digest = MessageDigest.getInstance("SHA-256")

    FileInputStream(this).use { inputStream ->
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            digest.update(buffer, 0, bytesRead)
        }
    }

    return digest.digest().joinToString("") { "%02x".format(it) }
}
