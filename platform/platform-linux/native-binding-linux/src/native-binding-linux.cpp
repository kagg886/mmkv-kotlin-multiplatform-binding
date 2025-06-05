#include <string>
#include "MMKV/MMKV.h"

static char* stringToChar(const std::string& ptr) {
    // get the memory in the C++ class and place it 'C like' for Java to read
    // and recycle.
    auto len = ptr.size() + 1;
    auto rtn = (char*)malloc(len);

    if (rtn == nullptr) {
        return nullptr;
    }

    memcpy(rtn, ptr.c_str(), len);
    return rtn;
}

typedef void(Logger)(int, const char*, const char*);

static Logger* g_logger = nullptr;
static void LogCallback(MMKVLogLevel level,
                        const char* file,
                        int line,
                        const char* function,
                        MMKVLog_t message) {
    if (g_logger) {
        g_logger(level, file, message.c_str());
    }
}

extern "C" void mmkvc_init(char* path, int level, Logger* logger) {
    g_logger = logger;
    MMKV::initializeMMKV(path, (MMKVLogLevel)level, LogCallback);
}

extern "C" MMKV* mmkvc_defaultMMKV() {
    auto mmkv = MMKV::defaultMMKV();
    mmkv->enableAutoKeyExpire(MMKV::ExpireNever);
    return mmkv;
}

extern "C" MMKV* mmkvc_mmkvWithID(char* id) {
    auto mmkv = MMKV::mmkvWithID();
    mmkv->enableAutoKeyExpire(MMKV::ExpireNever);
    return mmkv;
}


extern "C" int mmkvc_getInt(MMKV* mmkv, const char* key) {
    return mmkv->getInt32(key);
}

extern "C" void mmkvc_setInt(MMKV* mmkv, const char* key, int value) {
    mmkv->set(value, key);
}

extern "C" const char* mmkvc_getString(MMKV* mmkv, const char* key) {
    std::string ptr;
    mmkv->getString(key, ptr);

    return stringToChar(ptr);
}

extern "C" void mmkvc_setString(MMKV* mmkv, const char* key, char* value) {
    mmkv->set(value, key);
}

extern "C" float mmkvc_getFloat(MMKV* mmkv, const char* key) {
    return mmkv->getFloat(key);
}

extern "C" void mmkvc_setFloat(MMKV* mmkv, const char* key, float value) {
    mmkv->set(value, key);
}

extern "C" long mmkvc_getLong(MMKV* mmkv, const char* key) {
    return mmkv->getInt64(key);
}

extern "C" void mmkvc_setLong(MMKV* mmkv, const char* key, int64_t value) {
    mmkv->set(value, key);
}

extern "C" double mmkvc_getDouble(MMKV* mmkv, const char* key) {
    return mmkv->getDouble(key);
}

extern "C" void mmkvc_setDouble(MMKV* mmkv, const char* key, double value) {
    mmkv->set(value, key);
}

extern "C" bool mmkvc_getBool(MMKV* mmkv, const char* key) {
    return mmkv->getBool(key);
}

extern "C" void mmkvc_setBool(MMKV* mmkv, const char* key, bool value) {
    mmkv->set(value, key);
}

extern "C" uint8_t* mmkvc_getByteArray(MMKV* mmkv,
                                       const char* key,
                                       size_t* size) {
    auto buffer = mmkv->getBytes(key);
    auto rtn = (uint8_t*)malloc(buffer.length());
    *size = buffer.length();
    memcpy(rtn, buffer.getPtr(), buffer.length());
    return rtn;
}

extern "C" void mmkvc_setByteArray(MMKV* mmkv,
                                   const char* key,
                                   uint8_t* value,
                                   size_t size) {
    auto buffer = mmkv::MMBuffer(value, size, mmkv::MMBufferNoCopy);
    mmkv->set(buffer, key);
}

struct MMKVCStringListReturn {
    char** items;
    size_t size;
};

extern "C" MMKVCStringListReturn* mmkvc_getStringList(MMKV* mmkv,
                                                      const char* key) {
    std::vector<std::string> vector;
    mmkv->getVector(key, vector);

    auto rtn = (MMKVCStringListReturn*)malloc(sizeof(MMKVCStringListReturn));

    if (rtn == nullptr) {
        return nullptr;
    }

    rtn->size = 0;
    rtn->items = (char**)malloc(sizeof(char**) * vector.size());
    if (rtn->items == nullptr) {
        return nullptr;
    }
    for (std::string str : vector) {
        (rtn->items)[rtn->size] = stringToChar(str);
        (rtn->size) += 1;
    }
    return rtn;
}

extern "C" void mmkvc_setStringList(MMKV* mmkv,
                                    const char* key,
                                    const char** value,
                                    size_t size) {
    std::vector<std::string> vector;

    for (size_t i = 0; i < size; i++) {
        std::string tmp(value[i], strlen(value[i]));
        vector.push_back(tmp);
    }

    mmkv->set(vector, key);
}

extern "C" bool mmkvc_remove(MMKV* mmkv, const char* key) {
    return mmkv->removeValueForKey(key);
}

extern "C" void mmkvc_clear(MMKV* mmkv) {
    mmkv->clearAll();
}

extern "C" bool mmkvc_destroy(MMKV* mmkv) {
    return MMKV::removeStorage(mmkv->mmapID(), (&mmkv->getRootDir()));
}

extern "C" bool mmkvc_isAlive(MMKV* mmkv) {
    return MMKV::isFileValid(mmkv->mmapID(), (&mmkv->getRootDir()));
}

extern "C" int mmkvc_size(MMKV* mmkv) {
    return mmkv->count();
}

extern "C" MMKVCStringListReturn* mmkvc_allKeys(MMKV* mmkv) {
    std::vector<std::string> vector = mmkv->allKeys();

    auto rtn = (MMKVCStringListReturn*)malloc(sizeof(MMKVCStringListReturn));

    if (rtn == nullptr) {
        return nullptr;
    }

    rtn->size = 0;
    rtn->items = (char**)malloc(sizeof(char**) * vector.size());
    if (rtn->items == nullptr) {
        return nullptr;
    }
    for (std::string str : vector) {
        (rtn->items)[rtn->size] = stringToChar(str);
        (rtn->size) += 1;
    }
    return rtn;
}

extern "C" int mmkvc_exists(MMKV* mmkv, char* key) {
    return mmkv->containsKey(key);
}
