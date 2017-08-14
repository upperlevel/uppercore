package xyz.upperlevel.uppercore.database;

import lombok.Data;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.config.exceptions.InvalidConfigurationException;

import static xyz.upperlevel.uppercore.Uppercore.storages;

@Data
public class Connector {
    private final Plugin plugin;

    private final Storage storage;
    private final String database;
    private final String host;
    private final Integer port;
    private final String user, password;

    private Connection connection;

    public Connector(Plugin plugin, Config config) {
        this.plugin = plugin;
        String stgStr = config.getStringRequired("type");
        this.storage = storages().get(stgStr);
        if (storage == null)
            throw new InvalidConfigurationException("Storage type not supported: " + stgStr);
        this.database = config.has("database") ? config.getString("database") : plugin.getName().toLowerCase();
        this.host = config.getString("host");
        this.port = config.getInt("port", 0);
        this.user = config.getString("user");
        this.password = config.getString("password", "");
    }

    /**
     * If not cached, connects to the configured database with given data.
     */
    public Connection connection() {
        if (connection == null) {
            if (host != null) {
                if (user != null)
                    connection = storage.connect(database, host, port, user, password);
                else
                    connection = storage.connect(database, host, port);
            } else
                connection = storage.connect(database);
        }
        return connection;
    }
}