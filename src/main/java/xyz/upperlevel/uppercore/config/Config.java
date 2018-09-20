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
import xyz.upperlevel.uppercore.placeholder.message.Message;
import xyz.upperlevel.uppercore.placeholder.*;
import xyz.upperlevel.uppercore.sound.CompatibleSound;
import xyz.upperlevel.uppercore.sound.PlaySound;
import xyz.upperlevel.uppercore.util.LocUtil;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

@SuppressWarnings({"unchecked", "deprecation"}) // -_-
public abstract class Config {
    private static Representer yamlRepresenter = new YamlRepresenter();

    public abstract Object get(String key);

    public abstract Node getYamlNode();

    // Object

    public Object get(String key, Object defaultValue) {
        final Object res = get(key);
        return res != null ? res : defaultValue;
    }

    public Object getRequired(String key) {
        final Object res = get(key);
        if (res == null) {
            throw new RequiredPropertyNotFoundException(key);
        }
        return res;
    }

    public boolean has(String key) {
        return get(key) != null;
    }

    // DyeColor

    public DyeColor getDye(String key, DyeColor def) {
        String raw = getString(key);
        if (raw == null)
            return def;
        else {
            try {
                return ConfigUtil.parseDye(raw);
            } catch (InvalidConfigException e) {
                e.addLocation("in property \"" + key + "\"");
                throw e;
            }
        }
    }

    public DyeColor getDye(String key) {
        return getDye(key, null);
    }

    public DyeColor getDyeRequired(String key) {
        DyeColor color = getDye(key, null);
        if (color == null) {
            throw new RequiredPropertyNotFoundException(key);
        }
        return color;
    }

    // String

    public String getString(String key) {
        Object raw = get(key);
        return raw == null ? null : raw.toString();
    }

    public String getString(String key, String def) {
        final String res = getString(key);
        return res != null ? res : def;
    }

    public String getStringRequired(String key) {
        String str = getString(key);
        if (str == null) {
            throw new RequiredPropertyNotFoundException(key);
        }
        return str;
    }

    // String List

    public List<String> getStringList(String key) {
        List<String> res = null;
        try {
            res = (List<String>) get(key);
        } catch (ClassCastException e) {
            throw new InvalidConfigValueException(key, get(key), "List");
        }
        return res;
    }

    public List<String> getStringList(String key, List<String> def) {
        List<String> res = getStringList(key);
        return res != null ? res : def;
    }

    public List<String> getStringListRequired(String key) {
        List<String> res = getStringList(key);
        if (res == null) {
            throw new RequiredPropertyNotFoundException(key);
        }
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
        if (res == null) {
            throw new RequiredPropertyNotFoundException(key);
        }
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
        if (tmp == null)
            return null;
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
        if (res == null) {
            throw new RequiredPropertyNotFoundException(key);
        }
        return res;
    }

    // Int

    public Integer getInt(String key) {
        Number res = null;
        try {
            res = ((Number) get(key));
        } catch (ClassCastException e) {
            throw new InvalidConfigValueException(key, get(key), "Number");
        }
        return res == null ? null : res.intValue();
    }

    public int getInt(String key, int def) {
        final Integer res = getInt(key);
        return res != null ? res : def;
    }

    public int getIntRequired(String key) {
        Object raw = get(key);
        if (raw == null) {
            throw new RequiredPropertyNotFoundException(key);
        }
        try {
            return ((Number) get(key)).intValue();
        } catch (ClassCastException e) {
            throw new InvalidConfigValueException(key, raw, "Number");
        }
    }

    // Short

    public Short getShort(String key) {
        Number res = null;
        try {
            res = ((Number) get(key));
        } catch (ClassCastException e) {
            throw new InvalidConfigValueException(key, get(key), "Number");
        }
        return res == null ? null : res.shortValue();
    }

    public short getShort(String key, short def) {
        final Short res = getShort(key);
        return res != null ? res : def;
    }

    public short getShortRequired(String key) {
        Object raw = get(key);
        if (raw == null) {
            throw new RequiredPropertyNotFoundException(key);
        }
        try {
            return ((Number) get(key)).shortValue();
        } catch (ClassCastException e) {
            throw new InvalidConfigValueException(key, raw, "Number");
        }
    }

