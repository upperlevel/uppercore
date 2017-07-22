package xyz.upperlevel.uppercore.placeholder;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.placeholder.managers.CustomPlaceholderManager;
import xyz.upperlevel.uppercore.placeholder.managers.OfficialPlaceholderManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class PlaceholderUtil {

    private static PlaceholderManager manager = null;

    private PlaceholderUtil() {
    }

    public static PlaceholderValue<Byte> parseByte(Object obj) {
        if (obj instanceof Number) {
            return PlaceholderValue.fake(((Number) obj).byteValue());
        } else if (obj instanceof String)
            return PlaceholderValue.byteValue((String) obj);
        else return null;
    }

    public static PlaceholderValue<Short> parseShort(Object obj) {
        if (obj instanceof Number) {
            return PlaceholderValue.fake(((Number) obj).shortValue());
        } else if (obj instanceof String)
            return PlaceholderValue.shortValue((String) obj);
        else return null;
    }

    public static PlaceholderValue<Integer> parseInt(Object obj) {
        if (obj instanceof Number) {
            return PlaceholderValue.fake(((Number) obj).intValue());
        } else if (obj instanceof String)
            return PlaceholderValue.intValue((String) obj);
        else return null;
    }

    public static PlaceholderValue<Long> parseLong(Object obj) {
        if (obj instanceof Number) {
            return PlaceholderValue.fake(((Number) obj).longValue());
        } else if (obj instanceof String)
            return PlaceholderValue.longValue((String) obj);
        else return null;
    }

    public static PlaceholderValue<Float> parseFloat(Object obj) {
        if (obj instanceof Number) {
            return PlaceholderValue.fake(((Number) obj).floatValue());
        } else if (obj instanceof String)
            return PlaceholderValue.floatValue((String) obj);
        else return null;
    }

    public static PlaceholderValue<Double> parseDouble(Object obj) {
        if (obj instanceof Number) {
            return PlaceholderValue.fake(((Number) obj).doubleValue());
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

    public static void register(Plugin plugin, Placeholder placeholder) {
        manager.register(plugin, placeholder);
    }

    public static PlaceholderValue<String> process(String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        return PlaceholderValue.stringValue(message);
    }

    public static PlaceholderValue<String> process(String message, Map<String, Placeholder> local) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        return PlaceholderValue.stringValue(message, local);
    }

    public static PlaceholderValue<String> process(String message, Set<String> local) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        return PlaceholderValue.stringValue(message, local);
    }

    public static PlaceholderValue<String> process(String message, String... local) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        return PlaceholderValue.stringValue(message, local);
    }


    public static String resolve(Player player, String str) {
        return manager.apply(player, str);
    }

    public static String resolve(Player player, String str, Map<String, Placeholder> local) {
        return manager.apply(player, str, local);
    }

    public static String resolveRaw(Player player, String str, Map<String, String> local) {
        return manager.applyRaw(player, str, local);
    }

    public static String placeholder(Player player, String str) {
        return manager.apply(player, str);
    }

    public static boolean hasPlaceholders(String str) {
        return manager.hasPlaceholders(str);
    }

    public static boolean hasPlaceholders(String str, Map<String, Placeholder> local) {
        return manager.hasPlaceholders(str, local);
    }

    public static boolean hasPlaceholders(String str, Set<String> local) {
        return manager.hasPlaceholders(str, local);
    }

    public static boolean hasPlaceholders(String str, String... local) {
        return manager.hasPlaceholders(str, new HashSet<>(Arrays.asList(local)));
    }
}
