import top.kagg886.mkmb.*
import java.io.File
import kotlin.test.*

enum class JvmTarget(val ext: String) {
    MACOS("dylib"),
    WINDOWS("dll"),
    LINUX("so");
}

val hostTarget by lazy {
    val osName = System.getProperty("os.name")
    when {
        osName == "Mac OS X" -> JvmTarget.MACOS
        osName.startsWith("Win") -> JvmTarget.WINDOWS
        osName.startsWith("Linux") -> JvmTarget.LINUX
        else -> error("Unsupported OS: $osName")
    }
}

class MMKVInitTest {
    @BeforeTest
    fun beforeAll() {
        if (!MMKV.initialized) {
            val testFile = File("mmkv-test").apply {
                if (!exists()) {
                    mkdirs()
                }
                listFiles()?.forEach(File::deleteRecursively)
            }
            MMKV.initialize(testFile.absolutePath) {
                logLevel = MMKVOptions.LogLevel.Debug
            }
        }
    }

    @Test
    fun testMMKVIntStore() {
        val mmkv = MMKV.defaultMMKV()
        assertEquals(0, mmkv.getInt("key"))
        mmkv.set("key", 2)
        assertEquals(2, mmkv.getInt("key"))
    }

    @Test
    fun testMMKVStringStore() {
        val target = "UTF-8测试字符串，这个时候char*可以解析吗？"
        val mmkv = MMKV.defaultMMKV()
        assertEquals("", mmkv.getString("key"))
        mmkv.set("key", target)
        assertEquals(target, mmkv.getString("key"))
    }

    @Test
    fun testMMKVBooleanStore() {
        val mmkv = MMKV.defaultMMKV()
        assertEquals(false, mmkv.getBoolean("key"))
        mmkv.set("key", true)
        assertEquals(true, mmkv.getBoolean("key"))
    }

    @Test
    fun testMMKVLongStore() {
        val mmkv = MMKV.defaultMMKV()
        assertEquals(0L, mmkv.getLong("key"))
        mmkv.set("key", 2L)
        assertEquals(2L, mmkv.getLong("key"))
    }

    @Test
    fun testMMKVFloatStore() {
        val mmkv = MMKV.defaultMMKV()
        assertEquals(0f, mmkv.getFloat("key"))
        mmkv.set("key", 2f)
        assertEquals(2f, mmkv.getFloat("key"))
    }

    @Test
    fun testMMKVDoubleStore() {
        val mmkv = MMKV.defaultMMKV()
        assertEquals(0.0, mmkv.getDouble("key"))
        mmkv.set("key", 2.0)
        assertEquals(2.0, mmkv.getDouble("key"))
    }

    @Test
    fun testMMKVByteArrayStore() {
        val dest = byteArrayOf(1, 2, 3, 4, 5)
        val mmkv = MMKV.defaultMMKV()
        assertContentEquals(byteArrayOf(), mmkv.getByteArray("key"))
        mmkv.set("key", dest)
        assertContentEquals(dest, mmkv.getByteArray("key"))
    }

    @Test
    fun testMMKVStringListStore() {
        val target = listOf("qww", "UTF-8", "字符串？")
        val mmkv = MMKV.defaultMMKV()
        assertContentEquals(listOf(), mmkv.getStringList("key"))
        mmkv.set("key", target)
        assertContentEquals(target, mmkv.getStringList("key"))
    }

    @Test
    fun testMMKVNamedStore() {
        val mmkv = MMKV.mmkvWithID("awa")
        assertEquals(0, mmkv.getInt("key"))
        mmkv.set("key", 2)
        assertEquals(2, mmkv.getInt("key"))
    }

    @Test
    fun testMMKVRemove() {
        val mmkv = MMKV.defaultMMKV()
        assertFalse(mmkv.remove("key"))
        mmkv.set("key",1)
        assertTrue(mmkv.remove("key"))
    }

    @Test
    fun testMMKVClear() {
        val mmkv = MMKV.defaultMMKV()
        mmkv.set("key",1)
        mmkv.set("qwq","awa")
        mmkv.clear()

        assertEquals(0,mmkv.getInt("key"))
    }

    @Test
    fun testMMKVAlive() {
        val mmkv = MMKV.defaultMMKV()
        assertTrue(mmkv.isAlive())
        mmkv.set("key",1)
        mmkv.destroy()
        assertFalse(mmkv.isAlive())
    }
    @Test
    fun testMMKVMemoryProtect() {
        val mmkv = MMKV.defaultMMKV()
        mmkv.destroy()
        assertFailsWith(MMKVException::class) {
            mmkv.set("key",1)
        }
    }

    @Test
    fun testMMKVAllKeys() {
        val mmkv = MMKV.defaultMMKV()
        mmkv.set("key",1)
        assertContentEquals(listOf("key"),mmkv.allKeys())
        mmkv.set("qwq","awa")
        assertContentEquals(listOf("key","qwq"),mmkv.allKeys())
    }

    @Test
    fun testMMKVExists() {
        val mmkv = MMKV.defaultMMKV()
        mmkv.set("key",1)
        assertTrue(mmkv.exists("key"))
        assertFalse(mmkv.exists("qwq"))
        mmkv.set("qwq","awa")
        assertTrue(mmkv.exists("qwq"))
        assertFalse(mmkv.exists("awa"))
    }

    @Test
    fun testMMKVSize() {
        val mmkv = MMKV.defaultMMKV()
        assertEquals(0,mmkv.size())
        mmkv.set("key",1)
        assertEquals(1,mmkv.size())
        mmkv.set("qwq","awa")
        assertEquals(2,mmkv.size())
        mmkv.clear()
        assertEquals(0,mmkv.size())
    }
}
