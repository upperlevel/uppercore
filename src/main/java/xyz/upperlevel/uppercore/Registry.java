package xyz.upperlevel.uppercore;

import lombok.Data;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.board.Board;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.config.InvalidConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static java.util.Locale.ENGLISH;

@Data
public abstract class Registry<T> {
    private final Plugin plugin;
    private final Logger logger;
    private final String id;
    private final File folder;
    private final Map<String, Identifier<T>> entries = new HashMap<>();

    public Registry(Plugin plugin, String id) {
        this.plugin = plugin;
        this.logger = new RegistryLogger();
        this.id = id;
        this.folder = new File(plugin.getDataFolder(), id);
    }

    public Identifier<T> register(String id, T entry) {
        Identifier<T> result = new Identifier<>(plugin, id, entry);
        entries.put(id, result);
        return result;
    }

    public Identifier<T> unregister(String id) {
        return entries.remove(id.toLowerCase(ENGLISH));
    }

    public Identifier<T> get(String id) {
        return entries.get(id);
    }

    public Collection<Identifier<T>> get() {
        return entries.values();
    }

    public void load(File file, Loader<T> loader) {
        logger.info("Attempting to load entry(s) at: \"" + file.getPath() + "\"");
        if (file.exists()) {
            if (!file.isDirectory()) {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                String id = file.getName().replaceFirst("[.][^.]+$", "");
                T entry;
                try {
                    entry = loader.load(Config.wrap(config));
                } catch (InvalidConfigurationException e) {
                    e.addLocalizer("in entry " + id);
                    throw e;
                }
                register(id, entry);
                logger.info("Successfully loaded entry: \"" + id + "\"");
            } else {
                File[] files = folder.listFiles();
                if (files == null) {
                    logger.severe("Error while reading files in: \"" + folder + "\"");
                    return;
                }
                for (File sub : files)
                    load(sub, loader);
            }
        } else {
            logger.warning("File/directory not exists: " + file);
            if (file.isDirectory()) {
                file.mkdirs();
            } else {
                try {
                    if (file.getParentFile() != null)
                        file.getParentFile().mkdirs();
                    file.createNewFile();
                } catch (IOException ignored) {
                }
            }
            logger.info("File/directory created!");
        }
    }

    private class RegistryLogger extends java.util.logging.Logger {
        public RegistryLogger() {
            super(Registry.class.getClass().getCanonicalName(), null);
        }

        @Override
        public void log(LogRecord record) {
            record.setMessage("[" + plugin.getName() + "] " + "[" + id + "] " + record.getMessage());
            super.log(record);
        }
    }
}
