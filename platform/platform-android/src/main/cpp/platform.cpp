#include <string>
#include "jni.h"
#include <MMKV/MMKV.h>

using namespace std;

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
    jmethodID j2 = global->GetMethodID(global->FindClass("top/kagg886/mkmb/MMKVInternalLog"),
                                       "invoke", "(ILjava/lang/String;Ljava/lang/String;)V");

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
extern "C"
JNIEXPORT void JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1setString(JNIEnv *env, jclass clazz, jlong handle,
                                                  jstring key, jstring value) {
    MMKV *mmkv = (MMKV *) handle;
    mmkv->set(env->GetStringUTFChars(value, &JFALSE), env->GetStringUTFChars(key, &JFALSE));
}
extern "C"
JNIEXPORT void JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1setByteArray(JNIEnv *env, jclass clazz, jlong handle,
                                                     jstring key, jbyteArray value) {
    MMKV *mmkv = (MMKV *) handle;
    jbyte *bytes = env->GetByteArrayElements(value, &JFALSE);
    jsize size = env->GetArrayLength(value);

    auto buffer = new mmkv::MMBuffer(bytes, size);
    mmkv->set(buffer, env->GetStringUTFChars(key, &JFALSE));

}
extern "C"
JNIEXPORT void JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1setStringList(JNIEnv *env, jclass clazz, jlong handle,
                                                      jstring key, jobject value) {
    MMKV *mmkv = (MMKV *) handle;
    vector<string> string;

    auto listClass = env->FindClass("java/util/List");
    jmethodID getMethod = env->GetMethodID(listClass, "get", "(I)Ljava/lang/Object;");
    jmethodID sizeMethod = env->GetMethodID(listClass, "size", "()I");

    jint size = env->CallIntMethod(value, sizeMethod);
    for (int i = 0; i < size; i++) {
        jstring item = (jstring) env->CallObjectMethod(value, getMethod, i);
        string.push_back(env->GetStringUTFChars(item, &JFALSE));
    }
    mmkv->set(string, env->GetStringUTFChars(key, &JFALSE));
}
extern "C"
JNIEXPORT void JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1setBoolean(JNIEnv *env, jclass clazz, jlong handle,
                                                   jstring key, jboolean value) {
    MMKV *mmkv = (MMKV *) handle;
    mmkv->set(value, env->GetStringUTFChars(key, &JFALSE));
}
extern "C"
JNIEXPORT void JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1setLong(JNIEnv *env, jclass clazz, jlong handle,
                                                jstring key, jlong value) {
    MMKV *mmkv = (MMKV *) handle;
    mmkv->set(value, env->GetStringUTFChars(key, &JFALSE));
}
extern "C"
JNIEXPORT void JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1setFloat(JNIEnv *env, jclass clazz, jlong handle,
                                                 jstring key, jfloat value) {
    MMKV *mmkv = (MMKV *) handle;
    mmkv->set(value, env->GetStringUTFChars(key, &JFALSE));
}
extern "C"
JNIEXPORT void JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1setDouble(JNIEnv *env, jclass clazz, jlong handle,
                                                  jstring key, jdouble value) {
    MMKV *mmkv = (MMKV *) handle;
    mmkv->set(value, env->GetStringUTFChars(key, &JFALSE));
}
extern "C"
JNIEXPORT jstring JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1getString(JNIEnv *env, jclass clazz, jlong handle,
                                                  jstring key) {
    MMKV *mmkv = (MMKV *) handle;
    string result;
    mmkv->getString(env->GetStringUTFChars(key, &JFALSE), result);
    return env->NewStringUTF(result.c_str());
}
extern "C"
JNIEXPORT jbyteArray JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1getByteArray(JNIEnv *env, jclass clazz, jlong handle,
                                                     jstring key) {
    MMKV *mmkv = (MMKV *) handle;
    mmkv::MMBuffer buffer;
    mmkv->getBytes(env->GetStringUTFChars(key, &JFALSE), buffer);

    jbyteArray result = env->NewByteArray(buffer.length());
    env->SetByteArrayRegion(result, 0, buffer.length(), (jbyte *) buffer.getPtr());
    return result;
}
extern "C"
JNIEXPORT jobject JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1getStringList(JNIEnv *env, jclass clazz, jlong handle,
                                                      jstring key) {
    MMKV *mmkv = (MMKV *) handle;
    vector<string> result;
    mmkv->getVector(env->GetStringUTFChars(key, &JFALSE), result);

    jclass listClass = env->FindClass("java/util/ArrayList");
    jmethodID listConstructor = env->GetMethodID(listClass, "<init>", "()V");
    jobject list = env->NewObject(listClass, listConstructor);
    jmethodID addMethod = env->GetMethodID(listClass, "add", "(Ljava/lang/Object;)Z");

    for (auto item: result) {
        jstring itemString = env->NewStringUTF(item.c_str());
        env->CallBooleanMethod(list, addMethod, itemString);
    }

    return list;
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1getBoolean(JNIEnv *env, jclass clazz, jlong handle,
                                                   jstring key) {
    MMKV *mmkv = (MMKV *) handle;
    return (jboolean) mmkv->getBool(env->GetStringUTFChars(key, &JFALSE));
}
extern "C"
JNIEXPORT jlong JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1getLong(JNIEnv *env, jclass clazz, jlong handle,
                                                jstring key) {
    MMKV *mmkv = (MMKV *) handle;
    return (jlong) mmkv->getInt64(env->GetStringUTFChars(key, &JFALSE));
}
extern "C"
JNIEXPORT jfloat JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1getFloat(JNIEnv *env, jclass clazz, jlong handle,
                                                 jstring key) {
    MMKV *mmkv = (MMKV *) handle;
    return (jfloat) mmkv->getFloat(env->GetStringUTFChars(key, &JFALSE));
}
extern "C"
JNIEXPORT jdouble JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1getDouble(JNIEnv *env, jclass clazz, jlong handle,
                                                  jstring key) {

    MMKV *mmkv = (MMKV *) handle;
    return (jdouble) mmkv->getFloat(env->GetStringUTFChars(key, &JFALSE));
}
extern "C"
JNIEXPORT void JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1remove(JNIEnv *env, jclass clazz, jlong handle,
                                               jstring key) {
    MMKV *mmkv = (MMKV *) handle;
    mmkv->removeValueForKey(env->GetStringUTFChars(key, &JFALSE));
}



extern "C"
JNIEXPORT void JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1clear(JNIEnv *env, jclass clazz, jlong handle) {
    MMKV *mmkv = (MMKV *) handle;
    mmkv->clearAll();
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1destroy(JNIEnv *env, jclass clazz, jlong handle) {
    MMKV *mmkv = (MMKV *) handle;
    return MMKV::removeStorage(mmkv->mmapID(), (&mmkv->getRootDir()));
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1isAlive(JNIEnv *env, jclass clazz, jlong handle) {
    MMKV *mmkv = (MMKV *) handle;
    return MMKV::isFileValid(mmkv->mmapID(), (&mmkv->getRootDir()));
}
extern "C"
JNIEXPORT jint JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1size(JNIEnv *env, jclass clazz, jlong handle) {
    MMKV *mmkv = (MMKV *) handle;
    return mmkv->count();
}
extern "C"
JNIEXPORT jobject JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1allKeys(JNIEnv *env, jclass clazz, jlong handle) {
    MMKV *mmkv = (MMKV *) handle;
    vector<string> result;

    jclass listClass = env->FindClass("java/util/ArrayList");
    jmethodID listConstructor = env->GetMethodID(listClass, "<init>", "()V");
    jobject list = env->NewObject(listClass, listConstructor);
    jmethodID addMethod = env->GetMethodID(listClass, "add", "(Ljava/lang/Object;)Z");

    for (auto item: mmkv->allKeys()) {
        jstring itemString = env->NewStringUTF(item.c_str());
        env->CallBooleanMethod(list, addMethod, itemString);
    }
    return list;
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1exists(JNIEnv *env, jclass clazz, jlong handle,
                                               jstring key) {
    MMKV *mmkv = (MMKV *) handle;
    return mmkv->containsKey(env->GetStringUTFChars(key, &JFALSE));
}