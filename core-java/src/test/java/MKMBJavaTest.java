import org.junit.jupiter.api.Test;
import top.kagg886.mkmb.MMKV;
import top.kagg886.mkmb.MMKVJava;
import top.kagg886.mkmb.MMKVOptions;

public class MKMBJavaTest {
    @Test
    public void testMKMBInitAPI() {
        MMKVOptions options = new MMKVOptions();
//        options.setLogFunc((logLevel, s, s2) -> null);
//        options.setLogLevel(MMKVOptions.LogLevel.Error);
//        options.setLibLoader(() -> "/path/to/lib");
        MMKVJava.initialize(
                "path",
                options
        );

        MMKV mmkv = MMKVJava.mmkvWithID("test");

        mmkv.clear();
    }
}
