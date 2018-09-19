package xyz.upperlevel.uppercore.config;

import com.google.common.collect.ImmutableMap;
import org.bukkit.plugin.Plugin;
import org.junit.Test;

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
        Config.wrap(ImmutableMap.of(
                "insta", ImmutableMap.of(
                        "bool", true,
                        "name", "aname",
                        "life", 42
                ),
                "stringkey", "teststring"
        )).get((Plugin) null, B.class);
    }
}
