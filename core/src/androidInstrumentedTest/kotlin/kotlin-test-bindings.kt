import org.junit.Assert
import kotlin.reflect.KClass

fun <T> assertEquals(expected: T, actual: T) = Assert.assertEquals(expected, actual)

inline fun <reified T> assertContentEquals(expected: Array<out T>, actual: Array<out T>) =
    Assert.assertArrayEquals(expected, actual)

inline fun <reified T> assertContentEquals(expected: List<T>, actual: List<T>) =
    Assert.assertArrayEquals(expected.toTypedArray(), actual.toTypedArray())

fun assertTrue(condition: Boolean) = Assert.assertTrue(condition)
fun assertFalse(condition: Boolean) = Assert.assertFalse(condition)
inline fun <reified T : Throwable> assertFailsWith(clazz: KClass<T>, noinline block: () -> Unit): T? = Assert.assertThrows(clazz.java, block)
fun assertContentEquals(expected: ByteArray, actual: ByteArray) = Assert.assertArrayEquals(expected, actual)
