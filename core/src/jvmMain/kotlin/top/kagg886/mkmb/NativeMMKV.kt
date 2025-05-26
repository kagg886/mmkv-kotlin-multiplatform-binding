package top.kagg886.mkmb

import kotlinx.atomicfu.atomic
import java.lang.foreign.*
import java.lang.foreign.ValueLayout.*
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType


internal fun interface MMKVInternalLog {
    fun invoke(level: Int, tag: MemorySegment, message: MemorySegment): Int
}

internal object NativeMMKV {
    internal var global by atomic<Arena?>(null)
    internal var dll by atomic<SymbolLookup?>(null)

    val MMKVC_STRING_SET_RETURN_STRUCT: StructLayout = MemoryLayout.structLayout(
        ADDRESS.withName("items"),
        JAVA_LONG.withName("size"),
    )

    val mmkvc_init: (String, Int, (Int, String, String) -> Unit) -> Unit by lazy {
        val funcHandle = Linker.nativeLinker().downcallHandle(
            dll!!.find("mmkvc_init").orElseThrow(),
            FunctionDescriptor.ofVoid(ADDRESS, JAVA_INT, ADDRESS)
        )

        val adapter = MethodHandles.lookup().findVirtual(
            MMKVInternalLog::class.java,
            "invoke",
            MethodType.methodType(
                Int::class.java,
                Int::class.java,
                MemorySegment::class.java,
                MemorySegment::class.java
            )
        )

        return@lazy { path, logLevel, logFunc ->
            val loggerStub = Linker.nativeLinker().upcallStub(
                adapter.bindTo(
                    MMKVInternalLog { level, tag0, message0 ->
                        val tag = tag0.reinterpret(Long.MAX_VALUE).getString(0)
                        val msg = message0.reinterpret(Long.MAX_VALUE).getString(0)
                        logFunc(level, tag, msg)
                        1
                    }
                ),
                FunctionDescriptor.of(
                    JAVA_INT,
                    JAVA_INT,// int level
                    ADDRESS, // char* tag
                    ADDRESS  // char* message
                ),
                global,
            )

//            path.makeCString { cPath ->
//                funcHandle.invoke(cPath, logLevel, loggerStub)
//            }
            useArena {
                val cPath = allocateFrom(path)
                funcHandle.invoke(cPath, logLevel, loggerStub)
            }
        }
    }

    val mmkvc_defaultMMKV: () -> MMKV by lazy {
        val funcHandle = Linker.nativeLinker().downcallHandle(
            dll!!.find("mmkvc_defaultMMKV").orElseThrow(),
            FunctionDescriptor.of(ADDRESS)
        )

        return@lazy {
            val ptr = funcHandle.invoke() as? MemorySegment ?: error("mmkvc_defaultMMKV return null")
            PanamaMMKV(ptr)
        }
    }
    val mmkvc_mmkvWithID: (String) -> MMKV by lazy {
        val funcHandle = Linker.nativeLinker().downcallHandle(
            dll!!.find("mmkvc_mmkvWithID").orElseThrow(),
            FunctionDescriptor.of(ADDRESS, ADDRESS)
        )

        return@lazy {
            val ptr = useArena {
                val cId = allocateFrom(it)
                funcHandle.invoke(cId) as? MemorySegment ?: error("mmkvc_mmkvWithID return null")
            }
            PanamaMMKV(ptr)
        }
    }

    val mmkvc_getInt: (MemorySegment, String) -> Int by lazy {
        val funcHandle = Linker.nativeLinker().downcallHandle(
            dll!!.find("mmkvc_getInt").orElseThrow(),
            FunctionDescriptor.of(JAVA_INT, ADDRESS, ADDRESS)
        )

        return@lazy { mmkv, key ->
            useArena {
                val cKey = allocateFrom(key)
                funcHandle.invoke(mmkv, cKey) as Int
            }
        }
    }
    val mmkvc_setInt: (MemorySegment, String, Int) -> Unit by lazy {
        val funcHandle = Linker.nativeLinker().downcallHandle(
            dll!!.find("mmkvc_setInt").orElseThrow(),
            FunctionDescriptor.ofVoid(ADDRESS, ADDRESS, JAVA_INT)
        )

        return@lazy { mmkv, key, value ->
            useArena {
                val cKey = allocateFrom(key)
                funcHandle.invoke(mmkv, cKey, value)
            }
        }
    }

