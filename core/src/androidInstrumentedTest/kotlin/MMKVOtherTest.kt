import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import top.kagg886.mkmb.MMKV
import top.kagg886.mkmb.initialize
import top.kagg886.mkmb.mmkvWithID
import java.io.File

@RunWith(AndroidJUnit4::class)
class MMKVOtherTest {
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
