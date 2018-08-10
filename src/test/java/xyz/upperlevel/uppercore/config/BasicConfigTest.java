package xyz.upperlevel.uppercore.config;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.Plugin;
import org.junit.Test;
import xyz.upperlevel.uppercore.config.parser.ConfigParser;
import xyz.upperlevel.uppercore.config.parser.ConfigParserRegistry;
import xyz.upperlevel.uppercore.util.Position;

import java.io.StringReader;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class BasicConfigTest {
    public static final ConfigParserRegistry registry = ConfigParserRegistry.createStandard();
    private static final Plugin plugin = null;

    public static class ConfigLoaderExample {
        @ConfigConstructor
        public ConfigLoaderExample(@ConfigProperty("str")     String str,
                                   @ConfigProperty("count")   int count,
                                   @ConfigProperty("enum") List<ItemFlag> flags,
                                   @ConfigProperty("type")    Material type,
                                   @ConfigProperty("center") Position center,
                                   @ConfigProperty("center2") Position center2) {

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
        registry.getFor(ConfigLoaderExample.class)
                .parse(
                        plugin,
                        new StringReader(
                                "str: Stringa\n" +
                                        "count: 129\n" +
                                        "enum: [hide enchants, hide attributes]\n" +
                                        "type: 55\n" +
                                        "center: [15.0, 30.0, 60.0]\n" +
                                        "center2:\n" +
                                        "  x: 1.0\n" +
                                        "  y: 2.0\n" +
                                        "  z: 3.0\n"
                        )
                );
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
        private static Class<? extends PolymorphicFather> selectChild (@ConfigProperty("type") String type) {
            switch (type) {
                case "dog": return Dog.class;
                case "cat": return Cat.class;
                default:    return PolymorphicFather.class;
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
        ConfigParser<PolymorphicFather> parser = registry.getFor(PolymorphicFather.class);

        PolymorphicFather dog = parser.parse(
                plugin,
                new StringReader(
                        "type: dog\n" +
                                "furColor: blue\n"
                )
        );
        assertEquals(PolymorphicFather.Dog.class, dog.getClass());
        assertEquals(Color.BLUE, ((PolymorphicFather.Dog)dog).getFurColor());

        PolymorphicFather cat = parser.parse(
                plugin,
                new StringReader(
                        "type: cat\n" +
                                "meowMessage: meoow\n"
                )
        );
        assertEquals(PolymorphicFather.Cat.class, cat.getClass());
        assertEquals("meoow", ((PolymorphicFather.Cat)cat).getMeowMessage());

        PolymorphicFather horse = parser.parse(
                plugin,
                new StringReader(
                        "type: horse\n"
                )
        );
        assertEquals(PolymorphicFather.class, horse.getClass());
        assertEquals("horse", horse.getType());
    }
}
