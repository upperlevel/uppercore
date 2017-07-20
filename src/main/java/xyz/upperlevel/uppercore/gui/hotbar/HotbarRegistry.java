package xyz.upperlevel.uppercore.gui.hotbar;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.config.InvalidConfigurationException;
import xyz.upperlevel.uppercore.config.Config;

import java.io.File;
import java.util.*;
import java.util.logging.Level;


public class HotbarRegistry implements Listener {

    private final Plugin plugin;
    private final File folder;
    private final Map<String, Hotbar> hotbars = new HashMap<>();

    HotbarRegistry(Plugin plugin) {
        this.plugin = plugin;
        this.folder = new File(plugin.getDataFolder(), "hotbars");
        HotbarSystem.register(plugin, this);
    }

    /**
     * Registers the hotbar with the given id.
     *
     * @param id     the id
     * @param hotbar the hotbar
     */
    public void register(String id, Hotbar hotbar) {
        hotbars.put(id, hotbar);
        HotbarSystem.register(plugin, id, hotbar);
    }

    public Hotbar get(String id) {
        return hotbars.get(id);
    }

    public Collection<Hotbar> getHotbars() {
        return hotbars.values();
    }

    /**
     * Loads a new hotbar from the given file.
     *
     * @param file the file where to load the hotbar
     * @return the hotbar loaded
     */
    public Hotbar load(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        String id = file.getName().replaceFirst("[.][^.]+$", "");
        Hotbar hotbar;
        try {
            hotbar = Hotbar.deserialize(plugin, id, Config.wrap(config));
        } catch (InvalidConfigurationException e) {
            Uppercore.logger().severe(e.getErrorMessage("Invalid configuration in file \"" + file + "\""));
            return null;
        } catch (Exception e) {
            Uppercore.logger().log(Level.SEVERE, "Unknown error thrown while reading config in file \"" + file + "\"", e);
            return null;
        }
        register(id, hotbar);
        Uppercore.logger().log(Level.INFO, "Successfully loaded hotbar " + id);
        return hotbar;
    }

    /**
     * Loads all hotbars found in the given folder.
     *
     * @param folder the folder where to load the hotbars
     */
    public void loadFolder(File folder) {
        plugin.getLogger().info("Attempting to load hotbars at: \"" + folder.getPath() + "\"");
        if (folder.exists()) {
            if (folder.isDirectory()) {
                File[] files = folder.listFiles();
                if (files == null) {
                    plugin.getLogger().severe("Error while reading " + folder + " files");
                    return;
                }
                for (File file : files)
                    load(file);
            } else
                plugin.getLogger().severe("\"" + folder.getName() + "\" isn't a folder!");
        } else {
            try {
                folder.mkdirs();
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error creating the directory " + folder.getName(), e);
            }
        }
    }


    public void loadDefaultFolder() {
        loadFolder(folder);
    }
}
