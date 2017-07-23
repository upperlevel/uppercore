package xyz.upperlevel.uppercore.gui;

import lombok.Data;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.config.InvalidConfigurationException;
import xyz.upperlevel.uppercore.config.Config;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

@Data
public class GuiRegistry {

    private final Plugin plugin;
    private final File folder;
    private final Map<String, Gui> guis = new HashMap<>();

    GuiRegistry(Plugin plugin) {
        this.plugin = plugin;
        this.folder = new File(plugin.getDataFolder(), "guis");
        GuiSystem.register(plugin, this);
    }

    public GuiRegistry register(String id, Gui gui) {
        guis.put(id, gui);
        GuiSystem.register(plugin, id, gui);
        return this;
    }

    public Gui get(String id) {
        return guis.get(id);
    }

    public Collection<Gui> getGuis() {
        return guis.values();
    }

    /**
     * Loads the given gui file configuration.
     *
     * @param file loads the given file
     */
    public Gui load(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        String id = file.getName().replaceFirst("[.][^.]+$", "");
        ChestGui gui;
        try {
            gui = ChestGui.deserialize(plugin, id, Config.wrap(config));
        } catch (InvalidConfigurationException e) {
            e.addLocalizer("in gui " + id);
            throw e;
        }
        register(id, gui);
        plugin.getLogger().log(Level.INFO, "Successfully loaded gui " + id);
        return gui;
    }

    /**
     * Loads a folder that contains gui configurations.
     * This will not delete present guis.
     *
     * @param folder the folder to load
     */
    public void loadFolder(File folder) {
        plugin.getLogger().info("Attempting to load guis at: \"" + folder.getPath() + "\"");
        if (folder.exists()) {
            if (folder.isDirectory()) {
                File[] files = folder.listFiles();
                if (files == null) {
                    plugin.getLogger().severe("Error while reading " + folder + " files");
                    return;
                }
                for (File file : files)
                    load(file);
            } else {
                plugin.getLogger().severe("\"" + folder.getName() + "\" isn't a folder!");
            }
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