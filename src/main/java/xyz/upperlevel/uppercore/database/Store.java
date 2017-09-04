package xyz.upperlevel.uppercore.database;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import lombok.Data;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.config.ConfigUtils;

import java.io.File;
import java.util.logging.Logger;

@Data
public class Store {
    private static final String CONFIG_NAME = "store.yml";

    private final Plugin plugin;
    private final Logger logger;
    private final File file;

    private Connector connector;

    public Store(Plugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.file = new File(plugin.getDataFolder(), "store.yml");
        load();
    }

    /**
     * Loads configuration from plugin file.
     */
    private boolean load() {
        if (!file.exists()) {
            if (plugin.getResource(CONFIG_NAME) != null) {
                plugin.saveResource(CONFIG_NAME, false);
                logger.info("Resource \"" + CONFIG_NAME + "\" has been saved.");
            } else {
                logger.severe("Cannot find \"" + CONFIG_NAME + "\" resources.");
                return false;
            }
        }
        connector = new Connector(plugin, Config.wrap(ConfigUtils.loadConfig(file)).getConfigRequired("storage"));
        logger.info("\"" + CONFIG_NAME + "\" file loaded successfully!");
        return true;
    }

    /**
     * Connects to the configured database read in configuration.
     */
    public Connection connection() {
        if (connector == null)
            throw new IllegalStateException("Nothing loaded, cannot connect!");
        return connector.connection();
    }
}
