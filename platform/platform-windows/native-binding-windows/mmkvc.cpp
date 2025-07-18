﻿#include "MMKV/MMKV.h"

static std::wstring stringToWString(const std::string& str) {
    const char* mbStr = str.c_str();

    // 获取所需的大小
    size_t size;
    mbstowcs_s(&size, nullptr, 0, mbStr, 0);

    // 分配适当大小的wstring
    std::wstring wStr(size - 1, L'\0');  // size包含null终止符，所以要减1

    // 执行实际转换
    mbstowcs_s(&size, &wStr[0], size, mbStr, _TRUNCATE);

    return wStr;  // 原函数缺少返回语句
}

static char* stringToChar(const std::string& ptr) {
    //get the memory in the C++ class and place it 'C like' for Java to read and recycle.
    auto len = ptr.size() + 1;
    auto rtn = (char*)malloc(len);

    if (rtn == nullptr) {
        return nullptr;
    }

    memcpy(rtn, ptr.c_str(), len);
    return rtn;
}

typedef void (Logger)(int,const char*,const char*);

static Logger* g_logger = nullptr;
static void LogCallback(MMKVLogLevel level, const char* file, int line, const char* function, MMKVLog_t message) {
    if (g_logger) {
        g_logger(level,file, message.c_str());
    }
}

extern "C" __declspec(dllexport) void mmkvc_init(char* path,int level,Logger* logger) {
    g_logger = logger;
    MMKV::initializeMMKV(stringToWString(path), (MMKVLogLevel)level,LogCallback);
}

extern "C" __declspec(dllexport) MMKV* mmkvc_defaultMMKV(char* cryptKey) {
    MMKV* mmkv = nullptr;
    if (cryptKey != nullptr && strlen(cryptKey) > 0) {
        std::string crypt(cryptKey);
        mmkv = MMKV::defaultMMKV(MMKV_SINGLE_PROCESS, &crypt);
    } else {
        mmkv = MMKV::defaultMMKV();
    }
    mmkv->enableAutoKeyExpire(MMKV::ExpireNever);
    return mmkv;
}

extern "C" __declspec(dllexport) MMKV* mmkvc_mmkvWithID(char* id, char* cryptKey) {
    MMKV* mmkv = nullptr;
    if (cryptKey != nullptr && strlen(cryptKey) > 0) {
        std::string crypt(cryptKey);
        mmkv = MMKV::mmkvWithID(id, MMKV_SINGLE_PROCESS, &crypt);
    } else {
        mmkv = MMKV::mmkvWithID(id);
    }
    mmkv->enableAutoKeyExpire(MMKV::ExpireNever);
    return mmkv;
}

extern "C" __declspec(dllexport) int mmkvc_getInt(MMKV* mmkv,const char* key) {
   return mmkv->getInt32(key);
}

extern "C" __declspec(dllexport) void mmkvc_setInt(MMKV* mmkv,const char* key,int value,int expire) {
    mmkv->set(value,key,expire);
}

extern "C" __declspec(dllexport) const char* mmkvc_getString(MMKV* mmkv,const char* key) {
    std::string ptr;
    mmkv->getString(key,ptr);

    return stringToChar(ptr);
}

extern "C" __declspec(dllexport) void mmkvc_setString(MMKV* mmkv, const char* key, char* value,int expire) {
    auto str = std::string(value);
    mmkv->set(str, key,expire);
}

extern "C" __declspec(dllexport) float mmkvc_getFloat(MMKV* mmkv, const char* key) {
    return mmkv->getFloat(key);
}

extern "C" __declspec(dllexport) void mmkvc_setFloat(MMKV* mmkv, const char* key, float value,int expire) {
    mmkv->set(value, key,expire);
}

extern "C" __declspec(dllexport) long mmkvc_getLong(MMKV* mmkv, const char* key) {
    return mmkv->getInt64(key);
}

extern "C" __declspec(dllexport) void mmkvc_setLong(MMKV* mmkv, const char* key, int64_t value,int expire) {
    mmkv->set(value, key,expire);
}

