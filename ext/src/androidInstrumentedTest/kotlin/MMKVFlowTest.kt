import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.yield
import org.junit.Before
import org.junit.Test
import top.kagg886.mkmb.MMKV
import top.kagg886.mkmb.MMKVOptions
import top.kagg886.mkmb.ext.defaultMMKV
import top.kagg886.mkmb.ext.flow
import top.kagg886.mkmb.ext.mmkvWithID
import top.kagg886.mkmb.initialize
import java.io.File

/**
 * ================================================
 * Author:     886kagg
 * Created on: 2025/9/26 14:08
 * ================================================
 */
class MMKVFlowTest {
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
    fun testMMKVStringFlow() {
        val data = MMKV.mmkvWithID("test-string-flow")

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
        val data = MMKV.mmkvWithID("test-error-flow")

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
