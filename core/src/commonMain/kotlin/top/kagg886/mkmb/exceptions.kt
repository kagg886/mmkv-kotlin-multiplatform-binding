package top.kagg886.mkmb

/**
 * # MMKV异常
 * 当 **MKMB** 内部(并非MMKV本身)出现异常时，异常将会被抛出。
 */
class MMKVException(override val message: String) : RuntimeException(message)
