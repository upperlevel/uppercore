package xyz.upperlevel.uppercore.database;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.config.ConfigUtil;

import java.io.File;
import java.util.logging.Logger;

public class StorageConfiguration {
    private static final String CONFIG_FILENAME = "storage.yml";

    @Getter
    private Storage storage;

    @Getter
    private StorageLocation location;

    private StorageConfiguration(Storage storage, StorageLocation location) {
        this.storage = storage;
        this.location = location;
    }

    /**
     * Tries to download the drivers if not found, finally connects.
     */
    public Database connect() {
        if (!storage.isSupported()) {
            storage.download();
        }
        return location.connect(storage);
    }

    /**
     * Loads a {@link StorageConfiguration} from the file: "storage.yml".
     * To associate the name of the storage, to the {@link Storage} class, needs a {@link StorageManager}.
     */
    public static StorageConfiguration load(StorageManager storageManager, Plugin plugin) {
        Logger logger = plugin.getLogger();
        File file = new File(plugin.getDataFolder(), CONFIG_FILENAME);
        if (!file.exists()) {
            logger.info(CONFIG_FILENAME + " does not exist");
            if (plugin.getResource(CONFIG_FILENAME) != null) {
                plugin.saveResource(CONFIG_FILENAME, false);
                logger.info(CONFIG_FILENAME + " extracted from jar");
            } else {
                logger.severe("Cannot find resource " + CONFIG_FILENAME + " in jar");
            }
        }
        Config config = Config.wrap(ConfigUtil.loadConfig(file)).getConfigRequired("storage");
        String storageName = config.getStringRequired("type");
        Storage storage = storageManager.getStorage(storageName);
        if (storage == null) {
            throw new IllegalArgumentException("No storage found for: " + storageName);
        }
        return new StorageConfiguration(storage, StorageLocation.load(config));
    }
}
