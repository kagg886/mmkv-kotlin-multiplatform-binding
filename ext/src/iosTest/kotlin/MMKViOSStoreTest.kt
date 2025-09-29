import kotlinx.cinterop.BetaInteropApi
import okio.FileSystem
import okio.Path.Companion.toPath
import platform.Foundation.NSCodingProtocol
import platform.Foundation.NSURL
import top.kagg886.mkmb.MMKV
import top.kagg886.mkmb.MMKVOptions
import top.kagg886.mkmb.ext.get
import top.kagg886.mkmb.ext.mmkvWithID
import top.kagg886.mkmb.ext.set
import top.kagg886.mkmb.initialize
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull


class MMKViOSStoreTest {

    @BeforeTest
    fun beforeAll() {
        if (!MMKV.initialized) {
            val testFile = "mmkv-ext-ios-test".toPath().apply {
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

        assertNull(mmkv.get<NSURL>("data"))

        mmkv.set("data", url as NSCodingProtocol)

        assertEquals(url,mmkv.get<NSURL>("data"))
    }
}
