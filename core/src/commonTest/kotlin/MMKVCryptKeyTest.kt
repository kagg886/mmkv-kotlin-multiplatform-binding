import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM
import top.kagg886.mkmb.*
import kotlin.test.*

class MMKVCryptKeyTest {
    @BeforeTest
    fun beforeAll() {
        if (!MMKV.initialized) {
            val testFile = "mmkv-crypt-key-test".toPath().apply {
                if (FileSystem.SYSTEM.exists(this)) {
                    FileSystem.SYSTEM.deleteRecursively(this)
                }
                FileSystem.SYSTEM.createDirectory(this)
            }
            MMKV.initialize(FileSystem.SYSTEM.canonicalize(testFile.normalized()).toString()) {
                logLevel = MMKVOptions.LogLevel.Debug
            }
        }
    }

    @Test
    fun testDefaultMMKVWithCryptKey() {
        val cryptKey = "test-crypt-key-123"
        val mmkv = MMKV.defaultMMKV(cryptKey = cryptKey)

        // Test basic read/write
        assertEquals(0, mmkv.getInt("key"))
        mmkv.set("key", 42)
        assertEquals(42, mmkv.getInt("key"))

        // Test string storage
        val testString = "加密测试字符串"
        mmkv.set("string_key", testString)
        assertEquals(testString, mmkv.getString("string_key"))

        // Verify instance is alive
        assertTrue(mmkv.isAlive())
    }

    @Test
    fun testMMKVWithIDAndCryptKey() {
        val cryptKey = "test-id-crypt-key-456"
        val mmkv = MMKV.mmkvWithID("test-crypt-id", cryptKey = cryptKey)

        // Test basic read/write
        assertEquals(false, mmkv.getBoolean("bool_key"))
        mmkv.set("bool_key", true)
        assertEquals(true, mmkv.getBoolean("bool_key"))

        // Test byte array storage
        val testBytes = byteArrayOf(1, 2, 3, 4, 5)
        mmkv.set("bytes_key", testBytes)
        assertContentEquals(testBytes, mmkv.getByteArray("bytes_key"))

        // Verify instance is alive
        assertTrue(mmkv.isAlive())
    }

    @Test
    fun testCryptKeyNullBehavior() {
        // Test behavior when cryptKey is null
        val mmkvNull1 = MMKV.defaultMMKV(cryptKey = null)
        val mmkvNull2 = MMKV.mmkvWithID("null-test", cryptKey = null)

        // Test basic behavior
        mmkvNull1.set("null_key", "null_value")
        assertEquals("null_value", mmkvNull1.getString("null_key"))

        mmkvNull2.set("null_key2", 999)
        assertEquals(999, mmkvNull2.getInt("null_key2"))

        // Verify instance is alive
        assertTrue(mmkvNull1.isAlive())
        assertTrue(mmkvNull2.isAlive())
    }

    @Test
    fun testCryptKeyDataPersistence() {
        val cryptKey = "persistence-test-key"
        val testId = "persistence-test"

        // First create instance and store data
        val mmkv1 = MMKV.mmkvWithID(testId, cryptKey = cryptKey)
        mmkv1.set("persistent_string", "持久化测试")
        mmkv1.set("persistent_int", 12345)
        mmkv1.set("persistent_bool", true)

        // Verify data exists
        assertEquals("持久化测试", mmkv1.getString("persistent_string"))
        assertEquals(12345, mmkv1.getInt("persistent_int"))
        assertEquals(true, mmkv1.getBoolean("persistent_bool"))
        assertEquals(3, mmkv1.size())

        // Recreate instance with the same id and cryptKey
        val mmkv2 = MMKV.mmkvWithID(testId, cryptKey = cryptKey)

        // Verify data persistence
        assertEquals("持久化测试", mmkv2.getString("persistent_string"))
        assertEquals(12345, mmkv2.getInt("persistent_int"))
        assertEquals(true, mmkv2.getBoolean("persistent_bool"))
        assertEquals(3, mmkv2.size())

        // Verify key list
        val keys = mmkv2.allKeys().sorted()
        assertContentEquals(listOf("persistent_bool", "persistent_int", "persistent_string"), keys)
    }

    @Test
    fun testCryptKeyWithDifferentDataTypes() {
        val cryptKey = "data-types-test"
        val mmkv = MMKV.mmkvWithID("data-types-test", cryptKey = cryptKey)

        // Test encrypted storage of various data types
        mmkv.set("long_key", 9876543210L)
        mmkv.set("float_key", 3.14f)
        mmkv.set("double_key", 2.718281828)

        val stringList = listOf("加密", "字符串", "列表")
        mmkv.set("list_key", stringList)

        // Verify reads
        assertEquals(9876543210L, mmkv.getLong("long_key"))
        assertEquals(3.14f, mmkv.getFloat("float_key"))
        assertEquals(2.718281828, mmkv.getDouble("double_key"))
        assertContentEquals(stringList, mmkv.getStringList("list_key"))

        // Verify all keys exist
        assertTrue(mmkv.exists("long_key"))
        assertTrue(mmkv.exists("float_key"))
        assertTrue(mmkv.exists("double_key"))
        assertTrue(mmkv.exists("list_key"))
    }
}
