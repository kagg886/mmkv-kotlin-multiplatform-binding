import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM
import top.kagg886.mkmb.MMKV
import top.kagg886.mkmb.MMKVOptions
import top.kagg886.mkmb.ext.addEventListener
import top.kagg886.mkmb.ext.defaultMMKV
import top.kagg886.mkmb.ext.get
import top.kagg886.mkmb.ext.mmkvWithID
import top.kagg886.mkmb.initialize
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

/**
 * ================================================
 * Author:     886kagg
 * Created on: 2025/9/26 14:08
 * ================================================
 */


class MMKVCenterTest {
    @BeforeTest
    fun beforeAll() {
        if (!MMKV.initialized) {
            val testFile = "mmkv-ext-center-test".toPath().apply {
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
    fun testMMKVExtEquals() {
        val mmkv1 = MMKV.defaultMMKV()
        val mmkv2 = MMKV.defaultMMKV()
        assertEquals(mmkv1, mmkv2)
    }

    @Test
    fun testMMKVExtIntStore() {
        val mmkv = MMKV.mmkvWithID("test-ext-int-store")
        assertEquals(0, mmkv.get("key",0))
        mmkv.set("key", 2)
        assertEquals(2, mmkv.get("key"))
    }

    @Test
    fun testMMKVExtStringStore() {
        val target = "UTF-8测试字符串，这个时候char*可以解析吗？"
        val mmkv = MMKV.mmkvWithID("test-ext-string-store")
        assertEquals("", mmkv.get("key",""))
        mmkv.set("key", target)
        assertEquals(target, mmkv.get("key"))
    }

    @Test
    fun testMMKVExtBooleanStore() {
        val mmkv = MMKV.mmkvWithID("test-ext-bool-store")
        assertEquals(false, mmkv.get("key",false))
        mmkv.set("key", true)
        assertEquals(true, mmkv.get("key"))
    }

    @Test
    fun testMMKVExtLongStore() {
        val mmkv = MMKV.mmkvWithID("test-ext-long-store")
        assertEquals(0L, mmkv.get("key",0L))
        mmkv.set("key", 2L)
        assertEquals(2L, mmkv.get("key"))
    }

    @Test
    fun testMMKVExtFloatStore() {
        val mmkv = MMKV.mmkvWithID("test-ext-float-store")
        assertEquals(0f, mmkv.get("key",0f))
        mmkv.set("key", 2f)
        assertEquals(2f, mmkv.get("key"))
    }

    @Test
    fun testMMKVExtDoubleStore() {
        val mmkv = MMKV.mmkvWithID("test-ext-double-store")
        assertEquals(0.0, mmkv.get("key",0.0))
        mmkv.set("key", 2.0)
        assertEquals(2.0, mmkv.get("key"))
    }

    @Test
    fun testMMKVExtByteArrayStore() {
        val dest = byteArrayOf(1, 2, 3, 4, 5)
        val mmkv = MMKV.mmkvWithID("test-ext-bytes-store")
        assertContentEquals(byteArrayOf(), mmkv.get("key",byteArrayOf()))
        mmkv.set("key", dest)
        assertContentEquals(dest, mmkv.get("key"))
    }

    @Test
    fun testMMKVExtStringListStore() {
        val target = listOf("qww", "UTF-8", "字符串？")
        val mmkv = MMKV.mmkvWithID("test-ext-string-list-store")
        assertContentEquals(listOf<String>(), mmkv.get("key",listOf()))
        mmkv.set("key", target)
        assertContentEquals(target.sorted(), mmkv.get<List<String>>("key")!!.sorted())
    }
}
