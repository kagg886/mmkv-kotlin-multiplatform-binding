#include <string>
#include "jni.h"
#include <MMKV/MMKV.h>

static jobject logHandler;
static JNIEnv *global;

static jboolean JFALSE = 0;
static jboolean JTRUE = 1;

static void LogCallback(
        MMKVLogLevel level,
        const char *file,
        int line,
        const char *function,
        MMKVLog_t message
) {
    jmethodID j2 = global->GetMethodID(global->FindClass("top/kagg886/mkmb/MMKVInternalLog"),"invoke", "(ILjava/lang/String;Ljava/lang/String;)V");

    jstring jFile = global->NewStringUTF(file);
    jstring jMessage = global->NewStringUTF(message.c_str());

    global->CallVoidMethod(logHandler, j2, (jint) level, jFile, jMessage);

}

extern "C"
JNIEXPORT void JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1init(
        JNIEnv *env,
        jclass clazz,
        jstring path,
        jint level,
        jobject callback
) {
    logHandler = env->NewGlobalRef(callback);
    global = env;
    MMKV::initializeMMKV(
            env->GetStringUTFChars(path, &JFALSE),
            (MMKVLogLevel) level,
            LogCallback
    );
}

extern "C"
JNIEXPORT jlong JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1defaultMMKV(JNIEnv *env, jclass clazz) {
    return (jlong) MMKV::defaultMMKV();
}

extern "C"
JNIEXPORT jlong JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1mmkvWithID(JNIEnv *env, jclass clazz, jstring id) {
    return (jlong) MMKV::mmkvWithID(env->GetStringUTFChars(id, &JFALSE));
}
extern "C"
JNIEXPORT void JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1setInt(JNIEnv *env, jclass clazz, jlong handle, jstring key,
                                               jint value) {
    MMKV *mmkv = (MMKV *) handle;
    mmkv->set((int) value, env->GetStringUTFChars(key, &JFALSE));
}
extern "C"
JNIEXPORT jint JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1getInt(JNIEnv *env, jclass clazz, jlong handle,
                                               jstring key) {
    MMKV *mmkv = (MMKV *) handle;
    return (jint) mmkv->getInt32(
            env->GetStringUTFChars(key, &JFALSE));
}