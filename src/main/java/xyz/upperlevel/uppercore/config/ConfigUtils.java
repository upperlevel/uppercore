package xyz.upperlevel.uppercore.config;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import xyz.upperlevel.uppercore.config.InvalidConfigurationException;

import java.util.Locale;

import static java.lang.Integer.parseInt;

public final class ConfigUtils {

    private ConfigUtils() {
    }

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
        if (parts.length != 3)
            throw new InvalidConfigurationException("Invalid color format, use \"R;G;B\"");
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
}
