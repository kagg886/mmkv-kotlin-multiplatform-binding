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
        val mmkv = MMKV.defaultMMKV(cryptKey)
        
        // 测试基本的读写功能
        assertEquals(0, mmkv.getInt("key"))
        mmkv.set("key", 42)
        assertEquals(42, mmkv.getInt("key"))
        
        // 测试字符串存储
        val testString = "加密测试字符串"
        mmkv.set("string_key", testString)
        assertEquals(testString, mmkv.getString("string_key"))
        
        // 验证实例存活
        assertTrue(mmkv.isAlive())
    }

    @Test
    fun testMMKVWithIDAndCryptKey() {
        val cryptKey = "test-id-crypt-key-456"
        val mmkv = MMKV.mmkvWithID("test-crypt-id", cryptKey)
        
        // 测试基本的读写功能
        assertEquals(false, mmkv.getBoolean("bool_key"))
        mmkv.set("bool_key", true)
        assertEquals(true, mmkv.getBoolean("bool_key"))
        
        // 测试字节数组存储
        val testBytes = byteArrayOf(1, 2, 3, 4, 5)
        mmkv.set("bytes_key", testBytes)
        assertContentEquals(testBytes, mmkv.getByteArray("bytes_key"))
        
        // 验证实例存活
        assertTrue(mmkv.isAlive())
    }

    @Test
    fun testCryptKeyDataIsolation() {
        val cryptKey1 = "crypt-key-1"
        val cryptKey2 = "crypt-key-2"
        
        val mmkv1 = MMKV.mmkvWithID("isolation-test", cryptKey1)
        val mmkv2 = MMKV.mmkvWithID("isolation-test", cryptKey2)
        
        // 在第一个实例中存储数据
        mmkv1.set("shared_key", "value1")
        mmkv1.set("number", 100)
        
        // 在第二个实例中存储不同的数据
        mmkv2.set("shared_key", "value2")
        mmkv2.set("number", 200)
        
        // 验证数据隔离 - 不同cryptKey应该看到不同的数据
        assertEquals("value1", mmkv1.getString("shared_key"))
        assertEquals("value2", mmkv2.getString("shared_key"))
        assertEquals(100, mmkv1.getInt("number"))
        assertEquals(200, mmkv2.getInt("number"))
        
        // 验证键的存在性
        assertTrue(mmkv1.exists("shared_key"))
        assertTrue(mmkv2.exists("shared_key"))
        
        // 清理一个实例不应该影响另一个
        mmkv1.clear()
        assertEquals("", mmkv1.getString("shared_key"))
        assertEquals("value2", mmkv2.getString("shared_key"))
    }

    @Test
    fun testCryptKeyNullBehavior() {
        // 测试cryptKey为null的情况
        val mmkvNull1 = MMKV.defaultMMKV(null)
        val mmkvNull2 = MMKV.mmkvWithID("null-test", null)
        
        // 测试基本功能
        mmkvNull1.set("null_key", "null_value")
        assertEquals("null_value", mmkvNull1.getString("null_key"))
        
        mmkvNull2.set("null_key2", 999)
        assertEquals(999, mmkvNull2.getInt("null_key2"))
        
        // 验证实例存活
        assertTrue(mmkvNull1.isAlive())
        assertTrue(mmkvNull2.isAlive())
    }

    @Test
    fun testCryptKeyDataPersistence() {
        val cryptKey = "persistence-test-key"
        val testId = "persistence-test"
        
        // 第一次创建实例并存储数据
        val mmkv1 = MMKV.mmkvWithID(testId, cryptKey)
        mmkv1.set("persistent_string", "持久化测试")
        mmkv1.set("persistent_int", 12345)
        mmkv1.set("persistent_bool", true)
        
        // 验证数据存在
        assertEquals("持久化测试", mmkv1.getString("persistent_string"))
        assertEquals(12345, mmkv1.getInt("persistent_int"))
        assertEquals(true, mmkv1.getBoolean("persistent_bool"))
        assertEquals(3, mmkv1.size())
        
        // 重新创建相同ID和cryptKey的实例
        val mmkv2 = MMKV.mmkvWithID(testId, cryptKey)
        
        // 验证数据持久化
        assertEquals("持久化测试", mmkv2.getString("persistent_string"))
        assertEquals(12345, mmkv2.getInt("persistent_int"))
        assertEquals(true, mmkv2.getBoolean("persistent_bool"))
        assertEquals(3, mmkv2.size())
        
        // 验证键列表
        val keys = mmkv2.allKeys().sorted()
        assertContentEquals(listOf("persistent_bool", "persistent_int", "persistent_string"), keys)
    }

    @Test
    fun testCryptKeyWithDifferentDataTypes() {
        val cryptKey = "data-types-test"
        val mmkv = MMKV.mmkvWithID("data-types-test", cryptKey)
        
        // 测试各种数据类型的加密存储
        mmkv.set("long_key", 9876543210L)
        mmkv.set("float_key", 3.14f)
        mmkv.set("double_key", 2.718281828)
        
        val stringList = listOf("加密", "字符串", "列表")
        mmkv.set("list_key", stringList)
        
        // 验证读取
        assertEquals(9876543210L, mmkv.getLong("long_key"))
        assertEquals(3.14f, mmkv.getFloat("float_key"))
        assertEquals(2.718281828, mmkv.getDouble("double_key"))
        assertContentEquals(stringList, mmkv.getStringList("list_key"))
        
        // 验证所有键都存在
        assertTrue(mmkv.exists("long_key"))
        assertTrue(mmkv.exists("float_key"))
        assertTrue(mmkv.exists("double_key"))
        assertTrue(mmkv.exists("list_key"))
    }
}
