/**
 * ================================================
 * Author:     886kagg
 * Created on: 2025/8/25 14:28
 * ================================================
 */

enum class JvmTarget {
    MACOS,
    WINDOWS,
    LINUX;
}

val hostTarget by lazy {
    val osName = System.getProperty("os.name")
    when {
        osName.startsWith("Mac") -> JvmTarget.MACOS
        osName.startsWith("Win") -> JvmTarget.WINDOWS
        osName.startsWith("Linux") -> JvmTarget.LINUX
        else -> error("Unsupported OS: $osName")
    }
}