    val mmkvc_getString: (MemorySegment, String) -> String by lazy {
        val funcHandle = Linker.nativeLinker().downcallHandle(
            dll!!.find("mmkvc_getString").orElseThrow(),
            FunctionDescriptor.of(ADDRESS, ADDRESS, ADDRESS)
        )

        return@lazy { mmkv, key ->
            val segment = useArena {
                val cKey = allocateFrom(key)
                funcHandle.invoke(mmkv, cKey) as? MemorySegment ?: error("mmkvc_getString return null")
            }
            val str = segment.reinterpret(Long.MAX_VALUE).getString(0);
            free(segment)
            str
        }
    }
    val mmkvc_setString: (MemorySegment, String, String) -> Unit by lazy {
        val funcHandle = Linker.nativeLinker().downcallHandle(
            dll!!.find("mmkvc_setString").orElseThrow(),
            FunctionDescriptor.ofVoid(ADDRESS, ADDRESS, ADDRESS)
        )

        return@lazy { mmkv, key, value ->
            useArena {
                val cKey = allocateFrom(key)
                val cValue = allocateFrom(value)
                funcHandle.invoke(mmkv, cKey, cValue)
            }
        }
    }

    val mmkvc_getFloat: (MemorySegment, String) -> Float by lazy {
        val funcHandle = Linker.nativeLinker().downcallHandle(
            dll!!.find("mmkvc_getFloat").orElseThrow(),
            FunctionDescriptor.of(JAVA_FLOAT, ADDRESS, ADDRESS)
        )

        return@lazy { mmkv, key ->
            useArena {
                val cKey = allocateFrom(key)
                funcHandle.invoke(mmkv, cKey) as Float
            }
        }
    }
    val mmkvc_setFloat: (MemorySegment, String, Float) -> Unit by lazy {
        val funcHandle = Linker.nativeLinker().downcallHandle(
            dll!!.find("mmkvc_setFloat").orElseThrow(),
            FunctionDescriptor.ofVoid(ADDRESS, ADDRESS, JAVA_FLOAT)
        )

        return@lazy { mmkv, key, value ->
            useArena {
                val cKey = allocateFrom(key)
                funcHandle.invoke(mmkv, cKey, value)
            }
        }
    }

    val mmkvc_getLong: (MemorySegment, String) -> Long by lazy {
        val funcHandle = Linker.nativeLinker().downcallHandle(
            dll!!.find("mmkvc_getLong").orElseThrow(),
            FunctionDescriptor.of(JAVA_LONG, ADDRESS, ADDRESS)
        )

        return@lazy { mmkv, key ->
            useArena {
                val cKey = allocateFrom(key)
                funcHandle.invoke(mmkv, cKey) as Long
            }
        }
    }
    val mmkvc_setLong: (MemorySegment, String, Long) -> Unit by lazy {
        val funcHandle = Linker.nativeLinker().downcallHandle(
            dll!!.find("mmkvc_setLong").orElseThrow(),
            FunctionDescriptor.ofVoid(ADDRESS, ADDRESS, JAVA_LONG)
        )

        return@lazy { mmkv, key, value ->
            useArena {
                val cKey = allocateFrom(key)
                funcHandle.invoke(mmkv, cKey, value)
            }
        }
    }

    val mmkvc_getDouble: (MemorySegment, String) -> Double by lazy {
        val funcHandle = Linker.nativeLinker().downcallHandle(
            dll!!.find("mmkvc_getDouble").orElseThrow(),
            FunctionDescriptor.of(JAVA_DOUBLE, ADDRESS, ADDRESS)
        )

        return@lazy { mmkv, key ->
            useArena {
                val cKey = allocateFrom(key)
                funcHandle.invoke(mmkv, cKey) as Double
            }
        }
    }
    val mmkvc_setDouble: (MemorySegment, String, Double) -> Unit by lazy {
        val funcHandle = Linker.nativeLinker().downcallHandle(
            dll!!.find("mmkvc_setDouble").orElseThrow(),
            FunctionDescriptor.ofVoid(ADDRESS, ADDRESS, JAVA_DOUBLE)
        )

        return@lazy { mmkv, key, value ->
            useArena {
                val cKey = allocateFrom(key)
                funcHandle.invoke(mmkv, cKey, value)
            }
        }
    }

    val mmkvc_getBoolean: (MemorySegment, String) -> Boolean by lazy {
        val funcHandle = Linker.nativeLinker().downcallHandle(
            dll!!.find("mmkvc_getBool").orElseThrow(),
            FunctionDescriptor.of(JAVA_BOOLEAN, ADDRESS, ADDRESS)
        )

        return@lazy { mmkv, key ->
            useArena {
                val cKey = allocateFrom(key)
                funcHandle.invoke(mmkv, cKey) as Boolean
            }
        }
    }
    val mmkvc_setBoolean: (MemorySegment, String, Boolean) -> Unit by lazy {
        val funcHandle = Linker.nativeLinker().downcallHandle(
            dll!!.find("mmkvc_setBool").orElseThrow(),
            FunctionDescriptor.ofVoid(ADDRESS, ADDRESS, JAVA_BOOLEAN)
        )

        return@lazy { mmkv, key, value ->
            useArena {
                val cKey = allocateFrom(key)
                funcHandle.invoke(mmkv, cKey, value)
            }
        }
    }

