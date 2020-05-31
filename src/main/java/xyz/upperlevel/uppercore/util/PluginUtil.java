package xyz.upperlevel.uppercore.util;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.SimplePluginManager;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;

import java.util.function.Consumer;

public final class PluginUtil {
    public static void onPluginLoaded(String pluginName, Consumer<Plugin> callback) {
        if (Bukkit.getPluginManager().isPluginEnabled(pluginName)) {
            Bukkit.getLogger().severe("PLUGIN ENABLED: " + pluginName);
            callback.accept(Bukkit.getPluginManager().getPlugin(pluginName));
        } else {
            Bukkit.getLogger().severe("PLUGIN DISABLED: " + pluginName + " WAITING FOR ACTIVATION");
            Bukkit.getPluginManager().registerEvents(new PluginLoadedListener(pluginName, callback), Uppercore.plugin());
        }
    }

    public static NamespacedKey parseNamespacedKey(String raw) {
        int sepIndex = raw.indexOf(':');
        if (sepIndex == -1) {
            return NamespacedKey.minecraft(raw);
        }

        return new NamespacedKey(raw.substring(0, sepIndex), raw.substring(sepIndex + 1));
    }

    @RequiredArgsConstructor
    private static class PluginLoadedListener implements Listener {
        private final String pluginName;
        private final Consumer<Plugin> callback;

        @EventHandler
        public void onPluginEnable(PluginEnableEvent e) {
            Bukkit.getLogger().severe("ENABLE EVENT: " + pluginName);
            // Check if the plugin name is what we're searching for
            if (!e.getPlugin().getName().equals(pluginName)) return;


            Bukkit.getLogger().severe("FOUND!: " + pluginName);

            // Prepare for destruction (unregister listener)
            HandlerList.unregisterAll(this);

            // Call callback then wait for GC.
            callback.accept(e.getPlugin());
        }
    }

    private PluginUtil() {}
}
