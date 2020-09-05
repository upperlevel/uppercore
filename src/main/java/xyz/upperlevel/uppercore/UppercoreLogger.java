package xyz.upperlevel.uppercore;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class UppercoreLogger extends Logger {
    public static final Map<Level, ChatColor> colorByLevel = new HashMap<Level, ChatColor>() {{
        put(Level.SEVERE, ChatColor.RED);
        put(Level.WARNING, ChatColor.YELLOW);
    }};

    private final String pluginName;

    public UppercoreLogger(Plugin plugin) {
        super(plugin.getClass().getCanonicalName(), null);
        String prefix = plugin.getDescription().getPrefix();
        this.pluginName = prefix != null ? "[" + prefix + "] " : "[" + plugin.getDescription().getName() + "] ";
        this.setParent(plugin.getServer().getLogger());
        this.setLevel(Level.ALL);
    }


    @Override
    public void log(@NotNull LogRecord logRecord) {
        ChatColor color = colorByLevel.get(logRecord.getLevel());
        Bukkit.getConsoleSender().sendMessage((color != null ? color : "") + this.pluginName + logRecord.getMessage());
    }
}
