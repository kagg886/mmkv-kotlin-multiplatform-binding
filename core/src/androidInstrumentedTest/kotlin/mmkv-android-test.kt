import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import top.kagg886.mkmb.MMKV
import top.kagg886.mkmb.MMKVException
import top.kagg886.mkmb.defaultMMKV
import top.kagg886.mkmb.initialize
import top.kagg886.mkmb.mmkvWithID
import java.io.File


@RunWith(AndroidJUnit4::class)
class LogHistoryAndroidUnitTest {

    @Before
    fun init() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        appContext.cacheDir.listFiles()?.forEach(File::deleteRecursively)
        MMKV.initialize(appContext.cacheDir.absolutePath) {
            logFunc = { _,tag,string->
                println("$tag : $string")
            }
        }
    }

    @Test
    fun testMMKVIntStore() {
        val mmkv = MMKV.mmkvWithID("test-int-store")
        assertEquals(0, mmkv.getInt("key"))
        mmkv.set("key", 2)
        assertEquals(2, mmkv.getInt("key"))
    }

    @Test
    fun testMMKVStringStore() {
        val target = "UTF-8测试字符串，这个时候char*可以解析吗？"
        val mmkv = MMKV.mmkvWithID("test-string-store")
        assertEquals("", mmkv.getString("key"))
        mmkv.set("key", target)
        assertEquals(target, mmkv.getString("key"))
    }

    @Test
    fun testMMKVBooleanStore() {
        val mmkv = MMKV.mmkvWithID("test-bool-store")
        assertEquals(false, mmkv.getBoolean("key"))
        mmkv.set("key", true)
        assertEquals(true, mmkv.getBoolean("key"))
    }

    @Test
    fun testMMKVLongStore() {
        val mmkv = MMKV.mmkvWithID("test-long-store")
        assertEquals(0L, mmkv.getLong("key"))
        mmkv.set("key", 2L)
        assertEquals(2L, mmkv.getLong("key"))
    }

    @Test
    fun testMMKVFloatStore() {
        val mmkv = MMKV.mmkvWithID("test-float-store")
        assertEquals(0f, mmkv.getFloat("key"))
        mmkv.set("key", 2f)
        assertEquals(2f, mmkv.getFloat("key"))
    }

    @Test
    fun testMMKVDoubleStore() {
        val mmkv = MMKV.mmkvWithID("test-double-store")
        assertEquals(0.0, mmkv.getDouble("key"),1e-10)
        mmkv.set("key", 2.0)
        assertEquals(2.0, mmkv.getDouble("key"),1e-10)
    }

    @Test
    fun testMMKVByteArrayStore() {
        val dest = byteArrayOf(1, 2, 3, 4, 5)
        val mmkv = MMKV.mmkvWithID("test-bytes-store")
        assertArrayEquals(byteArrayOf(), mmkv.getByteArray("key"))
        mmkv.set("key", dest)
        assertArrayEquals(dest, mmkv.getByteArray("key"))
    }

    @Test
    fun testMMKVStringListStore() {
        val target = listOf("qww", "UTF-8", "字符串？")
        val mmkv = MMKV.mmkvWithID("test-string-list-store")
        assertArrayEquals(emptyArray(), mmkv.getStringList("key").toTypedArray())
        mmkv.set("key", target)
        assertArrayEquals(target.toTypedArray(), mmkv.getStringList("key").toTypedArray())
    }

    @Test
    fun testMMKVRemove() {
        val mmkv = MMKV.mmkvWithID("test-remove-store")
        assertFalse(mmkv.remove("key"))
        mmkv.set("key",1)
        assertTrue(mmkv.remove("key"))
    }

    @Test
    fun testMMKVClear() {
        val mmkv = MMKV.mmkvWithID("test-clear")
        mmkv.set("key",1)
        mmkv.set("qwq","awa")
        mmkv.clear()

        assertEquals(0, mmkv.getInt("key"))
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
        assertThrows(MMKVException::class.java) {
            mmkv.set("key",1)
        }
    }

    @Test
    fun testMMKVAllKeys() {
        val mmkv = MMKV.mmkvWithID("test-all-keys")
        mmkv.set("key",1)
        assertArrayEquals(listOf("key").toTypedArray(),mmkv.allKeys().toTypedArray())
        mmkv.set("qwq","awa")
        assertArrayEquals(listOf("key","qwq").toTypedArray().sortedArray(),mmkv.allKeys().toTypedArray().sortedArray())
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
        assertEquals(0, mmkv.size())
        mmkv.set("key",1)
        assertEquals(1, mmkv.size())
        mmkv.set("qwq","awa")
        assertEquals(2, mmkv.size())
        mmkv.clear()
        assertEquals(0, mmkv.size())
    }
}