    // Byte

    public Byte getByte(String key) {
        Number res;
        try {
            res = ((Number) get(key));
        } catch (ClassCastException e) {
            throw new InvalidConfigValueException(key, get(key), "Number");
        }
        return res == null ? null : res.byteValue();
    }

    public byte getByte(String key, byte def) {
        final Byte res = getByte(key);
        return res != null ? res : def;
    }

    public byte getByteRequired(String key) {
        Object raw = get(key);
        if (raw == null) {
            throw new RequiredPropertyNotFoundException(key);
        }
        try {
            return ((Number) get(key)).byteValue();
        } catch (ClassCastException e) {
            throw new InvalidConfigValueException(key, raw, "Number");
        }
    }

    // Long

    public Long getLong(String key) {
        Number res;
        try {
            res = ((Number) get(key));
        } catch (ClassCastException e) {
            throw new InvalidConfigValueException(key, get(key), "Number");
        }
        return res == null ? null : res.longValue();
    }

    public long getLong(String key, long def) {
        final Long res = getLong(key);
        return res != null ? res : def;
    }

    public long getLongRequired(String key) {
        Object raw = get(key);
        if (raw == null) {
            throw new RequiredPropertyNotFoundException(key);
        }
        try {
            return ((Number) get(key)).longValue();
        } catch (ClassCastException e) {
            throw new InvalidConfigValueException(key, raw, "Number");
        }
    }

    // Bool

    public Boolean getBool(String key) {
        Object raw = get(key);
        if (raw == null) {
            return null;
        }
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
        throw new InvalidConfigValueException(key, raw, "Boolean");
    }

    public boolean getBool(String key, boolean def) {
        final Boolean res = getBool(key);
        return res != null ? res : def;
    }

    public boolean getBoolRequired(String key) {
        Boolean raw = getBool(key);
        if (raw == null) {
            throw new RequiredPropertyNotFoundException(key);
        }
        return raw;
    }

    // Float

    public Float getFloat(String key) {
        Number res;
        try {
            res = ((Number) get(key));
        } catch (ClassCastException e) {
            throw new InvalidConfigValueException(key, get(key), "Number");
        }
        return res == null ? null : res.floatValue();
    }

    public float getFloat(String key, float def) {
        Float res = getFloat(key);
        return res != null ? res : def;
    }

    public float getFloatRequired(String key) {
        Object raw = get(key);
        if (raw == null) {
            throw new RequiredPropertyNotFoundException(key);
        }
        try {
            return ((Number) get(key)).floatValue();
        } catch (ClassCastException e) {
            throw new InvalidConfigValueException(key, raw, "Number");
        }
    }

    // Double

    public Double getDouble(String key) {
        Number res;
        try {
            res = ((Number) get(key));
        } catch (ClassCastException e) {
            throw new InvalidConfigValueException(key, get(key), "Number");
        }
        return res == null ? null : res.doubleValue();
    }

    public double getDouble(String key, double def) {
        Double res = getDouble(key);
        return res != null ? res : def;
    }

    public double getDoubleRequired(String key) {
        Object raw = get(key);
        if (raw == null) {
            throw new RequiredPropertyNotFoundException(key);
        }
        try {
            return ((Number) get(key)).doubleValue();
        } catch (ClassCastException e) {
            throw new InvalidConfigValueException(key, raw, "Number");
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
            throw new InvalidConfigException("Cannot find \"" + clazz.getSimpleName().toLowerCase() + "\" \"" + raw + "\"");
        }
    }

    public <T extends Enum<T>> T getEnum(String key, T def, Class<T> clazz) {
        final T res = getEnum(key, clazz);
        return res != null ? res : def;
    }

