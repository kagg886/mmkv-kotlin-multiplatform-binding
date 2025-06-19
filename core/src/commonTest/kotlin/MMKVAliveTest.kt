import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM
import top.kagg886.mkmb.MMKV
import top.kagg886.mkmb.MMKVException
import top.kagg886.mkmb.MMKVOptions
import top.kagg886.mkmb.initialize
import top.kagg886.mkmb.mmkvWithID
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class MMKVAliveTest {
    @BeforeTest
    fun beforeAll() {
        if (!MMKV.initialized) {
            val testFile = "mmkv-alive-test".toPath().apply {
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
    fun testMMKVAlive() {
        val mmkv = MMKV.mmkvWithID("test-alive")
        mmkv.set("key",1)
        assertTrue(mmkv.isAlive())
        mmkv.destroy()
        assertFalse(mmkv.isAlive())
    }
    @Test
    fun testMMKVMemoryProtect() {
        val mmkv = MMKV.mmkvWithID("test-mem-protect")
        assertTrue(mmkv.destroy())
        assertFailsWith(MMKVException::class) {
            mmkv.set("key",1)
        }
    }
}
