package top.kagg886.mkmb

import kotlinx.atomicfu.atomic

/**
 * # MMKV多平台接口
 * 为各个平台提供的门面，实现由具体的target进行提供。
 */
interface MMKV {
    companion object {
        /**
         * 是否已经初始化
         * @see initialize
         * @see destroy
         */
        var initialized: Boolean by atomic(false)
            internal set
    }

    /**
     * 设置整型值
     * @param key 键
     * @param value 值
     */
    fun set(key: String, value: Int)

    /**
     * 获取整型值
     * @param key 键
     * @return 对应的整型值
     */
    fun getInt(key: String): Int

    /**
     * 设置字符串值
     * @param key 键
     * @param value 值
     */
    fun set(key: String, value: String)

    /**
     * 获取字符串值
     * @param key 键
     * @return 对应的字符串值
     */
    fun getString(key: String): String

    /**
     * 设置字节数组值
     * @param key 键
     * @param value 值
     */
    fun set(key: String, value: ByteArray)

    /**
     * 获取字节数组值
     * @param key 键
     * @return 对应的字节数组值
     */
    fun getByteArray(key: String): ByteArray

    /**
     * 设置字符串列表值
     * @param key 键
     * @param value 值
     */
    fun set(key: String, value: List<String>)

    /**
     * 获取字符串列表值
     * @param key 键
     * @return 对应的字符串列表值
     */
    fun getStringList(key: String): List<String>

    /**
     * 设置布尔值
     * @param key 键
     * @param value 值
     */
    fun set(key: String, value: Boolean)

    /**
     * 获取布尔值
     * @param key 键
     * @return 对应的布尔值
     */
    fun getBoolean(key: String): Boolean

    /**
     * 设置长整型值
     * @param key 键
     * @param value 值
     */
    fun set(key: String, value: Long)

    /**
     * 获取长整型值
     * @param key 键
     * @return 对应的长整型值
     */
    fun getLong(key: String): Long

    /**
     * 设置浮点型值
     * @param key 键
     * @param value 值
     */
    fun set(key: String, value: Float)

    /**
     * 获取浮点型值
     * @param key 键
     * @return 对应的浮点型值
     */
    fun getFloat(key: String): Float

    /**
     * 设置双精度浮点型值
     * @param key 键
     * @param value 值
     */
    fun set(key: String, value: Double)

    /**
     * 获取双精度浮点型值
     * @param key 键
     * @return 对应的双精度浮点型值
     */
    fun getDouble(key: String): Double

    /**
     * 移除指定键的值
     * @param key 键
     * @return 是否移除成功
     */
    fun remove(key: String): Boolean

    /**
     * # 清空MMKV实例内的所有内容
     * 注意：不会删除存储在本地磁盘上的文件。
     *
     * 若您要删除本地文件，请使用: [destroy]
     */
    fun clear()

    /**
     * # 销毁MMKV实例
     * 注意：不仅会销毁内存中的MMKV实例，还会删除存储在本地磁盘上的文件。
     *
     * 若您要仅清空数据但销毁实例，请使用: [clear]
     */
    fun destroy(): Boolean

    /**
     * # 实例是否存在
     * @return 实例是否存在
     */
    fun isAlive(): Boolean

    /**
     * 获取存储的键值对数量
     * @return 键值对数量
     */
    fun size(): Int

    /**
     * 获取所有键
     * @return 所有键的列表
     */
    fun allKeys(): List<String>

    /**
     * 检查指定键是否存在
     * @param key 键
     * @return 键是否存在
     */
    fun exists(key: String): Boolean
}

/**
 * MMKV配置选项类
 */
class MMKVOptions {

    /**
     * MMKV C库加载器接口
     */
    fun interface MMKVCLibLoader {
        /**
         * 加载C库
         * @return 加载的库路径
         */
        fun load(): String
    }

    /**
     * 日志级别枚举
     */
    enum class LogLevel(val level: Int) {
        Debug(0),
        Info(1),
        Warning(2),
        Error(3),
        None(4);

        companion object {
            /**
             * 根据级别值获取对应的日志级别
             * @param level 级别值
             * @return 对应的日志级别
             * @throws IllegalArgumentException 如果级别值无效
             */
            fun from(level: Int): LogLevel =
                entries.find { it.level == level } ?: throw IllegalArgumentException("Invalid log level: $level")
        }
    }

    /**
     * C库加载器
     */
    var libLoader: MMKVCLibLoader = MMKV.defaultLoader

    /**
     * 日志级别
     */
    var logLevel: LogLevel = LogLevel.Debug

    /**
     * 日志函数
     */
    var logFunc: (LogLevel, String, String) -> Unit = { level, tag, it -> println("[$tag]: $level - $it") }
}

/**
 * 默认的C库加载器
 */
expect val MMKV.Companion.defaultLoader: MMKVOptions.MMKVCLibLoader

/**
 * 初始化MMKV
 * @param path 存储路径
 * @param conf 配置选项
 */
fun MMKV.Companion.initialize(path: String, conf: MMKVOptions.() -> Unit = {}) = MMKV.initialize(path, MMKVOptions().apply(conf))

/**
 * 初始化MMKV
 * @param path 存储路径
 * @param conf 配置选项
 */
expect fun MMKV.Companion.initialize(path: String, options:MMKVOptions)

/**
 * 获取默认的MMKV实例
 * @return 默认的MMKV实例
 */
expect fun MMKV.Companion.defaultMMKV(): MMKV

/**
 * 根据ID获取MMKV实例
 * @param id 实例ID
 * @return 对应的MMKV实例
 */
expect fun MMKV.Companion.mmkvWithID(id: String): MMKV
