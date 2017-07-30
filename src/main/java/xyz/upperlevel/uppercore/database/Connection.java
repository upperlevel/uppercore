package xyz.upperlevel.uppercore.database;

import org.bukkit.plugin.Plugin;

public interface Connection {
    default Database database(Plugin plugin) {
        return database(plugin.getName());
    }

    Database database(String id);

    void disconnect();
}