    public <T extends Enum<T>> T getEnumRequired(String key, Class<T> clazz) {
        T res = getEnum(key, clazz);
        if (res == null) {
            throw new RequiredPropertyNotFoundException(key);
        }
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
                e.addLocation("in property \"" + key + "\"");
                throw e;
            }
        }
    }

    public Color getColor(String key) {
        return getColor(key, null);
    }

    public Color getColorRequired(String key) {
        Color color = getColor(key, null);
        if (color == null) {
            throw new RequiredPropertyNotFoundException(key);
        }
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
                throw new InvalidConfigException("Cannot find sound \"" + raw + "\", is it supported?");
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
        if (sound == null) {
            throw new RequiredPropertyNotFoundException(key);
        }
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
        return PlaySound.fromConfig(raw);
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
                throw new InvalidConfigValueException(key, raw, "String|Number");
            }
            if (res == null) {
                throw new InvalidConfigException("Cannot find material \"" + raw + "\"");
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
        if (mat == null) {
            throw new RequiredPropertyNotFoundException(key);
        }
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
            throw new InvalidConfigValueException(key, raw, "Map");
        }
    }

    public Map<String, Object> getMap(String key, Map<String, Object> def) {
        final Map<String, Object> res = getMap(key);
        return res != null ? res : def;
    }

    public Map<String, Object> getMapRequired(String key) {
        Map<String, Object> res = getMap(key);
        if (res == null) {
            throw new RequiredPropertyNotFoundException(key);
        }
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
            throw new InvalidConfigValueException(key, raw, "Map");
        }
    }

    public Config getConfig(String key) {
        return getConfig(key, null);
    }

    public Config getConfigRequired(String key) {
        Config res = getConfig(key, null);
        if (res == null) {
            throw new RequiredPropertyNotFoundException(key);
        }
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
        if (res == null) {
            throw new RequiredPropertyNotFoundException(key);
        }
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
        if (res == null) {
            throw new RequiredPropertyNotFoundException(key);
        }
        return res;
    }


    // Collection

    public Collection getCollection(String key) {
        try {
            return ((Collection) get(key));
        } catch (ClassCastException e) {
            throw new InvalidConfigValueException(key, get(key), "Collection");
        }
    }

    public Collection getCollection(String key, Collection def) {
        Collection found = getCollection(key);
        return found == null ? def : found;
    }

    public Collection getCollectionRequired(String key) {
        Object raw = get(key);
        if (raw == null) {
            throw new RequiredPropertyNotFoundException(key);
        }
        try {
            return ((Collection) get(key));
        } catch (ClassCastException e) {
            throw new InvalidConfigValueException(key, get(key), "Collection");
        }
    }

    // Location

    public Location getLocation(String key, Location def) {
        try {
            return LocUtil.deserialize(getConfig(key));
        } catch (Exception e) {
            return def;
        }
    }

    public Location getLocation(String key) {
        return getLocation(key, null);
    }

    public Location getLocationRequired(String key) {
        Location l = getLocation(key);
        if (l == null) {
            throw new RequiredPropertyNotFoundException(key);
        }
        return l;
    }

    // Location List

    public List<Location> getLocationList(String key, List<Location> def) {
        List<Config> configs = getConfigList(key);
        List<Location> res = new ArrayList<>();
        if (configs == null)
            return def;
        try {
            for (Config cfg : configs)
                res.add(LocUtil.deserialize(cfg));
        } catch (Exception e) {
            return def;
        }
        return res;
    }

    public List<Location> getLocationList(String key) {
        return getLocationList(key, null);
    }

    public List<Location> getLocationListRequired(String key) {
        List<Location> res = getLocationList(key);
        if (res == null) {
            throw new RequiredPropertyNotFoundException(key);
        }
        return res;
    }

    // Custom Item

    public CustomItem getCustomItem(String key, Function<Config, CustomItem> deserializer) {
        Config sub = getConfig(key);
        if (sub == null)
            return null;
        try {
            return deserializer.apply(sub);
        } catch (InvalidConfigException e) {
            e.addLocation("in item " + key);
            throw e;
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
        if (res == null)
            throw new RequiredPropertyNotFoundException(key);
        return res;
    }

    public CustomItem getCustomItemRequired(String key, PlaceholderRegistry local) {
        CustomItem res = getCustomItem(key, local);
        if (res == null)
            throw new RequiredPropertyNotFoundException(key);
        return res;
    }

    // ConfigParser way

    public <T> T get(Plugin plugin, Class<T> clazz) {
        return ConfigParserRegistry.getStandard()
                .getFor(clazz)
                .parse(plugin, getYamlNode());
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