extern "C" __declspec(dllexport) double mmkvc_getDouble(MMKV* mmkv, const char* key) {
    return mmkv->getDouble(key);
}

extern "C" __declspec(dllexport) void mmkvc_setDouble(MMKV* mmkv, const char* key, double value,int expire) {
    mmkv->set(value, key,expire);
}

extern "C" __declspec(dllexport) bool mmkvc_getBool(MMKV* mmkv, const char* key) {
    return mmkv->getBool(key);
}

extern "C" __declspec(dllexport) void mmkvc_setBool(MMKV* mmkv, const char* key, bool value,int expire) {
    mmkv->set(value, key,expire);
}

extern "C" __declspec(dllexport) uint8_t* mmkvc_getByteArray(MMKV* mmkv,
    const char* key,
    size_t* size) {
    auto buffer = mmkv->getBytes(key);
    auto rtn = (uint8_t*)malloc(buffer.length());
    *size = buffer.length();
    memcpy(rtn, buffer.getPtr(), buffer.length());
    return rtn;
}

extern "C" __declspec(dllexport) void mmkvc_setByteArray(MMKV* mmkv,const char* key,uint8_t* value,size_t size,int expire) {
    auto buffer = mmkv::MMBuffer(value, size, mmkv::MMBufferNoCopy);
    mmkv->set(buffer, key, expire);
}

struct MMKVCStringListReturn {
    char** items;
    size_t size;
};

extern "C" __declspec(dllexport) MMKVCStringListReturn* mmkvc_getStringList(MMKV* mmkv, const char* key) {
    std::vector<std::string> vector;
    mmkv->getVector(key,vector);

    auto rtn = (MMKVCStringListReturn*) malloc(sizeof(MMKVCStringListReturn));

    if (rtn == nullptr) {
        return nullptr;
    }

    rtn->size = 0;
    rtn->items = (char**) malloc(sizeof(char**) * vector.size());
    if (rtn->items == nullptr) {
        return nullptr;
    }
    for (std::string str : vector) {
        (rtn->items)[rtn->size] = stringToChar(str);
        (rtn->size) += 1;
    }
    return rtn;
}

extern "C" __declspec(dllexport) void mmkvc_setStringList(MMKV* mmkv, const char* key,const char** value,size_t size,int expire) {
    std::vector<std::string> vector;

    for (size_t i = 0; i < size; i++) {
        std::string tmp(value[i],strlen(value[i]));
        vector.push_back(tmp);
    }

    mmkv->set(vector, key,expire);
}

extern "C" __declspec(dllexport) bool mmkvc_remove(MMKV* mmkv, const char* key) {
    return mmkv->removeValueForKey(key);
}

extern "C" __declspec(dllexport) void mmkvc_clear(MMKV* mmkv) {
    mmkv->clearAll();
}

extern "C" __declspec(dllexport) bool mmkvc_destroy(MMKV* mmkv) {
    return MMKV::removeStorage(mmkv->mmapID(), (&mmkv->getRootDir()));
}

extern "C" __declspec(dllexport) bool mmkvc_isAlive(MMKV* mmkv) {
    return MMKV::isFileValid(mmkv->mmapID(), (&mmkv->getRootDir()));
}


extern "C" __declspec(dllexport) int mmkvc_size(MMKV* mmkv) {
    return mmkv->count();
}


extern "C" __declspec(dllexport) MMKVCStringListReturn* mmkvc_allKeys(MMKV* mmkv) {
    std::vector<std::string> vector = mmkv->allKeys();

    auto rtn = (MMKVCStringListReturn*) malloc(sizeof(MMKVCStringListReturn));

    if (rtn == nullptr) {
        return nullptr;
    }

    rtn->size = 0;
    rtn->items = (char**) malloc(sizeof(char**) * vector.size());
    if (rtn->items == nullptr) {
        return nullptr;
    }
    for (std::string str : vector) {
        (rtn->items)[rtn->size] = stringToChar(str);
        (rtn->size) += 1;
    }
    return rtn;
}

extern "C" __declspec(dllexport) int mmkvc_exists(MMKV* mmkv,char* key) {
    return mmkv->containsKey(key);
}
