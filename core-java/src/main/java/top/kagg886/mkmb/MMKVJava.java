package top.kagg886.mkmb;

public class MMKVJava {
    public static void initialize(String path, MMKVOptions options) {
        MMKV_jvmKt.initialize(MMKV.Companion, path, options);
    }

    public static MMKV mmkvWithID(String id) {
        return MMKV_jvmKt.mmkvWithID(MMKV.Companion, id, MMKVMode.SINGLE_PROCESS, null);
    }

    public static MMKV mmkvWithID(String id, MMKVMode mode, String cryptKey) {
        return MMKV_jvmKt.mmkvWithID(MMKV.Companion, id, mode, cryptKey);
    }

    public static MMKV mmkvWithID(String id, MMKVMode mode) {
        return MMKV_jvmKt.mmkvWithID(MMKV.Companion, id, mode, null);
    }

    public static MMKV mmkvWithID(String id, String cryptKey) {
        return MMKV_jvmKt.mmkvWithID(MMKV.Companion, id, MMKVMode.SINGLE_PROCESS, cryptKey);
    }

    public static MMKV defaultMMKV() {
        return MMKV_jvmKt.defaultMMKV(MMKV.Companion, MMKVMode.SINGLE_PROCESS, null);
    }

    public static MMKV defaultMMKV(MMKVMode mode) {
        return MMKV_jvmKt.defaultMMKV(MMKV.Companion, mode, null);
    }

    public static MMKV defaultMMKV(String cryptKey) {
        return MMKV_jvmKt.defaultMMKV(MMKV.Companion,MMKVMode.SINGLE_PROCESS, cryptKey);
    }

    public static MMKV defaultMMKV(MMKVMode mode, String cryptKey) {
        return MMKV_jvmKt.defaultMMKV(MMKV.Companion, mode, cryptKey);
    }
}
