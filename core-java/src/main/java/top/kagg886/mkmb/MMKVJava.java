package top.kagg886.mkmb;

public class MMKVJava {
    public static void initialize(String path,MMKVOptions options) {
        MMKV_jvmKt.initialize(MMKV.Companion, path, options);
    }

    public static MMKV mmkvWithID(String id) {
        return MMKV_jvmKt.mmkvWithID(MMKV.Companion, id);
    }

    public static MMKV defaultMMKV() {
        return MMKV_jvmKt.defaultMMKV(MMKV.Companion);
    }
}
