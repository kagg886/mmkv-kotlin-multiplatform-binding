import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import top.kagg886.mkmb.MMKV
import top.kagg886.mkmb.initialize
import top.kagg886.mkmb.mmkvWithID
import top.kagg886.mkmb.defaultMMKV
import java.io.File


@RunWith(AndroidJUnit4::class)
class MMKVCryptKeyTest {
    @Before
    fun beforeAll() {
        if (!MMKV.initialized) {
            val appContext = InstrumentationRegistry.getInstrumentation().targetContext
            appContext.cacheDir.listFiles()?.forEach(File::deleteRecursively)
            MMKV.initialize(appContext.cacheDir.absolutePath) {
                logFunc = { _, tag, string ->
                    println("$tag : $string")
                }
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
    fun testCryptKeyNullBehavior() {
        // 测试cryptKey为null的情况
        val mmkvNull1 = MMKV.defaultMMKV(null)
        val mmkvNull2 = MMKV.mmkvWithID("null-test", null)

        // 测试基本功能
        mmkvNull1.set("null_key", "null_value")
        assertEquals("null_value", mmkvNull1.getString("null_key"))

        mmkvNull2.set("null_key2", 999)
        assertEquals(999, mmkvNull2.getInt("null_key2"))
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
