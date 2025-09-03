package top.kagg886.mkmb

/**
 * # MMKV exception
 * When an exception occurs inside **MKMB** (not MMKV itself), it will be thrown.
 */
class MMKVException(override val message: String) : RuntimeException(message)
