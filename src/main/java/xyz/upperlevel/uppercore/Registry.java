package xyz.upperlevel.uppercore;

import lombok.Data;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.config.InvalidConfigurationException;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static java.util.Locale.ENGLISH;

@Data
public abstract class Registry<T extends Identifier<?>> {
    private final Plugin plugin;
    private final Logger logger;
    private final String id;
    private final File folder;
    private final Map<String, T> entries = new HashMap<>();

    public Registry(Plugin plugin, String id) {
        this.plugin = plugin;
        this.logger = new RegistryLogger();
        this.id = id;
        this.folder = new File(plugin.getDataFolder(), id);
    }

    public void register(T entry) {
        entries.put(entry.getId(), entry);
    }

    public T unregister(String id) {
        return entries.remove(id.toLowerCase(ENGLISH));
    }

    public T get(String id) {
        return entries.get(id.toLowerCase(ENGLISH));
    }

    public Collection<T> get() {
        return entries.values();
    }

    public void load(File file, Loader<T> loader) {
        logger.info("Attempting to load registrable(s) at: \"" + file.getPath() + "\"");
        if (file.exists()) {
            if (file.isDirectory())
                loadFolder(file, loader);
            else
                loadFile(file, loader);
        } else
            logger.severe("File/directory not found: " + file);
    }

    public void loadFolder(File folder, Loader<T> loader) {
        File[] files = folder.listFiles();
        if (files == null) {
            logger.severe("Error while reading files in: \"" + folder + "\"");
            return;
        }
        for (File sub : files)
            load(sub, loader);
    }

    public T loadFile(File file, Loader<T> loader) {
        String id = file.getName().replaceFirst("[.][^.]+$", "");
        T entry;
        try {
            entry = loader.load(plugin, id, file);
        } catch (InvalidConfigurationException e) {
            e.addLocalizer("in registrable " + id);
            throw e;
        }
        register(entry);
        postLoad(file, entry);
        return entry;
    }

    protected void postLoad(File in, T out) {
        logger.info("Successfully loaded registrable: \"" + out.getId() + "\"");
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
