#include <MMKV/MMKV.h>
#include <string>
#include "android/log.h"
#include "jni.h"

using namespace std;

#define jstring2cppstring(env, jstr)                          \
    ([&](JNIEnv* e, jstring js) -> std::string {              \
        if (js == nullptr)                                    \
            return "";                                        \
        const char* cstr = e->GetStringUTFChars(js, nullptr); \
        if (cstr == nullptr)                                  \
            return "";                                        \
        std::string result(cstr);                             \
        e->ReleaseStringUTFChars(js, cstr);                   \
        return result;                                        \
    })(env, jstr)

static jobject logHandler;

JavaVM* jvm = nullptr;

JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env = nullptr;
    if (vm->GetEnv((void**)&env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
    jvm = vm;
    return JNI_VERSION_1_6;
}

void GetJNIEnv(JNIEnv*& env) {
    int status = jvm->GetEnv((void**)&env, JNI_VERSION_1_6);
    // 获取当前native线程是否有没有被附加到jvm环境中
    switch (status) {
        case JNI_EDETACHED: {
            jvm->AttachCurrentThread(&env, nullptr);
            break;
        }

        case JNI_OK: {
            break;
        }

        case JNI_EVERSION: {
            break;
        }

        default: {
            break;
        }
    }
}

static void LogCallback(MMKVLogLevel level,
                        const char* file,
                        int line,
                        const char* function,
                        MMKVLog_t message) {
    JNIEnv* global = nullptr;
    GetJNIEnv(global);
    jmethodID j2 = global->GetMethodID(
            global->FindClass("top/kagg886/mkmb/MMKVInternalLog"), "invoke",
            "(ILjava/lang/String;Ljava/lang/String;)V");

    jstring jFile = global->NewStringUTF(file);
    jstring jMessage = global->NewStringUTF(message.c_str());

    global->CallVoidMethod(logHandler, j2, (jint)level, jFile, jMessage);
}

extern "C" JNIEXPORT void JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1init(JNIEnv* env,
                                             jclass clazz,
                                             jstring path,
                                             jint level,
                                             jobject callback) {
    logHandler = env->NewGlobalRef(callback);

    auto rootDir = jstring2cppstring(env, path);
    MMKV::initializeMMKV(rootDir, (MMKVLogLevel)level, LogCallback);
}

extern "C" JNIEXPORT jlong JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1defaultMMKV(JNIEnv* env, jclass clazz, jint mode, jstring cryptKey) {
    MMKV* mmkv = nullptr;
    if (cryptKey != NULL) {
        auto cryptKeyStr = jstring2cppstring(env, cryptKey);
        if (!cryptKeyStr.empty()) {
            mmkv = MMKV::defaultMMKV((MMKVMode)mode, &cryptKeyStr);
        } else {
            mmkv = MMKV::defaultMMKV((MMKVMode)mode, nullptr);
        }
    } else {
        mmkv = MMKV::defaultMMKV((MMKVMode)mode, nullptr);
    }
    mmkv->enableAutoKeyExpire(MMKV::ExpireNever);
    return (jlong)mmkv;
}

extern "C" JNIEXPORT jlong JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1mmkvWithID(JNIEnv* env,
                                                   jclass clazz,
                                                   jstring id,
                                                   jint mode,
                                                   jstring cryptKey) {
    auto mmapIDStr = jstring2cppstring(env, id);
    MMKV* mmkv = nullptr;
    if (cryptKey != NULL) {
        auto cryptKeyStr = jstring2cppstring(env, cryptKey);
        if (!cryptKeyStr.empty()) {
            mmkv = MMKV::mmkvWithID(mmapIDStr, mmkv::DEFAULT_MMAP_SIZE, (MMKVMode)mode, &cryptKeyStr);
        } else {
            mmkv = MMKV::mmkvWithID(mmapIDStr, mmkv::DEFAULT_MMAP_SIZE, (MMKVMode)mode, nullptr);
        }
    } else {
        mmkv = MMKV::mmkvWithID(mmapIDStr, mmkv::DEFAULT_MMAP_SIZE, (MMKVMode)mode, nullptr);
    }
    mmkv->enableAutoKeyExpire(MMKV::ExpireNever);
    return (jlong)mmkv;
}

extern "C" JNIEXPORT void JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1setInt(JNIEnv* env,
                                               jclass clazz,
                                               jlong handle,
                                               jstring key,
                                               jint value,
                                               jint expire) {
    MMKV* mmkv = (MMKV*)handle;
    auto keyStr = jstring2cppstring(env, key);
    mmkv->set((int)value, keyStr, expire);
}

extern "C" JNIEXPORT jint JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1getInt(JNIEnv* env,
                                               jclass clazz,
                                               jlong handle,
                                               jstring key) {
    MMKV* mmkv = (MMKV*)handle;
    auto keyStr = jstring2cppstring(env, key);
    return (jint)mmkv->getInt32(keyStr);
}

extern "C" JNIEXPORT void JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1setString(JNIEnv* env,
                                                  jclass clazz,
                                                  jlong handle,
                                                  jstring key,
                                                  jstring value,
                                                  jint expire) {
    MMKV* mmkv = (MMKV*)handle;

    auto keys = jstring2cppstring(env, key);
    auto values = jstring2cppstring(env, value);

    mmkv->set(values, keys, expire);
}

extern "C" JNIEXPORT void JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1setByteArray(JNIEnv* env,
                                                     jclass clazz,
                                                     jlong handle,
                                                     jstring key,
                                                     jbyteArray value,
                                                     jint expire) {
    MMKV* mmkv = (MMKV*)handle;
    jbyte* bytes = env->GetByteArrayElements(value, nullptr);
    jsize size = env->GetArrayLength(value);

    mmkv::MMBuffer buffer(bytes, size);

    auto keyStr = jstring2cppstring(env, key);

    mmkv->set(buffer, keyStr, expire);

    env->ReleaseByteArrayElements(value, bytes, 0);
}

extern "C" JNIEXPORT void JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1setStringList(JNIEnv* env,
                                                      jclass clazz,
                                                      jlong handle,
                                                      jstring key,
                                                      jobject value,
                                                      jint expire) {
    MMKV* mmkv = (MMKV*)handle;
    vector<string> string;

    auto listClass = env->FindClass("java/util/List");
    jmethodID getMethod =
            env->GetMethodID(listClass, "get", "(I)Ljava/lang/Object;");
    jmethodID sizeMethod = env->GetMethodID(listClass, "size", "()I");

    jint size = env->CallIntMethod(value, sizeMethod);
    for (int i = 0; i < size; i++) {
        jstring item = (jstring)env->CallObjectMethod(value, getMethod, i);
        auto itemStr = jstring2cppstring(env, item);
        string.push_back(itemStr);
    }
    auto keyStr = jstring2cppstring(env, key);
    mmkv->set(string, keyStr, expire);
}

extern "C" JNIEXPORT void JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1setBoolean(JNIEnv* env,
                                                   jclass clazz,
                                                   jlong handle,
                                                   jstring key,
                                                   jboolean value,
                                                   jint expire) {
    MMKV* mmkv = (MMKV*)handle;
    auto keyStr = jstring2cppstring(env, key);
    mmkv->set(value == JNI_TRUE, keyStr,expire);
}

extern "C" JNIEXPORT void JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1setLong(JNIEnv* env,
                                                jclass clazz,
                                                jlong handle,
                                                jstring key,
                                                jlong value,
                                                jint expire) {
    MMKV* mmkv = (MMKV*)handle;
    auto keyStr = jstring2cppstring(env, key);
    mmkv->set(value, keyStr, expire);
}

extern "C" JNIEXPORT void JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1setFloat(JNIEnv* env,
                                                 jclass clazz,
                                                 jlong handle,
                                                 jstring key,
                                                 jfloat value,
                                                 jint expire) {
    MMKV* mmkv = (MMKV*)handle;
    auto keyStr = jstring2cppstring(env, key);
    mmkv->set(value, keyStr, expire);
}

extern "C" JNIEXPORT void JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1setDouble(JNIEnv* env,
                                                  jclass clazz,
                                                  jlong handle,
                                                  jstring key,
                                                  jdouble value,
                                                  jint expire) {
    MMKV* mmkv = (MMKV*)handle;
    auto keyStr = jstring2cppstring(env, key);
    mmkv->set(value, keyStr, expire);
}

extern "C" JNIEXPORT jstring JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1getString(JNIEnv* env,
                                                  jclass clazz,
                                                  jlong handle,
                                                  jstring key) {
    MMKV* mmkv = (MMKV*)handle;
    string result;
    auto keyStr = jstring2cppstring(env, key);
    mmkv->getString(keyStr, result);
    return env->NewStringUTF(result.c_str());
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1getByteArray(JNIEnv* env,
                                                     jclass clazz,
                                                     jlong handle,
                                                     jstring key) {
    MMKV* mmkv = (MMKV*)handle;
    mmkv::MMBuffer buffer;
    auto keyStr = jstring2cppstring(env, key);
    mmkv->getBytes(keyStr, buffer);

    jbyteArray result = env->NewByteArray(buffer.length());
    env->SetByteArrayRegion(result, 0, buffer.length(),
                            (jbyte*)buffer.getPtr());
    return result;
}

extern "C" JNIEXPORT jobject JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1getStringList(JNIEnv* env,
                                                      jclass clazz,
                                                      jlong handle,
                                                      jstring key) {
    MMKV* mmkv = (MMKV*)handle;
    vector<string> result;
    auto keyStr = jstring2cppstring(env, key);
    mmkv->getVector(keyStr, result);

    jclass listClass = env->FindClass("java/util/ArrayList");
    jmethodID listConstructor = env->GetMethodID(listClass, "<init>", "()V");
    jobject list = env->NewObject(listClass, listConstructor);
    jmethodID addMethod =
            env->GetMethodID(listClass, "add", "(Ljava/lang/Object;)Z");

    for (auto item : result) {
        jstring itemString = env->NewStringUTF(item.c_str());
        env->CallBooleanMethod(list, addMethod, itemString);
    }

    return list;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1getBoolean(JNIEnv* env,
                                                   jclass clazz,
                                                   jlong handle,
                                                   jstring key) {
    MMKV* mmkv = (MMKV*)handle;
    auto keys = jstring2cppstring(env, key);
    return (jboolean)mmkv->getBool(keys);
}

extern "C" JNIEXPORT jlong JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1getLong(JNIEnv* env,
                                                jclass clazz,
                                                jlong handle,
                                                jstring key) {
    MMKV* mmkv = (MMKV*)handle;
    auto keys = jstring2cppstring(env, key);
    return (jlong)mmkv->getInt64(keys);
}

extern "C" JNIEXPORT jfloat JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1getFloat(JNIEnv* env,
                                                 jclass clazz,
                                                 jlong handle,
                                                 jstring key) {
    MMKV* mmkv = (MMKV*)handle;
    auto keys = jstring2cppstring(env, key);
    return (jfloat)mmkv->getFloat(keys);
}

extern "C" JNIEXPORT jdouble JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1getDouble(JNIEnv* env,
                                                  jclass clazz,
                                                  jlong handle,
                                                  jstring key) {
    MMKV* mmkv = (MMKV*)handle;
    auto keys = jstring2cppstring(env, key);
    return (jdouble)mmkv->getDouble(keys);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1remove(JNIEnv* env,
                                               jclass clazz,
                                               jlong handle,
                                               jstring key) {
    MMKV* mmkv = (MMKV*)handle;
    auto keys = jstring2cppstring(env, key);
    return mmkv->removeValueForKey(keys);
}

extern "C" JNIEXPORT void JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1clear(JNIEnv* env,
                                              jclass clazz,
                                              jlong handle) {
    MMKV* mmkv = (MMKV*)handle;
    mmkv->clearAll();
}

extern "C" JNIEXPORT jboolean JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1destroy(JNIEnv* env,
                                                jclass clazz,
                                                jlong handle) {
    MMKV* mmkv = (MMKV*)handle;
    return MMKV::removeStorage(mmkv->mmapID(), (&mmkv->getRootDir()));
}

extern "C" JNIEXPORT jboolean JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1isAlive(JNIEnv* env,
                                                jclass clazz,
                                                jlong handle) {
    MMKV* mmkv = (MMKV*)handle;
    return MMKV::checkExist(mmkv->mmapID(), (&mmkv->getRootDir()));
}

extern "C" JNIEXPORT jint JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1size(JNIEnv* env,
                                             jclass clazz,
                                             jlong handle) {
    MMKV* mmkv = (MMKV*)handle;
    return mmkv->count();
}

extern "C" JNIEXPORT jobject JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1allKeys(JNIEnv* env,
                                                jclass clazz,
                                                jlong handle) {
    MMKV* mmkv = (MMKV*)handle;
    vector<string> result;

    jclass listClass = env->FindClass("java/util/ArrayList");
    jmethodID listConstructor = env->GetMethodID(listClass, "<init>", "()V");
    jobject list = env->NewObject(listClass, listConstructor);
    jmethodID addMethod =
            env->GetMethodID(listClass, "add", "(Ljava/lang/Object;)Z");

    for (auto item : mmkv->allKeys()) {
        jstring itemString = env->NewStringUTF(item.c_str());
        env->CallBooleanMethod(list, addMethod, itemString);
    }
    return list;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_top_kagg886_mkmb_NativeMMKV_mmkvc_1exists(JNIEnv* env,
                                               jclass clazz,
                                               jlong handle,
                                               jstring key) {
    MMKV* mmkv = (MMKV*)handle;
    auto keys = jstring2cppstring(env, key);
    return mmkv->containsKey(keys);
}
