import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.yield
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM
import top.kagg886.mkmb.MMKV
import top.kagg886.mkmb.MMKVOptions
import top.kagg886.mkmb.ext.defaultMMKV
import top.kagg886.mkmb.ext.flow
import top.kagg886.mkmb.ext.mmkvWithID
import top.kagg886.mkmb.initialize
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.time.Duration.Companion.seconds

/**
 * ================================================
 * Author:     886kagg
 * Created on: 2025/9/26 14:08
 * ================================================
 */
class MMKVFlowTest {
    @BeforeTest
    fun beforeAll() {
        if (!MMKV.initialized) {
            val testFile = "mmkv-ext-flow-test".toPath().apply {
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
    fun testMMKVStringFlow() {
        val data = MMKV.mmkvWithID("test-ext-string-flow")

        runBlocking {
            val flow = data.flow<String>("test")
            val job = launch {
                flow.collect {
                    println("receive change! the latest data is $it")
                }
            }

            yield()

            data.set("test", "1")
            data.set("test", "2")
            data.set("test", "3")
            data.set("test", "4")


            job.cancelAndJoin()
        }
    }

    @Test
    fun testMMKVStringFlowError() {
        val data = MMKV.mmkvWithID("test-ext-error-flow")

        runBlocking {
            val flow = data.flow<String>("test")

            val ex = CompletableDeferred<Throwable>()
            val job = launch {
                try {
                    flow.collect { println("receive: $it") }
                } catch (e: Throwable) {
                    ex.complete(e)
                }
            }

            yield()

            data.set("test", "1")
            data.set("test", "2")
            data.set("test", "3")
            data.set("test", "4")
            data.set("test", 5)
            job.cancelAndJoin()

            assertEquals("can't be read as string because the tag is INT",ex.await().message)
        }
    }
}
