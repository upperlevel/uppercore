package xyz.upperlevel.uppercore.placeholder;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.placeholder.events.PlaceholderManagerHookEvent;
import xyz.upperlevel.uppercore.placeholder.managers.CustomPlaceholderManager;
import xyz.upperlevel.uppercore.placeholder.managers.PapiPlaceholderManager;
import xyz.upperlevel.uppercore.util.PluginUtil;

public final class PlaceholderUtil {
    @Getter
    @Setter
    private static PlaceholderManager manager = null;
    private static boolean fallback = false;

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
        PlaceholderManagerHookEvent event = new PlaceholderManagerHookEvent();
        Bukkit.getPluginManager().callEvent(event);
        if (event.getPlaceholderManager() != null) {
            manager = event.getPlaceholderManager();
            Uppercore.logger().info("Successfully hooked with custom PlaceholderManager");
        } else if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            manager = new PapiPlaceholderManager();
            Uppercore.logger().info("Successfully hooked into PlaceholderAPI");
        } else {
            manager = new CustomPlaceholderManager();
            Uppercore.logger().warning("Cannot find PlaceholderAPI, using internal system.");
            fallback = true;
            PluginUtil.onPluginLoaded("PlaceholderAPI", PlaceholderUtil::onPapiEnable);
        }
    }

    public static void onPapiEnable(Plugin plugin) {
        if (!fallback) return; // Another manager has been registered (and it is not a fallback)
        Uppercore.logger().info("Late hooking into PlaceholderAPI");
        manager = new PapiPlaceholderManager();
    }

    public static void register(Plugin plugin, Placeholder placeholder) {
        manager.register(plugin, placeholder);
    }

    public static PlaceholderValue<String> process(String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        return PlaceholderValue.stringValue(message);
    }

    public static String resolve(Player player, String str) {
        return manager.apply(player, str);
    }

    public static String resolve(Player player, String str, PlaceholderRegistry local) {
        return manager.apply(player, str, local);
    }

    public static String placeholder(Player player, String str) {
        return manager.apply(player, str);
    }

    public static boolean hasPlaceholders(String str) {
        return manager.hasPlaceholders(str);
    }

    public static PlaceholderRegistry getRegistry() {
        return manager.getRegistry();
    }
}
