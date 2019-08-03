package xyz.upperlevel.uppercore.config;

import com.google.common.collect.ImmutableMap;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import xyz.upperlevel.uppercore.config.exceptions.RequiredPropertyNotFoundConfigException;
import xyz.upperlevel.uppercore.config.exceptions.WrongValueConfigException;

import java.io.StringReader;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    @Rule
    public ExpectedException exc = ExpectedException.none();

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
        exc.expect(WrongValueConfigException.class);
        exc.expectMessage(containsString("line 1, column 4"));
        Config.fromYaml(new StringReader(
                "i: hello\n"
        )).getIntRequired("i");
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
        exc.expect(WrongValueConfigException.class);
        exc.expectMessage(containsString("line 4, column 6"));
        cnfs.get(1).getIntRequired("i");// Not an int, exception
    }

    @Test
    public void configExceptionTest3() {
        exc.expect(RequiredPropertyNotFoundConfigException.class);
        exc.expectMessage(containsString("line 1, column 1"));
        Config.fromYaml(new StringReader(
                "a: 1\n" +
                "b: 2\n" +
                "f: 6\n"
        )).getRequired("c");
    }

    @Test
    public void oldConfigMapUnfoldingTest() {
        Config c = Config.fromYaml(new StringReader(
                "a:\n" +
                "  b:\n" +
                "    c: d\n"
        ));
        assertEquals("d", c.getStringRequired("a.b.c"));
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
