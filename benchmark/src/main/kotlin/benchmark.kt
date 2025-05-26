import top.kagg886.mkmb.MMKV
import top.kagg886.mkmb.defaultMMKV
import top.kagg886.mkmb.initialize
import kotlin.time.Duration
import kotlin.time.measureTime

fun main(args: Array<String>) {
    MMKV.initialize("mmkv-benchmark")

    val settings = MMKV.defaultMMKV()
    val (time, ns) = benchmark {
        settings.set("test", 114514)
        settings.getInt("test")
    }
    println("MMKV Origin Int Store Benchmark: cost: $time,avg: $ns")

    val (time2, ns2) = benchmark {
        settings.set("test", "床前明月光，疑是地上霜，举头望明月，低头思故乡")
        settings.getString("test")
    }
    println("MMKV Origin String Store Benchmark: cost: $time2,avg: $ns2")

    val (time3, ns3) = benchmark {
        settings.set("test", 1145141919810520)
        settings.getLong("test")
    }
    println("MMKV Origin Long Store Benchmark: cost: $time3,avg: $ns3")

    val (time4, ns4) = benchmark {
        settings.set("test", 3.141592653589793)
        settings.getDouble("test")
    }
    println("MMKV Origin Double Store Benchmark: cost: $time4,avg: $ns4")

    val (time5, ns5) = benchmark {
        settings.set("test", 3.1415927f)
        settings.getFloat("test")
    }
    println("MMKV Origin Float Store Benchmark: cost: $time5,avg: $ns5")

    val (time6, ns6) = benchmark {
        settings.set("test", true)
        settings.getBoolean("test")
    }
    println("MMKV Origin Boolean Store Benchmark: cost: $time6,avg: $ns6")

    val byt = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8)
    val (time7, ns7) = benchmark {
        settings.set("test", byt)
        settings.getByteArray("test")
    }
    println("MMKV Origin ByteArray Store Benchmark: cost: $time7,avg: $ns7")

    val list = "原 神 ， 启 动 ！".split(" ")
    val (time8, ns8) = benchmark {
        settings.set("test", list)
        settings.getStringList("test")
    }
    println("MMKV Origin List Store Benchmark: cost: $time8,avg: $ns8")

    //10000
    //MMKV Origin Int Store Benchmark: cost: 122.172300ms,avg: 12217
    //MMKV Origin String Store Benchmark: cost: 284.759500ms,avg: 28475
    //MMKV Origin Long Store Benchmark: cost: 83.586700ms,avg: 8358
    //MMKV Origin Double Store Benchmark: cost: 73.715100ms,avg: 7371
    //MMKV Origin Float Store Benchmark: cost: 64.344900ms,avg: 6434
    //MMKV Origin Boolean Store Benchmark: cost: 52.675300ms,avg: 5267
    //MMKV Origin ByteArray Store Benchmark: cost: 161.866900ms,avg: 16186
    //MMKV Origin List Store Benchmark: cost: 384.565ms,avg: 38456

    //1000000
    //MMKV Origin Int Store Benchmark: cost: 987.454900ms,avg: 987
    //MMKV Origin String Store Benchmark: cost: 2.310656800s,avg: 2310
    //MMKV Origin Long Store Benchmark: cost: 990.175400ms,avg: 990
    //MMKV Origin Double Store Benchmark: cost: 824.121ms,avg: 824
    //MMKV Origin Float Store Benchmark: cost: 814.752900ms,avg: 814
    //MMKV Origin Boolean Store Benchmark: cost: 750.027700ms,avg: 750
    //MMKV Origin ByteArray Store Benchmark: cost: 2.262743900s,avg: 2262
    //MMKV Origin List Store Benchmark: cost: 6.834319600s,avg: 6834
}

private fun benchmark(repeat: Int = 1000000, block: () -> Unit): Pair<Duration, Long> {
    val result = measureTime {
        repeat(repeat) {
            block()
        }
    }
    return result to result.inWholeNanoseconds / repeat
}
