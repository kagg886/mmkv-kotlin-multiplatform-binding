import kotlinx.cinterop.BetaInteropApi
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM
import kotlin.test.Test
import platform.Foundation.NSURL
import top.kagg886.mkmb.MMKV
import top.kagg886.mkmb.MMKVOptions
import top.kagg886.mkmb.getNSCoding
import top.kagg886.mkmb.initialize
import top.kagg886.mkmb.mmkvWithID
import top.kagg886.mkmb.set
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MMKViOSStoreTest {

    @BeforeTest
    fun beforeAll() {
        if (!MMKV.initialized) {
            val testFile = "mmkv-ios-test".toPath().apply {
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


    @OptIn(BetaInteropApi::class)
    @Test
    fun testMMKVNSCodingStoreTest() {
        val url = NSURL.URLWithString("https://google.com")

        val mmkv = MMKV.mmkvWithID("test-nscoding")

        assertNull(mmkv.getNSCoding("data", NSURL.`class`()!!))

        mmkv.set("data",url)

        assertEquals(url,mmkv.getNSCoding("data", NSURL.`class`()!!))
    }
}
