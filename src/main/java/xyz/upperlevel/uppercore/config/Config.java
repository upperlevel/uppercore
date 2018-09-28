package xyz.upperlevel.uppercore.config;

import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlRepresenter;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Representer;
import xyz.upperlevel.uppercore.config.exceptions.InvalidConfigException;
import xyz.upperlevel.uppercore.config.exceptions.InvalidConfigValueException;
import xyz.upperlevel.uppercore.config.exceptions.RequiredPropertyNotFoundException;
import xyz.upperlevel.uppercore.config.parser.ConfigParser;
import xyz.upperlevel.uppercore.config.parser.ConfigParserRegistry;
import xyz.upperlevel.uppercore.itemstack.CustomItem;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;
import xyz.upperlevel.uppercore.placeholder.message.Message;
import xyz.upperlevel.uppercore.sound.CompatibleSound;
import xyz.upperlevel.uppercore.sound.PlaySound;
import xyz.upperlevel.uppercore.util.LocUtil;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;

@SuppressWarnings({"unchecked", "deprecation"}) // -_-
public abstract class Config {
    private static Representer yamlRepresenter = new YamlRepresenter();

    public abstract Object get(String key);

    public abstract Node getYamlNode();

    public abstract Stream<String> keys();

    // Object

    public Object get(String key, Object defaultValue) {
        final Object res = get(key);
        return res != null ? res : defaultValue;
    }

    public Object getRequired(String key) {
        final Object res = get(key);
        checkPropertyNotNull(key, res);
        return res;
    }

    public boolean has(String key) {
        return get(key) != null;
    }

    // DyeColor

    public DyeColor getDye(String key, DyeColor def) {
        String raw = getString(key);
        if (raw == null) return def;
        else {
            try {
                return ConfigUtil.parseDye(raw);
            } catch (InvalidConfigException e) {
                throw adjustParsingException(key, e);
            }
        }
    }

    public DyeColor getDye(String key) {
        return getDye(key, null);
    }

    public DyeColor getDyeRequired(String key) {
        DyeColor color = getDye(key, null);
        checkPropertyNotNull(key, color);
        return color;
    }

    // String

