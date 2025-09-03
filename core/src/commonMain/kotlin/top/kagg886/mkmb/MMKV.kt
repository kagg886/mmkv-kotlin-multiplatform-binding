package top.kagg886.mkmb

import kotlinx.atomicfu.atomic

/**
 * # MMKV multiplatform interface
 * Facade for all platforms; implemented by each specific target.
 */
interface MMKV {
    companion object {
        /**
         * Whether MMKV is initialized
         * @see initialize
         * @see destroy
         */
        var initialized: Boolean by atomic(false)
            internal set
    }

    /**
     * Set an integer value
     * @param key key
     * @param value value
     * @param expire expiration in seconds; 0 means never expires
     */
    fun set(key: String, value: Int, expire: Int = 0)

    /**
     * Get an integer value
     * @param key key
     * @return the integer value
     */
    fun getInt(key: String): Int

    /**
     * Set a string value
     * @param key key
     * @param value value
     * @param expire expiration in seconds; 0 means never expires
     */
    fun set(key: String, value: String,expire: Int = 0)

    /**
     * Get a string value
     * @param key key
     * @return the string value
     */
    fun getString(key: String): String

    /**
     * Set a byte array value
     * @param key key
     * @param value value
     * @param expire expiration in seconds; 0 means never expires
     */
    fun set(key: String, value: ByteArray,expire: Int = 0)

    /**
     * Get a byte array value
     * @param key key
     * @return the byte array value
     */
    fun getByteArray(key: String): ByteArray

    /**
     * Set a list of strings
     * @param key key
     * @param value value
     * @param expire expiration in seconds; 0 means never expires
     */
    fun set(key: String, value: List<String>,expire: Int = 0)

    /**
     * Get a list of strings
     * @param key key
     * @return the list of strings
     */
    fun getStringList(key: String): List<String>

    /**
     * Set a boolean value
     * @param key key
     * @param value value
     * @param expire expiration in seconds; 0 means never expires
     */
    fun set(key: String, value: Boolean,expire: Int = 0)

    /**
     * Get a boolean value
     * @param key key
     * @return the boolean value
     */
    fun getBoolean(key: String): Boolean

    /**
     * Set a long value
     * @param key key
     * @param value value
     * @param expire expiration in seconds; 0 means never expires
     */
    fun set(key: String, value: Long,expire: Int = 0)

    /**
     * Get a long value
     * @param key key
     * @return the long value
     */
    fun getLong(key: String): Long

    /**
     * Set a float value
     * @param key key
     * @param value value
     * @param expire expiration in seconds; 0 means never expires
     */
    fun set(key: String, value: Float,expire: Int = 0)

    /**
     * Get a float value
     * @param key key
     * @return the float value
     */
    fun getFloat(key: String): Float

    /**
     * Set a double value
     * @param key key
     * @param value value
     * @param expire expiration in seconds; 0 means never expires
     */
    fun set(key: String, value: Double,expire: Int = 0)

    /**
     * Get a double value
     * @param key key
     * @return the double value
     */
    fun getDouble(key: String): Double

    /**
     * Remove the value for the specified key
     * @param key key
     * @return whether removal succeeded
     */
    fun remove(key: String): Boolean

    /**
     * # Clear all data inside the MMKV instance
     * Note: this does NOT delete files on disk.
     *
     * To delete files on disk, use: [destroy]
     */
    fun clear()

    /**
     * # Destroy the MMKV instance
     * Note: this destroys the in-memory instance AND deletes files on disk.
     *
     * If you only want to clear data without deleting files, use: [clear]
     */
    fun destroy(): Boolean

    /**
     * # Whether the instance is alive
     * @return whether the instance exists
     */
    fun isAlive(): Boolean

    /**
     * Get the number of stored key-value pairs
     * @return number of key-value pairs
     */
    fun size(): Int

    /**
     * Get all keys
     * @return list of all keys
     */
    fun allKeys(): List<String>

    /**
     * Check whether the specified key exists
     * @param key key
     * @return whether the key exists
     */
    fun exists(key: String): Boolean
}

/**
 * MMKV configuration options
 */
class MMKVOptions {

    /**
     * MMKV C library loader interface
     */
    fun interface MMKVCLibLoader {
        /**
         * Load the C library
         * @return the loaded library path
         */
        fun load(): String
    }

    /**
     * Log level enum
     */
    enum class LogLevel(val level: Int) {
        Debug(0),
        Info(1),
        Warning(2),
        Error(3),
        None(4);

        companion object {
            /**
             * Get the log level by its numeric value
             * @param level numeric level value
             * @return corresponding log level
             * @throws IllegalArgumentException if the value is invalid
             */
            fun from(level: Int): LogLevel =
                entries.find { it.level == level }
                    ?: throw IllegalArgumentException("Invalid log level: $level")
        }
    }

    /**
     * C library loader
     */
    var libLoader: MMKVCLibLoader = MMKV.defaultLoader

    /**
     * Log level
     */
    var logLevel: LogLevel = LogLevel.Debug

    /**
     * Log function
     */
    var logFunc: (LogLevel, String, String) -> Unit =
        { level, tag, it -> println("[$tag]: $level - $it") }
}

/**
 * MMKV process mode enum
 */
enum class MMKVMode(val value: Int) {
    /**
     * Single process (default)
     */
    SINGLE_PROCESS(1 shl 0),

    /**
     * Multi-process
     */
    MULTI_PROCESS(1 shl 1),

    /**
     * Read-only
     */
    READ_ONLY(1 shl 5);

    companion object {
        /**
         * Get the mode by value
         */
        fun fromValue(value: Int): MMKVMode? {
            return entries.find { it.value == value }
        }
    }
}

/**
 * Default C library loader
 */
expect val MMKV.Companion.defaultLoader: MMKVOptions.MMKVCLibLoader

/**
 * Initialize MMKV
 * @param path storage path
 * @param conf options
 */
fun MMKV.Companion.initialize(path: String, conf: MMKVOptions.() -> Unit = {}) =
    MMKV.initialize(path, MMKVOptions().apply(conf))

/**
 * Initialize MMKV
 * @param path storage path
 * @param conf options
 */
expect fun MMKV.Companion.initialize(path: String, options: MMKVOptions)

/**
 * Get the default MMKV instance
 * @param mode process mode; default is single-process
 * @param cryptKey encryption key (optional)
 * @return the default MMKV instance
 */
expect fun MMKV.Companion.defaultMMKV(mode: MMKVMode = MMKVMode.SINGLE_PROCESS, cryptKey: String? = null): MMKV

/**
 * Get an MMKV instance by ID
 * @param id instance ID
 * @param mode process mode; default is single-process
 * @param cryptKey encryption key (optional)
 * @return the MMKV instance
 */
expect fun MMKV.Companion.mmkvWithID(id: String, mode: MMKVMode = MMKVMode.SINGLE_PROCESS, cryptKey: String? = null): MMKV
