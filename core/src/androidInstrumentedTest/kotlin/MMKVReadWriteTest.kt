import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import data.TestParcel
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import top.kagg886.mkmb.MMKV
import top.kagg886.mkmb.get
import top.kagg886.mkmb.initialize
import top.kagg886.mkmb.mmkvWithID
import top.kagg886.mkmb.set
import java.io.File
import kotlin.time.Duration.Companion.seconds

@RunWith(AndroidJUnit4::class)
class MMKVReadWriteTest {
    @Before
    fun beforeAll() {
        if (!MMKV.initialized) {
            val appContext = InstrumentationRegistry.getInstrumentation().targetContext
            appContext.cacheDir.listFiles()?.forEach(File::deleteRecursively)
            MMKV.initialize(appContext.cacheDir.absolutePath) {
                logFunc = { _,tag,string->
                    println("$tag : $string")
                }
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
        assertEquals(0.0, mmkv.getDouble("key"))
        mmkv.set("key", 2.0)
        assertEquals(2.0, mmkv.getDouble("key"))
    }

    @Test
    fun testMMKVByteArrayStore() {
        val dest = byteArrayOf(1, 2, 3, 4, 5)
        val mmkv = MMKV.mmkvWithID("test-bytes-store")
        assertContentEquals(byteArrayOf(), mmkv.getByteArray("key"))
        mmkv.set("key", dest)
        assertContentEquals(dest, mmkv.getByteArray("key"))
    }

    @Test
    fun testMMKVStringListStore() {
        val target = listOf("qww", "UTF-8", "字符串？")
        val mmkv = MMKV.mmkvWithID("test-string-list-store")
        assertContentEquals(listOf(), mmkv.getStringList("key"))
        mmkv.set("key", target)
        assertContentEquals(target, mmkv.getStringList("key"))
    }

    @Test
    fun testMMKVRemove() {
        val mmkv = MMKV.mmkvWithID("test-remove-store")
        assertFalse(mmkv.remove("key"))
        mmkv.set("key",1)
        assertTrue(mmkv.remove("key"))
    }

    @Test
    fun testMMKVParcelableStore() {
        val data = TestParcel("886","kagg",21,false,55.4f,123.456)

        val mmkv = MMKV.mmkvWithID("test-parcelable-store")
        assertNull(mmkv.get("key"))
        mmkv.set("key",data)
        assertEquals(data,mmkv.get("key"))
    }

    @Test
    fun testMMKVExpireStringTest() = runBlocking {
        val target = "UTF-8测试字符串"
        val mmkv = MMKV.mmkvWithID("test-expire-string-store")
        assertFalse(mmkv.exists("key"))
        mmkv.set("key", target, 3)
        assertEquals(target, mmkv.getString("key"))
        delay(3.seconds)
        assertFalse(mmkv.exists("key"))
    }

    @Test
    fun testMMKVExpireBooleanTest() = runBlocking {
        val mmkv = MMKV.mmkvWithID("test-expire-boolean-store")
        assertFalse(mmkv.exists("key"))
        mmkv.set("key", true, 3)
        assertEquals(true, mmkv.getBoolean("key"))
        delay(3.seconds)
        assertFalse(mmkv.exists("key"))
    }

    @Test
    fun testMMKVExpireLongTest() = runBlocking {
        val mmkv = MMKV.mmkvWithID("test-expire-long-store")
        assertFalse(mmkv.exists("key"))
        mmkv.set("key", 2L, 3)
        assertEquals(2L, mmkv.getLong("key"))
        delay(3.seconds)
        assertFalse(mmkv.exists("key"))
    }

    @Test
    fun testMMKVExpireFloatTest() = runBlocking {
        val mmkv = MMKV.mmkvWithID("test-expire-float-store")
        assertFalse(mmkv.exists("key"))
        mmkv.set("key", 2.5f, 3)
        assertEquals(2.5f, mmkv.getFloat("key"))
        delay(3.seconds)
        assertFalse(mmkv.exists("key"))
    }

    @Test
    fun testMMKVExpireDoubleTest() = runBlocking {
        val mmkv = MMKV.mmkvWithID("test-expire-double-store")
        assertFalse(mmkv.exists("key"))
        mmkv.set("key", 2.5, 3)
        assertEquals(2.5, mmkv.getDouble("key"))
        delay(3.seconds)
        assertFalse(mmkv.exists("key"))
    }

    @Test
    fun testMMKVExpireByteArrayTest() = runBlocking {
        val dest = byteArrayOf(1, 2, 3, 4, 5)
        val mmkv = MMKV.mmkvWithID("test-expire-bytes-store")
        assertFalse(mmkv.exists("key"))
        mmkv.set("key", dest, 3)
        assertContentEquals(dest, mmkv.getByteArray("key"))
        delay(3.seconds)
        assertFalse(mmkv.exists("key"))
    }

    @Test
    fun testMMKVExpireStringListTest() = runBlocking {
        val target = listOf("qww", "UTF-8", "字符串？")
        val mmkv = MMKV.mmkvWithID("test-expire-string-list-store")
        assertFalse(mmkv.exists("key"))
        mmkv.set("key", target, 3)
        assertContentEquals(target, mmkv.getStringList("key"))
        delay(3.seconds)
        assertFalse(mmkv.exists("key"))
    }
}
