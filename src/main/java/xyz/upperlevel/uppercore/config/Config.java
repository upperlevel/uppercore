package xyz.upperlevel.uppercore.config;

import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import xyz.upperlevel.uppercore.gui.config.itemstack.CustomItem;
import xyz.upperlevel.uppercore.placeholder.Placeholder;
import xyz.upperlevel.uppercore.placeholder.PlaceholderSession;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;
import xyz.upperlevel.uppercore.util.SerializationUtil;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public interface Config {

    Object get(String key);

    default Object get(String key, Object defaultValue) {
        final Object res = get(key);
        return res != null ? res : defaultValue;
    }

    default Object getRequired(String key) {
        final Object res = get(key);
        if (res == null)
            requiredPropertyNotFound(key);
        return res;
    }

    default boolean has(String key) {
        return get(key) != null;
    }

    //------------------------DyeColor

    default DyeColor getDye(String key, DyeColor def) {
        String raw = getString(key);
        if (raw == null)
            return def;
        else {
            try {
                return ConfigUtils.parseDye(raw);
            } catch (InvalidConfigurationException e) {
                e.addLocalizer("in property \"" + key + "\"");
                throw e;
            }
        }
    }

    default DyeColor getDye(String key) {
        return getDye(key, null);
    }

    default DyeColor getDyeRequired(String key) {
        DyeColor color = getDye(key, null);
        if (color == null)
            requiredPropertyNotFound(key);
        return color;
    }

    //------------------------Float

    default Float getFloat(String key) {
        Number res;
        try {
            res = ((Number) get(key));
        } catch (ClassCastException e) {
            invalidValue(key, get(key), "Number");
            return null;
        }
        return res == null ? null : res.floatValue();
    }

    default float getFloat(String key, float def) {
        Float res = getFloat(key);
        return res != null ? res : def;
    }

    default float getFloatRequired(String key) {
        Object raw = get(key);
        if (raw == null)
            requiredPropertyNotFound(key);
        try {
            return ((Number) get(key)).intValue();
        } catch (ClassCastException e) {
            invalidValue(key, raw, "Number");
            return -1;
        }
    }

    //------------------------Double

    default Double getDouble(String key) {
        Number res = null;
        try {
            res = ((Number) get(key));
        } catch (ClassCastException e) {
            invalidValue(key, get(key), "Number");
        }
        return res == null ? null : res.doubleValue();
    }

    default double getDouble(String key, double def) {
        Double res = getDouble(key);
        return res != null ? res : def;
    }

    default double getDoubleRequired(String key) {
        Object raw = get(key);
        if (raw == null)
            requiredPropertyNotFound(key);
        try {
            return ((Number) get(key)).intValue();
        } catch (ClassCastException e) {
            invalidValue(key, raw, "Number");
            return -1;
        }
    }

    //------------------------String

    default String getString(String key) {
        Object raw = get(key);
        return raw == null ? null : raw.toString();
    }

    default String getString(String key, String def) {
        final String res = getString(key);
        return res != null ? res : def;
    }

    default String getStringRequired(String key) {
        String str = getString(key);
        if (str == null)
            requiredPropertyNotFound(key);
        return str;
    }

    //--------------------------StringList

    default List<String> getStringList(String key) {
        List<String> res = null;
        try {
            res = (List<String>) get(key);
        } catch (ClassCastException e) {
            invalidValue(key, get(key), "List");
        }
        return res;
    }

    default List<String> getStringList(String key, List<String> def) {
        List<String> res = getStringList(key);
        return res != null ? res : def;
    }

    default List<String> getStringListRequired(String key) {
        List<String> res = getStringList(key);
        if (res == null)
            requiredPropertyNotFound(key);
        return res;
    }

    //--------------------------List

    default <T> List<T> getList(String key) {
        return (List<T>) get(key);
    }

    default <T> List<T> getList(String key, List<T> def) {
        List<T> res = getList(key);
        return res != null ? res : def;
    }

    default <T> List<T> getListRequired(String key) {
        List<T> res = getList(key);
        if (res == null)
            requiredPropertyNotFound(key);
        return res;
    }

    //-----------------------Message (String + placeholder + colors)

    default PlaceholderValue<String> getMessage(String key, String def) {
        String message = getString(key, def);
        return message == null ? null : PlaceholderUtil.process(message);
    }

    default PlaceholderValue<String> getMessage(String key) {
        String message = getString(key);
        return message == null ? null : PlaceholderUtil.process(message);
    }

    default PlaceholderValue<String> getMessageRequired(String key) {
        return PlaceholderUtil.process(getStringRequired(key));
    }

    //-----------------------Message List (String + placeholder + colors)

    default List<PlaceholderValue<String>> getMessageList(String key) {
        List<String> tmp = getStringList(key);
        if (tmp == null)
            return null;
        return tmp.stream()
                .map(PlaceholderValue::stringValue)
                .collect(Collectors.toList());
    }

    default List<PlaceholderValue<String>> getMessageList(String key, List<PlaceholderValue<String>> def) {
        List<PlaceholderValue<String>> res = getMessageList(key);
        return res != null ? res : def;
    }

    default List<PlaceholderValue<String>> getMessageListRequired(String key) {
        List<PlaceholderValue<String>> res = getMessageList(key);
        if (res == null) requiredPropertyNotFound(key);
        return res;
    }


    //------------------------Int

    default Integer getInt(String key) {
        Number res = null;
        try {
            res = ((Number) get(key));
        } catch (ClassCastException e) {
            invalidValue(key, get(key), "Number");
        }
        return res == null ? null : res.intValue();
    }

    default int getInt(String key, int def) {
        final Integer res = getInt(key);
        return res != null ? res : def;
    }

    default int getIntRequired(String key) {
        Object raw = get(key);
        if (raw == null)
            requiredPropertyNotFound(key);
        try {
            return ((Number) get(key)).intValue();
        } catch (ClassCastException e) {
            invalidValue(key, raw, "Number");
            return -1;
        }
    }

    //------------------------Short

    default Short getShort(String key) {
        Number res = null;
        try {
            res = ((Number) get(key));
        } catch (ClassCastException e) {
            invalidValue(key, get(key), "Number");
        }
        return res == null ? null : res.shortValue();
    }

    default short getShort(String key, short def) {
        final Short res = getShort(key);
        return res != null ? res : def;
    }

    default short getShortRequired(String key) {
        Object raw = get(key);
        if (raw == null)
            requiredPropertyNotFound(key);
        try {
            return ((Number) get(key)).shortValue();
        } catch (ClassCastException e) {
            invalidValue(key, raw, "Number");
            return -1;
        }
    }

    //------------------------Byte

    default Byte getByte(String key) {
        Number res = null;
        try {
            res = ((Number) get(key));
        } catch (ClassCastException e) {
            invalidValue(key, get(key), "Number");
        }
        return res == null ? null : res.byteValue();
    }

    default byte getByte(String key, byte def) {
        final Byte res = getByte(key);
        return res != null ? res : def;
    }

    default byte getByteRequired(String key) {
        Object raw = get(key);
        if (raw == null)
            requiredPropertyNotFound(key);
        try {
            return ((Number) get(key)).byteValue();
        } catch (ClassCastException e) {
            invalidValue(key, raw, "Number");
            return -1;
        }
    }

    //------------------------Long

    default Long getLong(String key) {
        Number res = null;
        try {
            res = ((Number) get(key));
        } catch (ClassCastException e) {
            invalidValue(key, get(key), "Number");
        }
        return res == null ? null : res.longValue();
    }

    default long getLong(String key, long def) {
        final Long res = getLong(key);
        return res != null ? res : def;
    }

    default long getLongRequired(String key) {
        Object raw = get(key);
        if (raw == null)
            requiredPropertyNotFound(key);
        try {
            return ((Number) get(key)).longValue();
        } catch (ClassCastException e) {
            invalidValue(key, raw, "Number");
            return -1;
        }
    }

    //------------------------Bool

    default Boolean getBool(String key) {
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
        invalidValue(key, raw, "Boolean");
        return null;
    }

    default boolean getBool(String key, boolean def) {
        final Boolean res = getBool(key);
        return res != null ? res : def;
    }

    default boolean getBoolRequired(String key) {
        Boolean raw = getBool(key);
        if (raw == null)
            requiredPropertyNotFound(key);
        return raw;
    }

    //------------------------Enum

    default <T extends Enum<T>> T getEnum(String key, Class<T> clazz) {
        String raw = getString(key);
        if (raw == null) return null;
        raw = raw.replace(' ', '_').toUpperCase(Locale.ENGLISH);
        try {
            return Enum.valueOf(clazz, raw);
        } catch (IllegalArgumentException e) {
            throw new InvalidConfigurationException("Cannot find \"" + clazz.getSimpleName().toLowerCase() + "\" \"" + raw + "\"");
        }
    }

    default <T extends Enum<T>> T getEnum(String key, T def, Class<T> clazz) {
        final T res = getEnum(key, clazz);
        return res != null ? res : def;
    }

    default <T extends Enum<T>> T getEnumRequired(String key, Class<T> clazz) {
        T res = getEnum(key, clazz);
        if (res == null)
            requiredPropertyNotFound(key);
        return res;
    }

    //------------------------Color

    default Color getColor(String key, Color def) {
        String raw = getString(key);
        if (raw == null)
            return def;
        else {
            try {
                return ConfigUtils.parseColor(raw);
            } catch (InvalidConfigurationException e) {
                e.addLocalizer("in property \"" + key + "\"");
                throw e;
            }
        }
    }

    default Color getColor(String key) {
        return getColor(key, null);
    }

    default Color getColorRequired(String key) {
        Color color = getColor(key, null);
        if (color == null)
            requiredPropertyNotFound(key);
        return color;
    }

    //------------------------Sound

    default Sound getSound(String key, Sound def) {
        String raw = getString(key);
        if (raw == null)
            return def;
        else {
            try {
                return Sound.valueOf(key.toUpperCase(Locale.ENGLISH));
            } catch (IllegalArgumentException e) {
                throw new InvalidConfigurationException("Cannot find sound \"" + raw + "\"");
            }
        }
    }

    default Sound getSound(String key) {
        return getSound(key, null);
    }

    default Sound getSoundRequired(String key) {
        Sound sound = getSound(key, null);
        if (sound == null)
            requiredPropertyNotFound(key);
        return sound;
    }

    //------------------------Material

    default Material getMaterial(String key, Material def) {
        Object raw = get(key);
        if (raw == null)
            return def;
        else {
            Material res = null;
            if (raw instanceof Number)
                res = Material.getMaterial(((Number) raw).intValue());
            else if (raw instanceof String) {
                res = Material.getMaterial(((String) raw).replace(' ', '_').toUpperCase());
            } else
                invalidValue(key, raw, "String|Number");
            if (res == null)
                throw new InvalidConfigurationException("Cannot find material \"" + raw + "\"");
            else return res;
        }
    }

    default Material getMaterial(String key) {
        return getMaterial(key, null);
    }

    default Material getMaterialRequired(String key) {
        Material mat = getMaterial(key, null);
        if (mat == null)
            requiredPropertyNotFound(key);
        return mat;
    }

    //------------------------Map

    @SuppressWarnings("unchecked")//-_-
    default Map<String, Object> getSection(String key) {
        Object raw = get(key);
        if(raw == null)
            return null;
        if (raw instanceof Map)
            return (Map<String, Object>) raw;
        else if (raw instanceof ConfigurationSection)
            return ((ConfigurationSection) raw).getValues(false);
        else
            invalidValue(key, raw, "Map");
        return null;
    }

    default Map<String, Object> getSection(String key, Map<String, Object> def) {
        final Map<String, Object> res = getSection(key);
        return res != null ? res : def;
    }

    default Map<String, Object> getSectionRequired(String key) {
        Map<String, Object> res = getSection(key);
        if (res == null)
            requiredPropertyNotFound(key);
        return res;
    }

    //------------------------Config

    default Config getConfig(String key, Config def) {
        Object raw = get(key);
        if (raw == null)
            return def;
        if (raw instanceof Map)
            return Config.wrap((Map<String, Object>) raw);
        else if (raw instanceof ConfigurationSection)
            return Config.wrap((ConfigurationSection) raw);
        else
            invalidValue(key, raw, "Map");
        return null;
    }

    default Config getConfig(String key) {
        return getConfig(key, null);
    }

    default Config getConfigRequired(String key) {
        Config res = getConfig(key, null);
        if (res == null)
            requiredPropertyNotFound(key);
        return res;
    }

    //------------------------Config List

    default List<Config> getConfigList(String key, List<Config> def) {
        Collection<Map<String, Object>> raw = getCollection(key);
        if (raw == null) return def;
        return raw.stream()
                .map(Config::wrap)
                .collect(Collectors.toList());
    }

    default List<Config> getConfigList(String key) {
        return getConfigList(key, null);
    }

    default List<Config> getConfigListRequired(String key) {
        List<Config> res = getConfigList(key, null);
        if (res == null)
            requiredPropertyNotFound(key);
        return res;
    }


    //------------------------Collection

    default Collection getCollection(String key) {
        try {
            return ((Collection) get(key));
        } catch (ClassCastException e) {
            invalidValue(key, get(key), "Collection");
            return null;
        }
    }

    default Collection getCollection(String key, Collection def) {
        Collection found = getCollection(key);
        return found == null ? def : found;
    }

    default Collection getCollectionRequired(String key) {
        Object raw = get(key);
        if (raw == null)
            requiredPropertyNotFound(key);
        try {
            return ((Collection) get(key));
        } catch (ClassCastException e) {
            invalidValue(key, get(key), "Collection");
            return null;
        }
    }

    //--------------------------Location

    default Location getLocation(String key, Location def) {
        try {
            return SerializationUtil.deserializeLocation(getConfig(key));
        } catch (Exception e) {
            return def;
        }
    }

    default Location getLocation(String key) {
        return getLocation(key, null);
    }

    default Location getLocationRequired(String key) {
        Location l = getLocation(key);
        if (l == null)
            requiredPropertyNotFound(key);
        return l;
    }

    //--------------------------LocationList

    default List<Location> getLocationList(String key, List<Location> def) {
        List<Config> cfgs = getConfigList(key);
        List<Location> res = new ArrayList<>();
        if (cfgs == null)
            return def;
        try {
            for (Config cfg : cfgs)
                res.add(SerializationUtil.deserializeLocation(cfg));
        } catch (Exception e) {
            return def;
        }
        return res;
    }

    default List<Location> getLocationList(String key) {
        return getLocationList(key, null);
    }

    default List<Location> getLocationListRequired(String key) {
        List<Location> res = getLocationList(key);
        if (res == null)
            requiredPropertyNotFound(key);
        return res;
    }

    //--------------------------CustomItem

    default CustomItem getCustomItem(String key, Function<Config, CustomItem> deserializer) {
        Config sub = getConfig(key);
        if (sub == null)
            return null;
        try {
            return deserializer.apply(sub);
        } catch (InvalidConfigurationException e) {
            e.addLocalizer("in item " + key);
            throw e;
        }
    }

    default CustomItem getCustomItem(String key) {
        return getCustomItem(key, CustomItem::deserialize);
    }

    default CustomItem getCustomItem(String key, Map<String, Placeholder> local) {
        return getCustomItem(key, config -> CustomItem.deserialize(config, local));
    }

    default CustomItem getCustomItem(String key, PlaceholderSession local) {
        return getCustomItem(key, config -> CustomItem.deserialize(config, local));
    }

    default CustomItem getCustomItem(String key, CustomItem def) {
        CustomItem res = getCustomItem(key);
        return res != null ? res : def;
    }

    default CustomItem getCustomItemRequired(String key) {
        CustomItem res = getCustomItem(key);
        if (res == null)
            requiredPropertyNotFound(key);
        return res;
    }

    default CustomItem getCustomItemRequired(String key, Map<String, Placeholder> local) {
        CustomItem res = getCustomItem(key, local);
        if (res == null)
            requiredPropertyNotFound(key);
        return res;
    }

    default CustomItem getCustomItemRequired(String key, PlaceholderSession local) {
        CustomItem res = getCustomItem(key, local);
        if (res == null)
            requiredPropertyNotFound(key);
        return res;
    }

    static void requiredPropertyNotFound(String key) {
        throw new InvalidConfigurationException("Cannot find property \"" + key + "\"");
    }

    static void invalidValue(String key, Object value, String expected) {
        throw new InvalidConfigurationException("Invalid value in '" + key + "' (found '" + value.getClass().getSimpleName() + "', expected '" + expected + "')");
    }

    static Config wrap(Map<String, Object> map) {
        return map::get;
    }

    static Config wrap(ConfigurationSection section) {
        return section::get;
    }
}