    val mmkvc_getByteArray: (MemorySegment, String) -> ByteArray by lazy {
        val funcHandle = Linker.nativeLinker().downcallHandle(
            dll!!.find("mmkvc_getByteArray").orElseThrow(),
            FunctionDescriptor.of(ADDRESS, ADDRESS, ADDRESS,ADDRESS)
        )
        return@lazy { mmkv, key ->
            useArena {
                val keyPtr = allocateFrom(key)
                val sizePtr = allocate(JAVA_LONG).reinterpret(JAVA_LONG.byteSize())
                val memoryPtr = funcHandle.invoke(mmkv, keyPtr, sizePtr) as MemorySegment

                val size = sizePtr.get(JAVA_LONG,0)

                val array = memoryPtr.reinterpret(size).toArray(JAVA_BYTE)
                free(memoryPtr)
                array
            }
        }
    }
    val mmkvc_setByteArray: (MemorySegment, String, ByteArray) -> Unit by lazy {
        val funcHandle = Linker.nativeLinker().downcallHandle(
            dll!!.find("mmkvc_setByteArray").orElseThrow(),
            FunctionDescriptor.ofVoid(ADDRESS, ADDRESS, ADDRESS, JAVA_LONG)
        )

        return@lazy { mmkv, key, value ->
//            key.makeCString {
//                Arena.ofConfined()!!.use { arena: Arena ->
//                    val segment = arena.allocate(value.size.toLong())
//                    segment.copyFrom(MemorySegment.ofArray(value))
//                    funcHandle.invoke(mmkv, it, segment, value.size.toLong())
//                }
//
//            }
            useArena {
                val keyPtr = allocateFrom(key)
                val valuePtr = allocate(value.size.toLong())
                valuePtr.copyFrom(MemorySegment.ofArray(value))
                funcHandle.invoke(mmkv, keyPtr, valuePtr, value.size.toLong())
            }
        }
    }

    val mmkvc_getStringList: (MemorySegment, String) -> List<String> by lazy {
        val funcHandle = Linker.nativeLinker().downcallHandle(
            dll!!.find("mmkvc_getStringList").orElseThrow(),
            FunctionDescriptor.of(ADDRESS, ADDRESS, ADDRESS)
        )
        // 获取字段的偏移量
        val itemOffset = MMKVC_STRING_SET_RETURN_STRUCT.byteOffset(MemoryLayout.PathElement.groupElement("items"))
        val lenOffset = MMKVC_STRING_SET_RETURN_STRUCT.byteOffset(MemoryLayout.PathElement.groupElement("size"))

        return@lazy { mmkv, key ->
            val segment = useArena {
                val keyPtr = allocateFrom(key)
                (funcHandle.invoke(mmkv, keyPtr) as? MemorySegment)?.reinterpret(MMKVC_STRING_SET_RETURN_STRUCT.byteSize()) ?: error("mmkvc_getStringList return null")
            }

            // 获取字符串数组指针和数组长度
            val len = segment.get(JAVA_LONG, lenOffset)
            val itemsPtr = segment.get(ADDRESS, itemOffset).reinterpret(len * ADDRESS.byteSize());

            val result = mutableListOf<String>()

            for (i in 0 until len) {
                val itemPtr = itemsPtr.getAtIndex(ADDRESS, i)
                result.add(itemPtr.reinterpret(Long.MAX_VALUE).getString(0))
                free(itemPtr)
            }
            free(itemsPtr)
            free(segment)
            // 返回结果List
            result
        }
    }
    val mmkvc_setStringList: (MemorySegment, String, List<String>) -> Unit by lazy {
        val funcHandle = Linker.nativeLinker().downcallHandle(
            dll!!.find("mmkvc_setStringList").orElseThrow(),
            FunctionDescriptor.ofVoid(ADDRESS, ADDRESS, ADDRESS, JAVA_LONG)
        )

        return@lazy { mmkv, key, value ->
//            Arena.ofConfined()!!.use { arena ->
//                val valueSegment = arena.allocate(ADDRESS.byteSize() * value.size);
//
//                value.map { arena.allocateFrom(it) }.forEachIndexed { index, segment ->
//                    valueSegment.setAtIndex(ADDRESS, index.toLong(), segment);
//                }
//                key.makeCString {
//                    funcHandle.invoke(mmkv, it, valueSegment, value.size.toLong())
//                }
//            }
            useArena {
                val valueSegment = allocate(ADDRESS.byteSize() * value.size);
                value.map { allocateFrom(it) }.forEachIndexed { index, segment ->
                    valueSegment.setAtIndex(ADDRESS, index.toLong(), segment);
                }
                val keyPtr = allocateFrom(key)
                funcHandle.invoke(mmkv, keyPtr, valueSegment, value.size.toLong())
            }
        }
    }

