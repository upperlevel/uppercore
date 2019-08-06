package xyz.upperlevel.uppercore.config;

import com.google.common.collect.ImmutableMap;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.yaml.snakeyaml.nodes.*;
import org.yaml.snakeyaml.nodes.Tag;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.config.exceptions.ConfigException;
import xyz.upperlevel.uppercore.config.exceptions.WrongValueConfigException;
import xyz.upperlevel.uppercore.config.parser.ConfigParserRegistry;
import xyz.upperlevel.uppercore.config.parser.ConstructorConfigParser;
import xyz.upperlevel.uppercore.gui.GuiSize;
import xyz.upperlevel.uppercore.gui.action.Action;
import xyz.upperlevel.uppercore.gui.action.ActionType;
import xyz.upperlevel.uppercore.sound.CompatibleSound;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;
import static xyz.upperlevel.uppercore.config.ConfigUtil.legacyAwareMaterialParse;
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
            throw new ConfigException("Cannot find Material by int, find the correct name here: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html", rawNode.getStartMark());
        } else {// node.getTag() == Tag.STR
            res = legacyAwareMaterialParse(node.getValue());
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
        Enchantment res = null;
        if (node.getTag() == Tag.STR) {
            NamespacedKey key = parseConfigNamespacedKey(node.getValue().replace(' ', '_').toLowerCase(Locale.ENGLISH), node);
            res = Enchantment.getByKey(key);
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
            @ConfigProperty("icon") Optional<Boolean> color
    ) {
        boolean particlesEnabled = hasParticles.orElse(true);
        return new PotionEffect(
                effect,
                duration,
                amplifier,
                ambient.orElse(false),
                particlesEnabled,
                color.orElse(particlesEnabled)
        );
    }

    @ConfigConstructor
    public static Action<?> parseGuiAction(Node raw) {
        checkTag(raw, Arrays.asList(Tag.MAP, Tag.STR));

        if (raw instanceof MappingNode) {
            MappingNode map = (MappingNode) raw;

            List<NodeTuple> params = map.getValue();

            Node explicitTypeNode = params.stream()
                    .filter(p -> p.getKeyNode() instanceof ScalarNode && ((ScalarNode) p.getKeyNode()).getValue().equals("type"))
                    .findFirst()
                    .map(NodeTuple::getValueNode)
                    .orElse(null);

            String type;
            Node typeNode;
            Node parameters;

            if (explicitTypeNode != null) {
                checkTag(explicitTypeNode, Tag.STR);
                type = ((ScalarNode) explicitTypeNode).getValue();
                typeNode = explicitTypeNode;
                parameters = map;
            } else {
                if (map.getValue().size() > 1) {
                    throw new ConfigException("Cannot have more than one action for now", raw);
                }

                typeNode = map.getValue().get(0).getKeyNode();
                Node valueNode = map.getValue().get(0).getValueNode();
                checkTag(typeNode, Tag.STR);
                type = ((ScalarNode) typeNode).getValue();
                parameters = valueNode;
            }

            ActionType<?> t = ActionType.getActionType(type.toLowerCase());
            if (t == null) {
                throw new ConfigException("Cannot find action \"" + type + "\" in " + ActionType.getActionTypes().keySet(), typeNode);
            }

            ConstructorConfigParser<?> parser = (ConstructorConfigParser<?>) ConfigParserRegistry.getStandard().getFor(t.getHandleClass());
            Predicate<String> oldPred = parser.getIgnoreUnmatchedProperties();
            parser.setIgnoreUnmatchedProperties(x -> x.equals("type") || oldPred.test(x));
            Action<?> res = (Action<?>) parser.parse(parameters);
            parser.setIgnoreUnmatchedProperties(oldPred);

            return res;
        } else { // Tag.STR
            String type = ((ScalarNode)raw).getValue();

            ActionType<?> t = ActionType.getActionType(type.toLowerCase());
            if (t == null) {
                throw new ConfigException("Cannot find action \"" + type + "\" in " + ActionType.getActionTypes().keySet(), raw);
            }
            return Config.from(ImmutableMap.of()).get(t.getHandleClass());
        }
    }

    @ConfigConstructor
    public static Config config(Node raw) {
        return new TrackingConfig(raw);
    }

    public static NamespacedKey parseConfigNamespacedKey(String val, Node node) {
        try {
            return parseNamespacedKey(val);
        } catch (IllegalArgumentException e) {
            throw new ConfigException(e.getMessage(), node.getStartMark());
        }
    }

    public static NamespacedKey parseNamespacedKey(String raw) {
        int sepIndex = raw.indexOf(':');
        if (sepIndex == -1) {
            return NamespacedKey.minecraft(raw);
        }
        return new NamespacedKey(raw.substring(0, sepIndex), raw.substring(sepIndex + 1));
    }
}
