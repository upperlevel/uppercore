package xyz.upperlevel.uppercore.config;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import xyz.upperlevel.uppercore.config.exceptions.RequiredPropertyNotFoundConfigException;
import xyz.upperlevel.uppercore.config.exceptions.WrongValueConfigException;

import java.io.StringReader;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class ConfigIntegrationTest {
    /*
     * The UpperCore API has been trough quite a lot of changes and,
     * for various motives it has been trying to keep compatibility
     * in the most used APIs.
     * This is a test to check if the old Config helper and the new
     * ConfigParser System can integrate well enough, without causing
     * any problems mixing the two of them and maintaining error
     * information (error line and similar) whenever possible.
     */

    public static class A {
        @ConfigConstructor
        public A(Config c) {
            assertTrue(c.getBoolRequired("bool"));
            assertEquals(c.getStringRequired("name"), "aname");
            assertEquals(c.getByteRequired("life"), 42);
        }
    }

    public static class B {
        @ConfigConstructor
        public B(
                @ConfigProperty("insta") A a,
                @ConfigProperty("stringkey") String str
        ) {
            assertEquals(str, "teststring");
        }
    }

    @Test
    public void basicTest() {
        Config.from(ImmutableMap.of(
                "insta", ImmutableMap.of(
                        "bool", true,
                        "name", "aname",
                        "life", 42
                ),
                "stringkey", "teststring"
        )).get(B.class);
    }

    // Test exceptions

    @Test
    public void configExceptionTest1() {
        Exception exc = assertThrows(WrongValueConfigException.class, () -> {
            Config.fromYaml(new StringReader(
                    "i: hello\n"
            )).getIntRequired("i");
        });
        assumeTrue(exc.getMessage().contains("line 1, column 4"));
    }

    @Test
    public void configExceptionTest2() {
        List<Config> cnfs =  Config.fromYaml(new StringReader(
                "sub:\n" +
                        "- i: 14\n" +
                        "  r: other_value\n" +
                        "- i: hello2\n"
        )).getConfigListRequired("sub");
        assertEquals(14, cnfs.get(0).getRequired("i"));
        assertEquals("other_value", cnfs.get(0).getStringRequired("r"));
        Exception exc = assertThrows(WrongValueConfigException.class, () -> {
            cnfs.get(1).getIntRequired("i");// Not an int, exception
        });
        assumeTrue(exc.getMessage().contains("line 4, column 6"));
    }

    @Test
    public void configExceptionTest3() {
        Exception exc = assertThrows(RequiredPropertyNotFoundConfigException.class, () -> {
            Config.fromYaml(new StringReader(
                    "a: 1\n" +
                            "b: 2\n" +
                            "f: 6\n"
            )).getRequired("c");
        });
        assumeTrue(exc.getMessage().contains("line 1, column 1"));
    }

    @Test
    public void oldConfigMapUnfoldingTest() {
        Config c = Config.fromYaml(new StringReader(
                "a:\n" +
                "  b:\n" +
                "    c: d\n"
        ));
        assertEquals("d", c.getStringRequired("a.b.c"));

        c = Config.from(ImmutableMap.of(
                "a", ImmutableMap.of(
                        "b", ImmutableMap.of(
                                "c", "d"
                        )
                )
        ));
        assertEquals("d", c.getStringRequired("a.b.c"));

        c = Config.from(ImmutableMap.of("a", ImmutableMap.of("test123", "mictest")));
        assertEquals("mictest", c.getStringRequired("a.test123"));
        assertNull(c.getString("b.test.abc"));
    }

    @Test
    public void configAsConfigMapTest() {
        Config c = Config.fromYaml(new StringReader(
                "a:\n" +
                        "  name: 'hello'\n" +
                        "  level: 9001\n" +
                        "b:\n" +
                        "  memes: true\n" +
                        "c:\n" +
                        " some: 'pair'\n"
        ));
        Map<String, Config> m = c.asConfigMap();
        Config ac = m.get("a");
        assertEquals("hello", ac.getStringRequired("name"));
        assertEquals(9001, ac.getIntRequired("level"));

        Config bc = m.get("b");
        assertTrue(bc.getBoolRequired("memes"));

        Config cc = m.get("c");
        assertEquals("pair", cc.getStringRequired("some"));
    }
}
