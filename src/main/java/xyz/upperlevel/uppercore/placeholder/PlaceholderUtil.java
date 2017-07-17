package xyz.upperlevel.uppercore.placeholder;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.placeholder.managers.CustomPlaceholderManager;
import xyz.upperlevel.uppercore.placeholder.managers.OfficialPlaceholderManager;

public final class PlaceholderUtil {

    private static PlaceholderManager manager = null;

    private PlaceholderUtil() {
    }

    public static PlaceholderValue<Byte> parseByte(Object obj) {
        if (obj instanceof Number) {
            byte value = ((Number) obj).byteValue();
            return p -> value;
        } else if (obj instanceof String)
            return PlaceholderValue.byteValue((String) obj);
        else return null;
    }

    public static PlaceholderValue<Short> parseShort(Object obj) {
        if (obj instanceof Number) {
            short value = ((Number) obj).shortValue();
            return p -> value;
        } else if (obj instanceof String)
            return PlaceholderValue.shortValue((String) obj);
        else return null;
    }

    public static PlaceholderValue<Integer> parseInt(Object obj) {
        if (obj instanceof Number) {
            int value = ((Number) obj).intValue();
            return p -> value;
        } else if (obj instanceof String)
            return PlaceholderValue.intValue((String) obj);
        else return null;
    }

    public static PlaceholderValue<Long> parseLong(Object obj) {
        if (obj instanceof Number) {
            long value = ((Number) obj).longValue();
            return p -> value;
        } else if (obj instanceof String)
            return PlaceholderValue.longValue((String) obj);
        else return null;
    }

    public static PlaceholderValue<Float> parseFloat(Object obj) {
        if (obj instanceof Number) {
            float value = ((Number) obj).floatValue();
            return p -> value;
        } else if (obj instanceof String)
            return PlaceholderValue.floatValue((String) obj);
        else return null;
    }

    public static PlaceholderValue<Double> parseDouble(Object obj) {
        if (obj instanceof Number) {
            double value = ((Number) obj).doubleValue();
            return p -> value;
        } else if (obj instanceof String)
            return PlaceholderValue.doubleValue((String) obj);
        else return null;
    }

    public static void tryHook() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            manager = new OfficialPlaceholderManager();
            Uppercore.logger().info("Successfully hooked into PlaceholderAPI");
        } else {
            manager = new CustomPlaceholderManager();
            Uppercore.logger().warning("Cannot find PlaceholderAPI");
        }
    }

    public static PlaceholderValue<String> process(String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        return PlaceholderValue.stringValue(message);
    }


    public static String resolvePlaceholders(Player player, String str) {
        return manager.apply(player, str);
    }

    public static String placeholder(Player player, String str) {
        return manager.apply(player, str);
    }

    public static boolean hasPlaceholders(String str) {
        return manager.hasPlaceholders(str);
    }
}
