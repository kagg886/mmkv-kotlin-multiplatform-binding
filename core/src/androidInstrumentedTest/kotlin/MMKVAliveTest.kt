import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.Test
import top.kagg886.mkmb.MMKV
import top.kagg886.mkmb.MMKVException
import top.kagg886.mkmb.initialize
import top.kagg886.mkmb.mmkvWithID
import java.io.File


class MMKVAliveTest {
    @Before
    fun beforeAll() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        appContext.cacheDir.listFiles()?.forEach(File::deleteRecursively)
        MMKV.initialize(appContext.cacheDir.absolutePath) {
            logFunc = { _,tag,string->
                println("$tag : $string")
            }
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
