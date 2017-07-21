package xyz.upperlevel.uppercore.config;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static java.lang.Integer.parseInt;

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
            Color color = COLOR_BY_NAME.get(s.toUpperCase());
            if(color == null)
                throw new InvalidConfigurationException("Invalid color format, use \"R;G;B\" or color name!");
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

    private ConfigUtils() {}
}
