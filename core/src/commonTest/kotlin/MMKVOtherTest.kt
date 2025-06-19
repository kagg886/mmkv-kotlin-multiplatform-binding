import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM
import top.kagg886.mkmb.MMKV
import top.kagg886.mkmb.MMKVOptions
import top.kagg886.mkmb.initialize
import top.kagg886.mkmb.mmkvWithID
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MMKVOtherTest {
    @BeforeTest
    fun beforeAll() {
        if (!MMKV.initialized) {
            val testFile = "mmkv-other-test".toPath().apply {
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

    @AfterTest
    fun afterAll() {
        val testFile = "mmkv-alive-test".toPath()
        if (FileSystem.SYSTEM.exists(testFile)) {
            FileSystem.SYSTEM.deleteRecursively(testFile)
        }
    }

    @Test
    fun testMMKVClear() {
        val mmkv = MMKV.mmkvWithID("test-clear")
        mmkv.set("key",1)
        mmkv.set("qwq","awa")
        mmkv.clear()
        assertEquals(0,mmkv.getInt("key"))
    }

    @Test
    fun testMMKVAllKeys() {
        val mmkv = MMKV.mmkvWithID("test-all-keys")
        mmkv.set("key",1)
        assertContentEquals(listOf("key"),mmkv.allKeys())
        mmkv.set("qwq","awa")
        assertContentEquals(listOf("key","qwq"),mmkv.allKeys().sorted())
    }

    @Test
    fun testMMKVExists() {
        val mmkv = MMKV.mmkvWithID("test-exists")
        mmkv.set("key",1)
        assertTrue(mmkv.exists("key"))
        assertFalse(mmkv.exists("qwq"))
        mmkv.set("qwq","awa")
        assertTrue(mmkv.exists("qwq"))
        assertFalse(mmkv.exists("awa"))
    }

    @Test
    fun testMMKVSize() {
        val mmkv = MMKV.mmkvWithID("test-size")
        assertEquals(0,mmkv.size())
        mmkv.set("key",1)
        assertEquals(1,mmkv.size())
        mmkv.set("qwq","awa")
        assertEquals(2,mmkv.size())
        mmkv.clear()
        assertEquals(0,mmkv.size())
    }
}
