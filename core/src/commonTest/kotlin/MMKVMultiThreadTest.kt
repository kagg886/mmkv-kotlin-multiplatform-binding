import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM
import top.kagg886.mkmb.MMKV
import top.kagg886.mkmb.MMKVOptions
import top.kagg886.mkmb.initialize
import top.kagg886.mkmb.mmkvWithID
import kotlin.test.BeforeTest
import kotlin.test.Test

class MMKVMultiThreadTest {
    @BeforeTest
    fun beforeAll() {
        if (!MMKV.initialized) {
            val testFile = "mmkv-multi-thread-test".toPath().apply {
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

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    @Test
    fun testMMKVMultiThread() : Unit = runBlocking {
        val mmkv = MMKV.mmkvWithID("test-multi-thread")
        val thread1 = newSingleThreadContext("thread-1")
        val thread2 = newSingleThreadContext("thread-2")
        val thread3 = newSingleThreadContext("thread-3")
        val thread4 = newSingleThreadContext("thread-4")

        withContext(thread1) {
            mmkv.set("key", 2)
        }
        withContext(thread2) {
            mmkv.getInt("key")
        }

        withContext(thread3) {
            mmkv.set("key", 3)
        }
        withContext(thread4) {
            mmkv.getInt("key")
        }
    }
}
