package xyz.upperlevel.uppercore.config;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import com.google.common.collect.Maps;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;
import xyz.upperlevel.uppercore.config.exceptions.InvalidConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;

import static java.lang.Integer.parseInt;
import static java.lang.Integer.remainderUnsigned;
import static xyz.upperlevel.uppercore.util.CollectionUtil.toMap;

public final class ConfigUtils {

    private static final Map<String, Color> COLOR_BY_NAME = new HashMap<String, Color>() {{
        put("WHITE", Color.WHITE);
        put("SILVER", Color.SILVER);
        put("GRAY", Color.GRAY);
        put("BLACK", Color.BLACK);
        put("RED", Color.RED);
        put("MAROON", Color.MAROON);
        put("YELLOW", Color.YELLOW);
        put("OLIVE", Color.OLIVE);
        put("LIME", Color.LIME);
        put("GREEN", Color.GREEN);
        put("AQUA", Color.AQUA);
        put("TEAL", Color.TEAL);
        put("BLUE", Color.BLUE);
        put("NAVY", Color.NAVY);
        put("FUCHSIA", Color.FUCHSIA);
        put("PURPLE", Color.PURPLE);
        put("ORANGE", Color.ORANGE);
    }};

    public static DyeColor parseDye(String s) {
        if (s == null) return DyeColor.BLACK;
        try {
            return DyeColor.valueOf(s.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            throw new InvalidConfigurationException("Cannot find dye color: \"" + s + "\"");
        }
    }

    public static Color parseColor(String s) {
        String[] parts = s.split(";");
        if (parts.length != 3) {
            Color color;
            if (s.charAt(0) == '#' && s.length() >= 7) {//Hex color
                try {
                    color = Color.fromRGB(Integer.parseUnsignedInt(s.substring(1, 7), 16));
                } catch (IllegalArgumentException e) {
                    color = null;
                }
            } else
                color = COLOR_BY_NAME.get(s.toUpperCase());
            if (color == null)
                throw new InvalidConfigurationException("Invalid color \"" + s + "\", use \"R;G;B\", \"#RRGGBB\" or color name!");
            else return color;
        } else
            return Color.fromRGB(parseInt(parts[0]), parseInt(parts[1]), parseInt(parts[2]));
    }

    public static FireworkEffect.Type parseFireworkEffectType(String s) {
        if (s == null)
            throw new InvalidConfigurationException("Missing firework effect type!");
        try {
            return FireworkEffect.Type.valueOf(s.replace(' ', '_').toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            throw new InvalidConfigurationException("Cannot find firework effect type: \"" + s + "\"");
        }
    }

    public static FileConfiguration loadConfig(Plugin plugin, String fileName) {
        return loadConfig(plugin.getDataFolder(), fileName);
    }

    public static FileConfiguration loadConfig(File folder, String filename) {
        return loadConfig(new File(folder, filename));
    }

    public static FileConfiguration loadConfig(File file) {
        if (!file.exists())
            throw new InvalidConfigurationException("Cannot read file '" + file + "': no file found");
        if (!file.isFile())
            throw new InvalidConfigurationException("Cannot read file '" + file + "': not a file");
        if (!file.canRead())
            throw new InvalidConfigurationException("Cannot read file '" + file + "': cannot read");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException e) {
            throw new InvalidConfigurationException("Cannot read file '" + file + "': " + e.getMessage(), e);
        } catch (org.bukkit.configuration.InvalidConfigurationException e) {
            throw new InvalidConfigurationException("Invalid config file '" + file + "': " + e.getMessage(), e);
        }
        return config;
    }

    public static Map<String, Object> loadMap(Plugin plugin, String fileName) {
        return loadConfig(plugin, fileName).getValues(false);
    }

    public static Map<String, Config> loadConfigMap(Plugin plugin, String fileName, String itemName) {
        return loadConfigMap(plugin, fileName, (key, obj) -> plugin.getLogger().severe("Cannot parse " + itemName + " " + key + ": expected map (found: " + obj.getClass().getSimpleName() + ")"));
    }

    public static Map<String, Config> loadConfigMap(Plugin plugin, String fileName, BiConsumer<String, Object> cannotParseAsConfig) {
        return loadConfigMap(loadConfig(plugin, fileName), cannotParseAsConfig);
    }

    public static Map<String, Config> loadConfigMap(ConfigurationSection config, Plugin plugin, String itemName) {
        return loadConfigMap(config, (key, obj) -> plugin.getLogger().severe("Cannot parse " + itemName + " " + key + ": expected map (found: " + obj.getClass().getSimpleName() + ")"));
    }

    public static Map<String, Config> loadConfigMap(ConfigurationSection config, BiConsumer<String, Object> cannotParseAsConfig) {
        return loadConfigMap(config.getValues(false), cannotParseAsConfig);
    }

    public static Map<String, Config> loadConfigMap(Map<String, Object> config, Plugin plugin, String itemName) {
        return loadConfigMap(config, (key, obj) -> plugin.getLogger().severe("Cannot parse " + itemName + " " + key + ": expected map (found: " + obj.getClass().getSimpleName() + ")"));
    }

    public static Map<String, Config> loadConfigMap(Map<String, Object> config, BiConsumer<String, Object> cannotParseAsConfig) {
        return config
                .entrySet()
                .stream()
                .map(e -> {
                    Object o = e.getValue();
                    if (o instanceof Map)
                        return Maps.immutableEntry(e.getKey(), Config.wrap((Map) o));
                    else if (o instanceof ConfigurationSection)
                        return Maps.immutableEntry(e.getKey(), Config.wrap((ConfigurationSection) o));
                    else {
                        cannotParseAsConfig.accept(e.getKey(), o);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(toMap(LinkedHashMap::new));
    }

    private ConfigUtils() {
    }
}
