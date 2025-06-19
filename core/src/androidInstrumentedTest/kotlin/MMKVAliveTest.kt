import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import top.kagg886.mkmb.MMKV
import top.kagg886.mkmb.MMKVException
import top.kagg886.mkmb.initialize
import top.kagg886.mkmb.mmkvWithID
import java.io.File

@RunWith(AndroidJUnit4::class)
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

    @After
    fun afterAll() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        appContext.cacheDir.listFiles()?.forEach(File::deleteRecursively)
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
