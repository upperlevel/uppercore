package xyz.upperlevel.uppercore.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import xyz.upperlevel.uppercore.UpperTestUtil;
import xyz.upperlevel.uppercore.config.exceptions.PropertyNotFoundParsingException;
import xyz.upperlevel.uppercore.config.parser.ConfigParser;
import xyz.upperlevel.uppercore.config.parser.ConfigParserRegistry;
import xyz.upperlevel.uppercore.gui.action.Action;
import xyz.upperlevel.uppercore.gui.action.actions.BroadcastAction;
import xyz.upperlevel.uppercore.gui.action.actions.CommandAction;
import xyz.upperlevel.uppercore.util.Pair;
import xyz.upperlevel.uppercore.util.Position;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static xyz.upperlevel.uppercore.util.TypeUtil.typeOf;

public class BasicConfigTest {

    @BeforeAll
    public static void setUpClass() {
        UpperTestUtil.setup();
    }

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
            assertEquals(Material.REDSTONE_WIRE, type);
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
                        "type: redstone wire\n" +
                        "center: [15.0, 30.0, 60.0]\n" +
                        "center2:\n" +
                        "  x: 1.0\n" +
                        "  y: 2.0\n" +
                        "  z: 3.0\n"
        )).get(ConfigLoaderExample.class);
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
                    // Fallback
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
        ConfigParser parser = ConfigParserRegistry.getStandard().getFor(PolymorphicFather.class);

        // And then you parse
        PolymorphicFather dog = (PolymorphicFather) parser.parse(
                new StringReader(
                        "type: dog\n" +
                                "furColor: blue\n"
                )
        );
        assertEquals(PolymorphicFather.Dog.class, dog.getClass());
        assertEquals(Color.BLUE, ((PolymorphicFather.Dog) dog).getFurColor());

        PolymorphicFather cat = (PolymorphicFather) parser.parse(
                new StringReader(
                        "type: cat\n" +
                                "meowMessage: meoow\n"
                )
        );
        assertEquals(PolymorphicFather.Cat.class, cat.getClass());
        assertEquals("meoow", ((PolymorphicFather.Cat) cat).getMeowMessage());

        PolymorphicFather horse = (PolymorphicFather) parser.parse(
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
            assertEquals(Material.REDSTONE_WIRE, type);
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
                        "type: redstone wire\n" +
                        "unf:\n" +
                        "  c: [15.0, 30.0, 60.0]\n" +
                        "  d:\n" +
                        "    x: 1.0\n" +
                        "    y: 2.0\n" +
                        "    z: 3.0\n"
        )).get(UnfoldTest.class);
    }

    @Test
    public void mapUnfoldingTestUnusedExc() {
        Exception exc = assertThrows(PropertyNotFoundParsingException.class, () -> {
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
            )).get(UnfoldTest.class);
        });
        assumeTrue(exc.getMessage().contains("line 3, column 3"));
    }

    public static class IncorrectUnfoldingTest {
        @ConfigConstructor
        public IncorrectUnfoldingTest(
                @ConfigProperty("unfold.test1") String str,
                @ConfigProperty("unfold.testb") int count,
                @ConfigProperty("unfold") Config unf
        ) {
            // Explanation: "unfold" catches everything so we can't get anything inside of it
            // it would mess with property checking (and it would be a pretty useless feature)
            // If you think otherwise please open an issue in the Github page
        }
    }

    @Test
    public void mapUnfoldingTestUsedPropertyException() {
        Exception exc = assertThrows(IllegalArgumentException.class, () -> {
            ConfigParserRegistry.getStandard().getFor(IncorrectUnfoldingTest.class);
        });
        assumeTrue(exc.getMessage().contains("Unfolding already used property"));
    }

    @Test
    public void genericTypeTest() {
        // List<Map<String, Integer>>
        Type type = typeOf(List.class, typeOf(Map.class, String.class, Integer.class));
        Config cfg = Config.fromYaml(new StringReader(
                "c:\n" +
                        "- a: 1\n" +
                        "  b: 2\n" +
                        "  c: 3\n" +
                        "- d: 4\n" +
                        "  e: 5\n" +
                        "- f: 6\n"
        ));
        List<Map<String, Integer>> res = cfg.get("c", type, null);
        assertEquals(ImmutableList.of(
                ImmutableMap.of(
                        "a", 1,
                        "b", 2,
                        "c", 3
                ),
                ImmutableMap.of(
                        "d", 4,
                        "e", 5
                ),
                ImmutableMap.of(
                        "f", 6
                )
        ), res);
    }

    @Test
    public void pairTest() {
        Config cfg = Config.fromYaml(new StringReader(
                "a: [1, 2]\n" +
                "c:\n" +
                "  a: b\n"
        ));
        Type t1 = typeOf(Pair.class, int.class, int.class);
        Type t2 = typeOf(Pair.class, String.class, String.class);
        assertEquals(Pair.of(1, 2), cfg.getRequired("a", t1));
        assertEquals(Pair.of("a", "b"), cfg.getRequired("c", t2));
    }

    @Test
    public void actionTest() {
        Config cfg = Config.fromYaml(new StringReader(
                "actionList:\n" +
                        "- broadcast: 'hello'\n" +
                        "- command: 'quake join a1'\n" +
                        "- type: 'command'\n" +
                        "  command: quake quit\n" +
                        "  executor: player\n"
        ));
        Type actionList = typeOf(List.class, Action.class);
        List<Action<?>> actions = cfg.getRequired("actionList", actionList);

        assertEquals(3, actions.size());
        assertEquals(BroadcastAction.TYPE, actions.get(0).getType());
        assertEquals(CommandAction.TYPE, actions.get(1).getType());

        assertEquals(CommandAction.TYPE, actions.get(2).getType());
        CommandAction a3 = (CommandAction) actions.get(2);
        assertEquals("quake quit", a3.getCommand().resolve(null));
        assertEquals(CommandAction.Executor.PLAYER, a3.getExecutor());
    }
}
