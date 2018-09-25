package xyz.upperlevel.uppercore.config;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.Plugin;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import xyz.upperlevel.uppercore.config.exceptions.PropertyNotFoundParsingException;
import xyz.upperlevel.uppercore.config.parser.ConfigParser;
import xyz.upperlevel.uppercore.config.parser.ConfigParserRegistry;
import xyz.upperlevel.uppercore.util.Position;

import java.io.StringReader;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;

public class BasicConfigTest {
    private static final Plugin plugin = null;
    @Rule
    public ExpectedException exc = ExpectedException.none();

    public static class ConfigLoaderExample {
        @ConfigConstructor
        public ConfigLoaderExample(
                @ConfigProperty("str") String str,
                @ConfigProperty("count") int count,
                @ConfigProperty("enum") List<ItemFlag> flags,
                @ConfigProperty("type") Material type,
                @ConfigProperty("center") Position center,
                @ConfigProperty("center2") Position center2
        ) {
            assertEquals("Stringa", str);
            assertEquals(129, count);
            assertEquals(ImmutableList.of(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES), flags);
            assertEquals(Material.getMaterial(55), type);
            assertEquals(new Position(15, 30, 60), center);
            assertEquals(new Position(1, 2, 3), center2);
        }
    }

    @Test
    public void basicTest() {
        // This is a shortcut for the second method shown below
        // it might be slightly slower if you need to parse more objects of the same type
        // but the difference could never be noticeable
        Config.fromYaml(new StringReader(
                "str: Stringa\n" +
                        "count: 129\n" +
                        "enum: [hide enchants, hide attributes]\n" +
                        "type: 55\n" +
                        "center: [15.0, 30.0, 60.0]\n" +
                        "center2:\n" +
                        "  x: 1.0\n" +
                        "  y: 2.0\n" +
                        "  z: 3.0\n"
        )).get(ConfigLoaderExample.class, plugin);
    }

    public static class PolymorphicFather {
        @Getter
        private final String type;

        @ConfigConstructor
        public PolymorphicFather(
                @ConfigProperty("type") String type
        ) {
            this.type = type;
        }

        @PolymorphicSelector
        private static Class<? extends PolymorphicFather> selectChild(@ConfigProperty("type") String type) {
            switch (type) {
                case "dog":
                    return Dog.class;
                case "cat":
                    return Cat.class;
                default:
                    return PolymorphicFather.class;
            }
        }

        public static class Dog extends PolymorphicFather {
            @Getter
            private Color furColor;

            @ConfigConstructor
            public Dog(@ConfigProperty("furColor") Color furColor) {
                super("dog");
                this.furColor = furColor;
            }
        }

        public static class Cat extends PolymorphicFather {
            @Getter
            private String meowMessage;

            @ConfigConstructor
            public Cat(@ConfigProperty("meowMessage") String meowMessage) {
                super("cat");
                this.meowMessage = meowMessage;
            }
        }
    }

    @Test
    public void testPolymorphic() {
        // This is the more explicit way to parse the config
        // First you query the parser
        ConfigParser<PolymorphicFather> parser = ConfigParserRegistry.getStandard().getFor(PolymorphicFather.class);

        // And then you parse
        PolymorphicFather dog = parser.parse(
                plugin,
                new StringReader(
                        "type: dog\n" +
                                "furColor: blue\n"
                )
        );
        assertEquals(PolymorphicFather.Dog.class, dog.getClass());
        assertEquals(Color.BLUE, ((PolymorphicFather.Dog) dog).getFurColor());

        PolymorphicFather cat = parser.parse(
                plugin,
                new StringReader(
                        "type: cat\n" +
                                "meowMessage: meoow\n"
                )
        );
        assertEquals(PolymorphicFather.Cat.class, cat.getClass());
        assertEquals("meoow", ((PolymorphicFather.Cat) cat).getMeowMessage());

        PolymorphicFather horse = parser.parse(
                plugin,
                new StringReader(
                        "type: horse\n"
                )
        );
        assertEquals(PolymorphicFather.class, horse.getClass());
        assertEquals("horse", horse.getType());
    }

    public static class UnfoldTest {
        @ConfigConstructor
        public UnfoldTest(
                @ConfigProperty("unfold.test1") String str,
                @ConfigProperty("unfold.testb") int count,
                @ConfigProperty("enum") List<ItemFlag> flags,
                @ConfigProperty("type") Material type,
                @ConfigProperty("unf.c") Position center,
                @ConfigProperty("unf.d") Position center2
        ) {
            assertEquals("Stringa", str);
            assertEquals(129, count);
            assertEquals(ImmutableList.of(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES), flags);
            assertEquals(Material.getMaterial(55), type);
            assertEquals(new Position(15, 30, 60), center);
            assertEquals(new Position(1, 2, 3), center2);
        }
    }

    @Test
    public void mapUnfoldingTest() {
        Config.fromYaml(new StringReader(
                        "unfold:\n" +
                        "  test1: Stringa\n" +
                        "  testb: 129\n" +
                        "enum: [hide enchants, hide attributes]\n" +
                        "type: 55\n" +
                        "unf:\n" +
                        "  c: [15.0, 30.0, 60.0]\n" +
                        "  d:\n" +
                        "    x: 1.0\n" +
                        "    y: 2.0\n" +
                        "    z: 3.0\n"
        )).get(UnfoldTest.class, plugin);
    }

    @Test
    public void mapUnfoldingTestUnusedExc() {
        exc.expect(PropertyNotFoundParsingException.class);
        exc.expectMessage(containsString("line 3, column 3"));
        Config.fromYaml(new StringReader(
                "unfold:\n" +
                        "  test1: Stringa\n" +
                        "  test2: 129\n" + // Line 3
                        "enum: [hide enchants, hide attributes]\n" +
                        "type: 55\n" +
                        "unf:\n" +
                        "  c: [15.0, 30.0, 60.0]\n" +
                        "  e: 3\n" +
                        "  d:\n" +
                        "    x: 1.0\n" +
                        "    y: 2.0\n" +
                        "    z: 3.0\n"
        )).get(UnfoldTest.class, plugin);
    }

    public static class IncorrectUnfoldingTest {
        @ConfigConstructor
        public IncorrectUnfoldingTest(
                @ConfigProperty("unfold.test1") String str,
                @ConfigProperty("unfold.testb") int count,
                @ConfigProperty("unfold") Config unf
        ) {
            // Explaination: "unfold" catches everything so we can't get anything inside of it
            // it would mess with property checking (and it would be a pretty useless feature)
            // If you think otherwise please open an issue in the Github page
        }
    }

    @Test
    public void mapUnfoldingTestUsedPropertyException() {
        exc.expect(IllegalArgumentException.class);
        exc.expectMessage(containsString("Unfolding already used property"));
        ConfigParserRegistry.getStandard().getFor(IncorrectUnfoldingTest.class);
    }
}
