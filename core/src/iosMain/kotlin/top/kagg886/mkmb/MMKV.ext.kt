package top.kagg886.mkmb

import kotlinx.cinterop.BetaInteropApi
import platform.darwin.NSObject
import kotlinx.cinterop.ObjCClass

@OptIn(BetaInteropApi::class)
fun MMKV.getNSCoding(key:String, clazz: ObjCClass): Any? {
    val apple = this as AppleMMKV

    return NativeMMKVImpl.getNSCoding(apple.handle,key,clazz)
}

fun MMKV.set(key:String,value:NSObject?,expire:Int = 0) {
    val apple = this as AppleMMKV
    NativeMMKVImpl.setNSCoding(apple.handle,key,value,expire)
}
