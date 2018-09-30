package xyz.upperlevel.uppercore.config;

import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;
import xyz.upperlevel.uppercore.config.exceptions.WrongValueConfigException;
import xyz.upperlevel.uppercore.gui.GuiSize;
import xyz.upperlevel.uppercore.sound.CompatibleSound;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static xyz.upperlevel.uppercore.config.parser.ConfigParser.checkTag;

public class StandardExternalDeclarator implements ConfigExternalDeclarator {
    @ConfigConstructor(inlineable = true)
    private Vector parsePosition(@ConfigProperty("x") double x,
                                 @ConfigProperty("y") double y,
                                 @ConfigProperty("z") double z) {
        return new Vector(x, y, z);
    }

    @ConfigConstructor
    private Color parseColor(Node node) {// Raw constructor
        checkTag(node, Tag.STR);
        try {
            return ConfigUtil.parseColor(((ScalarNode) node).getValue());
        } catch (Exception e) {
            throw new WrongValueConfigException(node, ((ScalarNode) node).getValue(), "r;g;b or hex color");
        }
    }

    @ConfigConstructor(inlineable = true)
    private Sound parseSound(String raw) {
        return CompatibleSound.get(raw);
    }

    @ConfigConstructor
    private Material parseMaterial(Node rawNode) {
        checkTag(rawNode, Arrays.asList(Tag.STR, Tag.INT));
        ScalarNode node = (ScalarNode) rawNode;
        Material res;
        if (node.getTag() == Tag.INT) {
            res = Material.getMaterial(Integer.parseInt(node.getValue()));
        } else {// node.getTag() == Tag.STR
            res = Material.getMaterial(node.getValue().replace(' ', '_').toUpperCase());
        }
        if (res == null) {
            throw new WrongValueConfigException(node, node.getValue(), "Material");
        }
        return res;
    }

    @ConfigConstructor(inlineable = true)
    private Location parseLocation(@ConfigProperty("world") String rawWorld,
                                   @ConfigProperty("x") double x,
                                   @ConfigProperty("y") double y,
                                   @ConfigProperty("z") double z,
                                   @ConfigProperty(value = "yaw", optional = true) Float yaw,
                                   @ConfigProperty(value = "pitch", optional = true) Float pitch) {
        World world = Bukkit.getWorld(rawWorld);
        if (world == null) {
            throw new IllegalArgumentException("Cannot find world '" + rawWorld + "'");
        }
        return new Location(world, x, y, z, yaw != null ? yaw : 0.0f, pitch != null ? pitch : 0.0f);
    }

    @ConfigConstructor
    private Enchantment parseEnchantment(Node rawNode) {
        checkTag(rawNode, Arrays.asList(Tag.STR, Tag.INT));
        ScalarNode node = (ScalarNode) rawNode;
        Enchantment res;
        if (node.getTag() == Tag.INT) {
            res = Enchantment.getById(Integer.parseInt(node.getValue()));
        } else {// node.getTag() == Tag.STR
            res = Enchantment.getByName(node.getValue().replace(' ', '_').toUpperCase());
        }
        if (res == null) {
            throw new WrongValueConfigException(node, node.getValue(), "Enchantment");
        }
        return res;
    }

    @ConfigConstructor
    private GuiSize parseGuiSize(Node rawNode) {
        checkTag(rawNode, Arrays.asList(Tag.STR, Tag.INT));
        ScalarNode node = (ScalarNode) rawNode;
        GuiSize res;
        try {
            if (node.getTag() == Tag.INT) {
                res = GuiSize.lookup(Integer.parseInt(node.getValue()));
            } else {// node.getTag() == Tag.STR
                res = GuiSize.valueOf(node.getValue().replace(' ', '_').toUpperCase(Locale.ENGLISH));
            }
        } catch (Exception e) {
            res = null;
        }
        if (res == null) {
            throw new WrongValueConfigException(node, node.getValue(), "GuiSize");
        }
        return res;
    }

    @ConfigConstructor(inlineable = true)
    private PatternType parsePatternType(String id) {
        PatternType res = PatternType.getByIdentifier(id);
        if (res != null) {
            return res;
        }
        return PatternType.valueOf(id.replace(" ", "_").toUpperCase(Locale.ENGLISH));
    }

    @ConfigConstructor(inlineable = true)
    @ExternalDeclaratorPriority(50)
    private Pattern parsePattern(
            @ConfigProperty("color") DyeColor color,
            @ConfigProperty("type") PatternType type
    ) {
        return new Pattern(color, type);
    }

    @SuppressWarnings("unchecked")
    @ConfigConstructor
    @ExternalDeclaratorPriority(50)
    public static FireworkEffect parseFireworkEffect(
            @ConfigProperty("flicker") Optional<Boolean> flicker,
            @ConfigProperty("trail") Optional<Boolean> trail,
            @ConfigProperty(value = "colors", optional = true) List<Color> colors,
            @ConfigProperty(value = "fade-colors", optional = true) List<Color> fadeColors,
            @ConfigProperty("type") Optional<FireworkEffect.Type> type
    ) {
        return FireworkEffect.builder()
                .flicker(flicker.orElse(false))
                .trail(trail.orElse(false))
                .withColor(colors != null ? colors : emptyList())
                .withFade(fadeColors != null ? fadeColors : emptyList())
                .with(type.orElse(FireworkEffect.Type.BALL))
                .build();
    }

    @ConfigConstructor
    public static PotionEffectType parsePotionEffectType(Node rawNode) {
        checkTag(rawNode, Arrays.asList(Tag.STR, Tag.INT));
        ScalarNode node = (ScalarNode) rawNode;
        PotionEffectType res;
        if (node.getTag() == Tag.INT) {
            res = PotionEffectType.getById(Integer.parseInt(node.getValue()));
        } else {// node.getTag() == Tag.STR
            res = PotionEffectType.getByName(node.getValue().replace(' ', '_').toUpperCase(Locale.ENGLISH));
        }
        if (res == null) {
            throw new WrongValueConfigException(node, node.getValue(), "PotionEffectType");
        }
        return res;
    }

    @ConfigConstructor
    @ExternalDeclaratorPriority(50)
    public static PotionEffect parsePotionEffect(
            @ConfigProperty("effect") PotionEffectType effect,
            @ConfigProperty("duration") int duration,
            @ConfigProperty("amplifier") int amplifier,
            @ConfigProperty("ambient") Optional<Boolean> ambient,
            @ConfigProperty("has-particles") Optional<Boolean> hasParticles,
            @ConfigProperty("color") Optional<Color> color
    ) {
        return new PotionEffect(
                effect,
                duration,
                amplifier,
                ambient.orElse(false),
                hasParticles.orElse(true),
                color.orElse(null)
        );
    }

    @ConfigConstructor
    public static Config config(Node raw) {
        return new TrackingConfig(raw);
    }
}