    val mmkvc_remove: (MemorySegment, String) -> Boolean by lazy {
        val funcHandle = Linker.nativeLinker().downcallHandle(
            dll!!.find("mmkvc_remove").orElseThrow(),
            FunctionDescriptor.of(JAVA_BOOLEAN, ADDRESS, ADDRESS)
        )

        return@lazy { mmkv, key ->
            useArena {
                val keyPtr = allocateFrom(key)
                funcHandle.invoke(mmkv, keyPtr) as Boolean
            }
        }
    }

    val mmkvc_clear: (MemorySegment) -> Unit by lazy {
        val funcHandle = Linker.nativeLinker().downcallHandle(
            dll!!.find("mmkvc_clear").orElseThrow(),
            FunctionDescriptor.of(JAVA_BOOLEAN, ADDRESS)
        )

        return@lazy { mmkv ->
            funcHandle.invoke(mmkv)
        }
    }

    val mmkvc_destroy: (MemorySegment) -> Boolean by lazy {
        val funcHandle = Linker.nativeLinker().downcallHandle(
            dll!!.find("mmkvc_destroy").orElseThrow(),
            FunctionDescriptor.of(JAVA_BOOLEAN, ADDRESS)
        )

        return@lazy { mmkv ->
            funcHandle.invoke(mmkv) as Boolean
        }
    }

    val mmkvc_isAlive: (ptr: MemorySegment) -> Boolean by lazy {
        val funcHandle = Linker.nativeLinker().downcallHandle(
            dll!!.find("mmkvc_isAlive").orElseThrow(),
            FunctionDescriptor.of(JAVA_BOOLEAN, ADDRESS)
        )

        return@lazy { mmkv ->
            funcHandle.invoke(mmkv) as Boolean
        }
    }

    val mmkvc_size: (ptr: MemorySegment) -> Int by lazy {
        val funcHandle = Linker.nativeLinker().downcallHandle(
            dll!!.find("mmkvc_size").orElseThrow(),
            FunctionDescriptor.of(JAVA_INT, ADDRESS)
        )

        return@lazy { mmkv ->
            funcHandle.invoke(mmkv) as Int
        }
    }

    val mmkvc_allKeys: (ptr: MemorySegment) -> List<String> by lazy {
        val funcHandle = Linker.nativeLinker().downcallHandle(
            dll!!.find("mmkvc_allKeys").orElseThrow(),
            FunctionDescriptor.of(ADDRESS, ADDRESS)
        )
        val itemOffset = MMKVC_STRING_SET_RETURN_STRUCT.byteOffset(MemoryLayout.PathElement.groupElement("items"))
        val lenOffset = MMKVC_STRING_SET_RETURN_STRUCT.byteOffset(MemoryLayout.PathElement.groupElement("size"))


        return@lazy { mmkv ->
            val segment =
                (funcHandle.invoke(mmkv) as? MemorySegment)?.reinterpret(MMKVC_STRING_SET_RETURN_STRUCT.byteSize())
                    ?: error("mmkvc_allKeys return null")

            // 获取字符串数组指针和数组长度
            val len = segment.get(JAVA_LONG, lenOffset)
            val itemsPtr = segment.get(ADDRESS, itemOffset).reinterpret(len * ADDRESS.byteSize());

            val result = mutableListOf<String>()

            for (i in 0 until len) {
                val itemPtr = itemsPtr.getAtIndex(ADDRESS, i)
                result.add(itemPtr.reinterpret(Long.MAX_VALUE).getString(0))
                free(itemPtr)
            }
            free(itemsPtr)
            free(segment)
            // 返回结果List
            result
        }
    }

    val mmkvc_exists: (ptr: MemorySegment, key: String) -> Boolean by lazy {
        val funcHandle = Linker.nativeLinker().downcallHandle(
            dll!!.find("mmkvc_exists").orElseThrow(),
            FunctionDescriptor.of(JAVA_BOOLEAN, ADDRESS, ADDRESS)
        )

        return@lazy { mmkv, key ->
            useArena {
                val keyPtr = allocateFrom(key)
                funcHandle.invoke(mmkv, keyPtr) as Boolean
            }
        }
    }

    private val free by lazy {
        val func = with(Linker.nativeLinker()) {
            downcallHandle(
                defaultLookup().find("free").orElseThrow(),
                FunctionDescriptor.ofVoid(ADDRESS)
            )
        }
        return@lazy { message: MemorySegment ->
            func.invoke(message)
        }
    }
}
