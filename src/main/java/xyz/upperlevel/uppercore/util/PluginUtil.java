package xyz.upperlevel.uppercore.util;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
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
            callback.accept(Bukkit.getPluginManager().getPlugin(pluginName));
        } else {
            Bukkit.getPluginManager().registerEvents(new PluginLoadedListener(pluginName, callback), Uppercore.plugin());
        }
    }

    @RequiredArgsConstructor
    private static class PluginLoadedListener implements Listener {
        private final String pluginName;
        private final Consumer<Plugin> callback;

        @EventHandler
        public void onEnable(PluginEnableEvent e) {
            // Check if the plugin name is what we're searching for
            if (!e.getPlugin().getName().equals(pluginName)) return;

            // Prepare for destruction (unregister listener)
            HandlerList.unregisterAll(this);

            // Call callback then wait for GC.
            callback.accept(e.getPlugin());
        }
    }

    private PluginUtil() {}
}
