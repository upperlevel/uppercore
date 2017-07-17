package xyz.upperlevel.uppercore.gui.config.util;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import xyz.upperlevel.uppercore.gui.config.InvalidGuiConfigurationException;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

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
        String raw;
        try {
            raw = (String) get(key);
        } catch (ClassCastException e) {
            throw new InvalidGuiConfigurationException("Invalid value in \"" + key + "\"");
        }
        if (raw == null)
            return def;
        else {
            try {
                return ConfigUtils.parseDye(raw);
            } catch (InvalidGuiConfigurationException e) {
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

    //------------------------String

    default String getString(String key) {
        Object raw;
        try {
            raw = get(key);
        } catch (ClassCastException e) {
            throw new InvalidGuiConfigurationException("Invalid value in \"" + key + "\"");
        }
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

    //-----------------------Message (String + placeholder + colors)

    default PlaceholderValue<String> getMessage(String key, String def) {
        String message = getString(key, def);
        return message == null ? null : PlaceholderUtil.process(message);
    }

    default PlaceholderValue<String> getMessage(String key) {
        return getMessage(key, null);
    }

    default PlaceholderValue<String> getMessageRequired(String key) {
        return PlaceholderUtil.process(getStringRequired(key));
    }


    //------------------------Int

    default Integer getInt(String key) {
        Number res;
        try {
            res = ((Number) get(key));
        } catch (ClassCastException e) {
            throw new InvalidGuiConfigurationException("Invalid value in \"" + key + "\"");
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
            throw new InvalidGuiConfigurationException("Invalid value in \"" + key + "\"");
        }
    }

    //------------------------Short

    default Short getShort(String key) {
        Number res;
        try {
            res = ((Number) get(key));
        } catch (ClassCastException e) {
            throw new InvalidGuiConfigurationException("Invalid value in \"" + key + "\"");
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
            throw new InvalidGuiConfigurationException("Invalid value in \"" + key + "\"");
        }
    }

    //------------------------Long

    default Long getLong(String key) {
        Number res;
        try {
            res = ((Number) get(key));
        } catch (ClassCastException e) {
            throw new InvalidGuiConfigurationException("Invalid value in \"" + key + "\"");
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
            throw new InvalidGuiConfigurationException("Invalid value in \"" + key + "\"");
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
        throw new InvalidGuiConfigurationException("Invalid boolean in \"" + key + "\"");
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
            throw new InvalidGuiConfigurationException("Cannot find \"" + clazz.getSimpleName().toLowerCase() + "\" \"" + raw + "\"");
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
        String raw;
        try {
            raw = (String) get(key);
        } catch (ClassCastException e) {
            throw new InvalidGuiConfigurationException("Invalid value in \"" + key + "\"");
        }
        if (raw == null)
            return def;
        else {
            try {
                return ConfigUtils.parseColor(raw);
            } catch (InvalidGuiConfigurationException e) {
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
        String raw;
        try {
            raw = (String) get(key);
        } catch (ClassCastException e) {
            throw new InvalidGuiConfigurationException("Invalid value in \"" + key + "\"");
        }
        if (raw == null)
            return def;
        else {
            try {
                return Sound.valueOf(key.toUpperCase(Locale.ENGLISH));
            } catch (IllegalArgumentException e) {
                throw new InvalidGuiConfigurationException("Cannot find sound \"" + raw + "\"");
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
            Material res;
            if (raw instanceof Number)
                res = Material.getMaterial(((Number) raw).intValue());
            else if (raw instanceof String) {
                res = Material.getMaterial(((String) raw).replace(' ', '_').toUpperCase());
            } else
                throw new InvalidGuiConfigurationException("Invalid value in \"" + key + "\"");
            if (res == null)
                throw new InvalidGuiConfigurationException("Cannot find material \"" + raw + "\"");
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
        try {
            return (Map<String, Object>) get(key);
        } catch (ClassCastException e) {
            throw new InvalidGuiConfigurationException("Invalid value in \"" + key + "\"");
        }
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

    default Config getConfig(String key, Map<String, Object> def) {
        Map<String, Object> config = getSection(key);
        return config != null ? Config.wrap(config) : null;
    }

    @SuppressWarnings("unchecked")//-_-
    default Config getConfig(String key) {
        return getConfig(key, null);
    }

    default Config getConfigRequired(String key) {
        return Config.wrap(getSectionRequired(key));
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

    default Collection getCollection(String key, Map<String, Object> def) {
        try {
            return ((Collection) get(key));
        } catch (ClassCastException e) {
            throw new InvalidGuiConfigurationException("Invalid value in \"" + key + "\"");
        }
    }

    default Collection getCollection(String key) {
        return getCollection(key, null);
    }

    default Collection getCollectionRequired(String key) {
        Object raw = get(key);
        if (raw == null)
            requiredPropertyNotFound(key);
        try {
            return ((Collection) get(key));
        } catch (ClassCastException e) {
            throw new InvalidGuiConfigurationException("Invalid value in \"" + key + "\"");
        }
    }

    static void requiredPropertyNotFound(String key) {
        throw new InvalidGuiConfigurationException("Cannot find property \"" + key + "\"");
    }

    static Config wrap(Map<String, Object> map) {
        //I <3 java 8
        return map::get;
    }

    static Config wrap(ConfigurationSection section) {
        return section::get;
    }
}
