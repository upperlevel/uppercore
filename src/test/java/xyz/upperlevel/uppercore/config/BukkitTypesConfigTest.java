package xyz.upperlevel.uppercore.config;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.junit.Test;
import xyz.upperlevel.uppercore.config.parser.ConfigParserRegistry;
import xyz.upperlevel.uppercore.gui.GuiSize;

import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class BukkitTypesConfigTest {
    public static final ConfigParserRegistry registry = ConfigParserRegistry.getStandard();
    private static final Plugin plugin = null;

    public static class ConfigLoaderExample {
        @ConfigConstructor
        public ConfigLoaderExample(@ConfigProperty("pos") Vector position,
                                   @ConfigProperty("color1")   Color color1,
                                   @ConfigProperty("color2")   Color color2,
                                   @ConfigProperty("color3")   Color color3,
                                   //@ConfigProperty("sound")    Sound sound,
                                   @ConfigProperty("mat1")     Material mat1,
                                   @ConfigProperty("mat2")     Material mat2,
                                   //@ConfigProperty("loc")      Location loc,// TODO: mock
                                   //@ConfigProperty("ench1")    Enchantment ench1,
                                   //@ConfigProperty("ench2")    Enchantment ench2,
                                   @ConfigProperty("guisize1") GuiSize guiSize1,
                                   @ConfigProperty("guisize2") GuiSize guiSize2,
                                   @ConfigProperty("patterni") PatternType patternTypeId,
                                   @ConfigProperty("patternt") PatternType patternTypeName,
                                   @ConfigProperty("pattern") Pattern pattern,
                                   @ConfigProperty("firework") FireworkEffect fireworkEffect
                                   //@ConfigProperty("potiont")  PotionEffectType potionEffectType,
                                   //@ConfigProperty("potione")  PotionEffect potionEffect
        ) {

            assertEquals(new Vector(31, 24, 56), position);
            assertEquals(Color.GREEN, color1);
            assertEquals(Color.WHITE, color2);
            assertEquals(Color.RED, color3);
            //assertEquals(Sound.ENTITY_RABBIT_ATTACK, sound);
            assertEquals(Material.LAPIS_BLOCK, mat1);
            assertEquals(Material.BONE, mat2);// I am the bone of my api
            // location
            //assertEquals(Enchantment.OXYGEN, ench1);
            //assertEquals(Enchantment.THORNS, ench2);
            assertEquals(GuiSize.NORMAL, guiSize1);
            assertEquals(GuiSize.DOUBLE, guiSize2);
            assertEquals(PatternType.RHOMBUS_MIDDLE, patternTypeId);
            assertEquals(PatternType.MOJANG, patternTypeName);
            assertEquals(new Pattern(DyeColor.CYAN, PatternType.FLOWER), pattern);
            assertEquals(
                    FireworkEffect.builder()
                            .flicker(true)
                            .trail(true)
                            .withColor(Color.GREEN, Color.WHITE, Color.RED)
                            .withFade(Color.AQUA)
                            .with(FireworkEffect.Type.CREEPER)
                            .build(),
                    fireworkEffect
            );
            /*assertEquals(
                    PotionEffectType.JUMP,
                    potionEffectType
            );
            assertEquals(
                    new PotionEffect(
                            PotionEffectType.FAST_DIGGING,
                            100,
                            50,
                            true,
                            true,
                            Color.AQUA
                    ),
                    potionEffect
            );*/
        }
    }

    @Test
    public void basicTest() {
        registry.getFor(ConfigLoaderExample.class)
                .parse(
                        plugin,
                        new StringReader(
                                "pos: [31, 24, 56]\n" +
                                        "color1: 0;128;0\n" +
                                        "color2: '#FFFFFF'\n" +
                                        "color3: red\n" +
                                        //"sound: entity rabbit attack\n" +
                                        "mat1: lapis block\n" +
                                        "mat2: bone\n" +
                                        //"loc: [main, 10, 12, 13]\n" +
                                        //"ench1: 5\n" +
                                        //"ench2: thorns\n" +
                                        "guisize1: 27\n" +
                                        "guisize2: double\n" +
                                        "patterni: mr\n" +
                                        "patternt: mojang\n" +
                                        "pattern: [cyan, flower]\n" +
                                        "firework:\n" +
                                        "  flicker: true\n" +
                                        "  trail: true\n" +
                                        "  colors: [green, white, red]\n" +
                                        "  fade-colors: [aqua]\n" +
                                        "  type: creeper"
                                        /*"potiont: jump\n" +
                                        "potione: \n" +
                                        "  duration: 100\n" +
                                        "  amplifier: 50\n" +
                                        "  ambient: true\n" +
                                        "  particles: true\n" +
                                        "  color: aqua"*/
                        )
                );
    }
}