    public String getString(String key, String def) {
        Object raw = get(key);
        return raw == null ? def : raw.toString();
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public String getStringRequired(String key) {
        String str = getString(key);
        checkPropertyNotNull(key, str);
        return str;
    }

    // String List

    public List<String> getStringList(String key) {
        List<String> res;
        try {
            res = (List<String>) get(key);
        } catch (ClassCastException e) {
            throw invalidValueTypeException(key, "List");
        }
        return res;
    }

    public List<String> getStringList(String key, List<String> def) {
        List<String> res = getStringList(key);
        return res != null ? res : def;
    }

    public List<String> getStringListRequired(String key) {
        List<String> res = getStringList(key);
        checkPropertyNotNull(key, res);
        return res;
    }

    // List

    public <T> List<T> getList(String key) {
        return (List<T>) get(key);
    }

    public <T> List<T> getList(String key, List<T> def) {
        List<T> res = getList(key);
        return res != null ? res : def;
    }

    public <T> List<T> getListRequired(String key) {
        List<T> res = getList(key);
        checkPropertyNotNull(key, res);
        return res;
    }

    // Message

    public Message getMessage(String key) {
        return Message.fromConfig(get(key));
    }

    public Message getMessage(String key, Message def) {
        Message message = getMessage(key);
        return message == null ? def : message;
    }

    public Message getMessage(String key, String def) {
        Message message = getMessage(key);
        if (message != null) {
            return message;
        } else {
            return new Message(singletonList(PlaceholderValue.stringValue(def)));
        }
    }

    public Message getMessageRequired(String key) {
        return Message.fromConfig(getRequired(key));
    }

    // Message String (String + Placeholders + Colors)

    public PlaceholderValue<String> getMessageStr(String key) {
        return PlaceholderValue.stringValue(getString(key));
    }

    public PlaceholderValue<String> getMessageStr(String key, String def) {
        return PlaceholderValue.stringValue(getString(key, def));
    }

    public PlaceholderValue<String> getMessageStr(String key, PlaceholderValue<String> def) {
        String str = getString(key);
        return str == null ? def : PlaceholderValue.stringValue(str);
    }

    public PlaceholderValue<String> getMessageStrRequired(String key) {
        return PlaceholderValue.stringValue(getStringRequired(key));
    }

    // Message List (String + Placeholders + Colors)

    public List<PlaceholderValue<String>> getMessageStrList(String key) {
        List<String> tmp = getStringList(key);
        if (tmp == null) return null;
        return tmp.stream()
                .map(PlaceholderUtil::process)
                .collect(Collectors.toList());
    }

    public List<PlaceholderValue<String>> getMessageStrList(String key, List<PlaceholderValue<String>> def) {
        List<PlaceholderValue<String>> res = getMessageStrList(key);
        return res != null ? res : def;
    }

    public List<PlaceholderValue<String>> getMessageStrListRequired(String key) {
        List<PlaceholderValue<String>> res = getMessageStrList(key);
        checkPropertyNotNull(key, res);
        return res;
    }

    // Int

    public Integer getInt(String key) {
        Number res;
        try {
            res = ((Number) get(key));
        } catch (ClassCastException e) {
            throw invalidValueTypeException(key, "Number");
        }
        return res == null ? null : res.intValue();
    }

    public int getInt(String key, int def) {
        final Integer res = getInt(key);
        return res != null ? res : def;
    }

    public int getIntRequired(String key) {
        Object raw = get(key);
        checkPropertyNotNull(key, raw);
        try {
            return ((Number) get(key)).intValue();
        } catch (ClassCastException e) {
            throw invalidValueTypeException(key, "Number");
        }
    }

    // Short

    public Short getShort(String key) {
        Number res;
        try {
            res = ((Number) get(key));
        } catch (ClassCastException e) {
            throw invalidValueTypeException(key, "Number");
        }
        return res == null ? null : res.shortValue();
    }

    public short getShort(String key, short def) {
        final Short res = getShort(key);
        return res != null ? res : def;
    }

    public short getShortRequired(String key) {
        Object raw = get(key);
        checkPropertyNotNull(key, raw);
        try {
            return ((Number) raw).shortValue();
        } catch (ClassCastException e) {
            throw invalidValueTypeException(key, "Number");
        }
    }

    // Byte

    public Byte getByte(String key) {
        Number res;
        try {
            res = ((Number) get(key));
        } catch (ClassCastException e) {
            throw invalidValueTypeException(key, "Number");
        }
        return res == null ? null : res.byteValue();
    }

    public byte getByte(String key, byte def) {
        final Byte res = getByte(key);
        return res != null ? res : def;
    }

    public byte getByteRequired(String key) {
        Object raw = get(key);
        checkPropertyNotNull(key, raw);
        try {
            return ((Number) get(key)).byteValue();
        } catch (ClassCastException e) {
            throw invalidValueTypeException(key, "Number");
        }
    }

    // Long

    public Long getLong(String key) {
        Number res;
        try {
            res = ((Number) get(key));
        } catch (ClassCastException e) {
            throw invalidValueTypeException(key, "Number");
        }
        return res == null ? null : res.longValue();
    }

    public long getLong(String key, long def) {
        final Long res = getLong(key);
        return res != null ? res : def;
    }

    public long getLongRequired(String key) {
        Object raw = get(key);
        checkPropertyNotNull(key, raw);
        try {
            return ((Number) get(key)).longValue();
        } catch (ClassCastException e) {
            throw invalidValueTypeException(key, "Number");
        }
    }

    // Bool

    public Boolean getBool(String key) {
        Object raw = get(key);
        if (raw == null) return null;

        if (raw instanceof Boolean) {
            return (Boolean) raw;
        } else if (raw instanceof String) {
            switch (((String) raw).toLowerCase()) {
                case "no":
                case "false":
                    return false;
                case "yes":
                case "true":
                    return true;
            }
        } else if (raw instanceof Number) {
            return ((Number) raw).intValue() == 1;
        }
        throw invalidValueTypeException(key, "Boolean");
    }

    public boolean getBool(String key, boolean def) {
        final Boolean res = getBool(key);
        return res != null ? res : def;
    }

    public boolean getBoolRequired(String key) {
        Boolean raw = getBool(key);
        checkPropertyNotNull(key, raw);
        return raw;
    }

    // Float

    public Float getFloat(String key) {
        Number res;
        try {
            res = ((Number) get(key));
        } catch (ClassCastException e) {
            throw invalidValueTypeException(key, "Number");
        }
        return res == null ? null : res.floatValue();
    }

    public float getFloat(String key, float def) {
        Float res = getFloat(key);
        return res != null ? res : def;
    }

    public float getFloatRequired(String key) {
        Object raw = get(key);
        checkPropertyNotNull(key, raw);
        try {
            return ((Number) get(key)).floatValue();
        } catch (ClassCastException e) {
            throw invalidValueTypeException(key, "Number");
        }
    }

    // Double

    public Double getDouble(String key) {
        Number res;
        try {
            res = ((Number) get(key));
        } catch (ClassCastException e) {
            throw invalidValueTypeException(key, "Number");
        }
        return res == null ? null : res.doubleValue();
    }

    public double getDouble(String key, double def) {
        Double res = getDouble(key);
        return res != null ? res : def;
    }

    public double getDoubleRequired(String key) {
        Object raw = get(key);
        checkPropertyNotNull(key, raw);

        try {
            return ((Number) get(key)).doubleValue();
        } catch (ClassCastException e) {
            throw invalidValueTypeException(key, "Number");
        }
    }

    // Enum

    public <T extends Enum<T>> T getEnum(String key, Class<T> clazz) {
        String raw = getString(key);
        if (raw == null) return null;
        raw = raw.replace(' ', '_').toUpperCase(Locale.ENGLISH);
        try {
            return Enum.valueOf(clazz, raw);
        } catch (IllegalArgumentException e) {
            throw invalidConfigException(key, "Cannot find \"" + clazz.getSimpleName().toLowerCase() + "\" \"" + raw + "\"");
        }
    }

    public <T extends Enum<T>> T getEnum(String key, T def, Class<T> clazz) {
        final T res = getEnum(key, clazz);
        return res != null ? res : def;
    }

    public <T extends Enum<T>> T getEnumRequired(String key, Class<T> clazz) {
        T res = getEnum(key, clazz);
        checkPropertyNotNull(key, res);
        return res;
    }

    // Color

    public Color getColor(String key, Color def) {
        String raw = getString(key);
        if (raw == null) {
            return def;
        } else {
            try {
                return ConfigUtil.parseColor(raw);
            } catch (InvalidConfigException e) {
                throw adjustParsingException(key, e);
            }
        }
    }

    public Color getColor(String key) {
        return getColor(key, null);
    }

    public Color getColorRequired(String key) {
        Color color = getColor(key, null);
        checkPropertyNotNull(key, color);
        return color;
    }

    // Sound

    public Sound getSound(String key, Sound def) {
        String raw = getString(key);
        if (raw == null) {
            return def;
        } else {
            Sound res = CompatibleSound.get(raw);
            if (res == null) {
                throw invalidConfigException(key, "Cannot find sound \"" + raw + "\", is it supported?");
            } else {
                return res;
            }
        }
    }

    public Sound getSound(String key) {
        return getSound(key, null);
    }

    public Sound getSoundRequired(String key) {
        Sound sound = getSound(key, null);
        checkPropertyNotNull(key, sound);
        return sound;
    }

    // Bukkit Sound

    public PlaySound getBukkitSound(String key, PlaySound def) {
        Object raw = get(key);
        return raw != null ? PlaySound.fromConfig(raw) : def;
    }

    public PlaySound getBukkitSound(String key) {
        return getBukkitSound(key, null);
    }

    public PlaySound getBukkitSoundRequired(String key) {
        Object raw = getRequired(key);
        try {
            return PlaySound.fromConfig(raw);
        } catch (InvalidConfigException e) {
            throw adjustParsingException(key, e);
        }
    }

    // Material

    public Material getMaterial(String key, Material def) {
        Object raw = get(key);
        if (raw == null) {
            return def;
        } else {
            Material res;
            if (raw instanceof Number) {
                res = Material.getMaterial(((Number) raw).intValue());
            } else if (raw instanceof String) {
                res = Material.getMaterial(((String) raw).replace(' ', '_').toUpperCase());
            } else {
                throw invalidValueTypeException(key, "String|Number");
            }
            if (res == null) {
                throw invalidConfigException(key, "Cannot find material \"" + raw + "\"");
            } else {
                return res;
            }
        }
    }

    public Material getMaterial(String key) {
        return getMaterial(key, null);
    }

    public Material getMaterialRequired(String key) {
        Material mat = getMaterial(key, null);
        checkPropertyNotNull(key, mat);
        return mat;
    }

    // Map

    public Map<String, Object> getMap(String key) {
        Object raw = get(key);
        if (raw == null) {
            return null;
        }
        if (raw instanceof Map) {
            return (Map<String, Object>) raw;
        } else if (raw instanceof ConfigurationSection) {
            return ((ConfigurationSection) raw).getValues(false);
        } else {
            throw invalidValueTypeException(key, "Map");
        }
    }

    public Map<String, Object> getMap(String key, Map<String, Object> def) {
        final Map<String, Object> res = getMap(key);
        return res != null ? res : def;
    }

    public Map<String, Object> getMapRequired(String key) {
        Map<String, Object> res = getMap(key);
        checkPropertyNotNull(key, res);
        return res;
    }

    // Config

    public Config getConfig(String key, Config def) {
        Object raw = get(key);
        if (raw == null) {
            return def;
        } else if (raw instanceof Map) {
            return Config.from((Map<String, Object>) raw);
        } else if (raw instanceof ConfigurationSection) {
            return Config.from((ConfigurationSection) raw);
        } else {
            throw invalidValueTypeException(key, "Map");
        }
    }

    public Config getConfig(String key) {
        return getConfig(key, null);
    }

    public Config getConfigRequired(String key) {
        Config res = getConfig(key, null);
        checkPropertyNotNull(key, res);
        return res;
    }

    // Map List

    public List<Map<String, Object>> getMapList(String key, List<Map<String, Object>> def) {
        List<Map<String, Object>> res = getList(key);
        return res == null ? def : res;
    }

    public List<Map<String, Object>> getMapList(String key) {
        return getMapList(key, null);
    }

    public List<Map<String, Object>> getMapListRequired(String key) {
        List<Map<String, Object>> res = getMapList(key, null);
        checkPropertyNotNull(key, res);
        return res;
    }

    // Config List

    public List<Config> getConfigList(String key, List<Config> def) {
        Collection<Map<String, Object>> raw = getCollection(key);
        if (raw == null) return def;
        return raw.stream()
                .map(Config::from)
                .collect(Collectors.toList());
    }

    public List<Config> getConfigList(String key) {
        return getConfigList(key, null);
    }

    public List<Config> getConfigListRequired(String key) {
        List<Config> res = getConfigList(key, null);
        checkPropertyNotNull(key, res);
        return res;
    }


    // Collection

    public Collection getCollection(String key) {
        try {
            return ((Collection) get(key));
        } catch (ClassCastException e) {
            throw invalidValueTypeException(key, "Collection");
        }
    }

    public Collection getCollection(String key, Collection def) {
        Collection found = getCollection(key);
        return found == null ? def : found;
    }

    public Collection getCollectionRequired(String key) {
        Object raw = get(key);
        checkPropertyNotNull(key, raw);
        try {
            return ((Collection) get(key));
        } catch (ClassCastException e) {
            throw invalidValueTypeException(key, "Collection");
        }
    }

    // Location

    public Location getLocation(String key, Location def) {
        Config config = getConfig(key);
        if (config == null) return def;

        try {
            return LocUtil.deserialize(config);
        } catch (InvalidConfigException e) {
            throw adjustParsingException(key, e);
        }
    }

    public Location getLocation(String key) {
        return getLocation(key, null);
    }

    public Location getLocationRequired(String key) {
        Location l = getLocation(key);
        checkPropertyNotNull(key, l);
        return l;
    }

    // Location List

    public List<Location> getLocationList(String key, List<Location> def) {
        List<Config> configs = getConfigList(key);
        List<Location> res = new ArrayList<>();

        if (configs == null) return def;

        try {
            for (Config cfg : configs) {
                res.add(LocUtil.deserialize(cfg));
            }
        } catch (InvalidConfigException e) {
            throw adjustParsingException(key, e);
        }
        return res;
    }

    public List<Location> getLocationList(String key) {
        return getLocationList(key, null);
    }

    public List<Location> getLocationListRequired(String key) {
        List<Location> res = getLocationList(key);
        checkPropertyNotNull(key, res);
        return res;
    }

    // Custom Item

    public CustomItem getCustomItem(String key, Function<Config, CustomItem> deserializer) {
        Config sub = getConfig(key);
        if (sub == null) return null;

        try {
            return deserializer.apply(sub);
        } catch (InvalidConfigException e) {
            throw adjustParsingException(key, e);
        }
    }

    public CustomItem getCustomItem(String key) {
        return getCustomItem(key, CustomItem::deserialize);
    }

    public CustomItem getCustomItem(String key, PlaceholderRegistry local) {
        return getCustomItem(key, config -> CustomItem.deserialize(config, local));
    }

    public CustomItem getCustomItem(String key, CustomItem def) {
        CustomItem res = getCustomItem(key);
        return res != null ? res : def;
    }

    public CustomItem getCustomItemRequired(String key) {
        CustomItem res = getCustomItem(key);
        checkPropertyNotNull(key, res);
        return res;
    }

    public CustomItem getCustomItemRequired(String key, PlaceholderRegistry local) {
        CustomItem res = getCustomItem(key, local);
        checkPropertyNotNull(key, res);
        return res;
    }

    // ConfigParser way

    public <T> T get(Class<T> clazz, Plugin plugin) {
        return ConfigParserRegistry.getStandard()
                .getFor(clazz)
                .parse(plugin, getYamlNode());
    }

    public <T> T get(String key, Class<T> clazz, Plugin plugin) {
        Config c = getConfig(key);
        if (c == null) return null;
        return c.get(clazz, plugin);
    }

    public <T> T getRequired(String key, Class<T> clazz, Plugin plugin) {
        T res = get(key, clazz, plugin);
        checkPropertyNotNull(key, res);
        return res;
    }

    // Config map

    public Map<String, Config> asConfigMap() {
        return keys().collect(Collectors.toMap(k -> k, this::getConfig));
    }

    // Exception throwers

    protected void checkPropertyNotNull(String key, Object prop) {
        if (prop != null) return;
        throw new RequiredPropertyNotFoundException(key);
    }

    protected RuntimeException adjustParsingException(String key, InvalidConfigException e) {
        e.addLocation("in " + key);
        return e;
    }

    protected RuntimeException invalidValueTypeException(String key, String expectedType) {
        throw new InvalidConfigValueException(key, get(key), expectedType);
    }

    protected RuntimeException invalidConfigException(String key, String error) {
        throw new InvalidConfigException(key, error);
    }

    // Helper functions and Config builders

    public static Config from(Map<String, Object> map) {
        return new Config() {
            @Override
            public Object get(String key) {
                return map.get(key);
            }

            @Override
            public Node getYamlNode() {
                return yamlRepresenter.represent(map);
            }

            @Override
            public Stream<String> keys() {
                return map.keySet().stream();
            }
        };
    }

    public static Config from(ConfigurationSection section) {
        return new Config() {
            @Override
            public Object get(String key) {
                return section.get(key);
            }

            @Override
            public Node getYamlNode() {
                return yamlRepresenter.represent(section);
            }

            @Override
            public Stream<String> keys() {
                return section.getKeys(false).stream();
            }
        };
    }

    public static TrackingConfig from(Node node) {
        return new TrackingConfig(node);
    }

    public static TrackingConfig fromYaml(Reader reader) {
        return from(ConfigParser.defaultYaml.compose(reader));
    }

    public static TrackingConfig fromYaml(File in) {
        try {
            return fromYaml(new FileReader(in));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read yaml " + in.getName(), e);
        }
    }
}
