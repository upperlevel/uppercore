package xyz.upperlevel.uppercore.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.config.exceptions.InvalidConfigException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;

import static java.lang.Integer.parseInt;
import static xyz.upperlevel.uppercore.util.CollectionUtil.toMap;

public final class ConfigUtil {
    public static final Map<String, Color> COLOR_BY_NAME = new HashMap<>(ImmutableMap.<String, Color>builder()
            .put("WHITE", Color.WHITE)
            .put("SILVER", Color.SILVER)
            .put("GRAY", Color.GRAY)
            .put("BLACK", Color.BLACK)
            .put("RED", Color.RED)
            .put("MAROON", Color.MAROON)
            .put("YELLOW", Color.YELLOW)
            .put("OLIVE", Color.OLIVE)
            .put("LIME", Color.LIME)
            .put("GREEN", Color.GREEN)
            .put("AQUA", Color.AQUA)
            .put("TEAL", Color.TEAL)
            .put("BLUE", Color.BLUE)
            .put("NAVY", Color.NAVY)
            .put("FUCHSIA", Color.FUCHSIA)
            .put("PURPLE", Color.PURPLE)
            .put("ORANGE", Color.ORANGE)
            .build());

    public static DyeColor parseDye(String s) {
        if (s == null) return DyeColor.BLACK;
        try {
            return DyeColor.valueOf(s.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            throw new InvalidConfigException("Cannot find dye color: \"" + s + "\"");
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
            } else {
                color = COLOR_BY_NAME.get(s.toUpperCase());
            }
            if (color == null) {
                throw new InvalidConfigException("Invalid color \"" + s + "\", use \"R;G;B\", \"#RRGGBB\" or color value!");
            }
            else return color;
        } else {
            return Color.fromRGB(parseInt(parts[0]), parseInt(parts[1]), parseInt(parts[2]));
        }
    }

    public static Material legacyAwareMaterialParse(String name) {
        String processedName = name.replace(' ', '_').toUpperCase();

        Material res = Material.getMaterial(processedName);
        if (res != null) return res;

        res = Material.getMaterial(processedName, true);
        if (res != null) {
            Uppercore.logger().severe("------ LEGACY MATERIAL FOUND: '" + processedName + "' PLEASE REPLACE WITH: '" + res.name() + "'");
        }

        return res;
    }

    public static FireworkEffect.Type parseFireworkEffectType(String s) {
        if (s == null)
            throw new InvalidConfigException("Missing firework effect type!");
        try {
            return FireworkEffect.Type.valueOf(s.replace(' ', '_').toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            throw new InvalidConfigException("Cannot find firework effect type: \"" + s + "\"");
        }
    }

    private ConfigUtil() {
    }
}
