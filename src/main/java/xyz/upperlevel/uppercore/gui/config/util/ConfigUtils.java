package xyz.upperlevel.uppercore.gui.config.util;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import xyz.upperlevel.uppercore.gui.config.InvalidGuiConfigurationException;

import java.util.Locale;

import static java.lang.Integer.parseInt;

public final class ConfigUtils {
    public static DyeColor parseDye(String s) {
        if(s == null) return DyeColor.BLACK;
        try {
            return DyeColor.valueOf(s.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            throw new InvalidGuiConfigurationException("Cannot find dye color: \"" + s + "\"");
        }
    }

    public static Color parseColor(String s) {
        String[] parts = s.split(";");
        if(parts.length != 3)
            throw new InvalidGuiConfigurationException("Invalid color format, use \"R;G;B\"");
        return Color.fromRGB(parseInt(parts[0]), parseInt(parts[1]), parseInt(parts[2]));
    }

    public static FireworkEffect.Type parseFireworkEffectType(String s) {
        if(s == null)
            throw new InvalidGuiConfigurationException("Missing firework effect type!");
        try {
            return FireworkEffect.Type.valueOf(s.replace(' ', '_').toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            throw new InvalidGuiConfigurationException("Cannot find firework effect type: \"" + s + "\"");
        }
    }

    private ConfigUtils(){}
}
