package xyz.upperlevel.uppercore.scoreboard;

import lombok.Data;
import lombok.Getter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.gui.config.InvalidGuiConfigurationException;
import xyz.upperlevel.uppercore.util.Config;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

@Data
public class ScoreboardRegistry {

    private final Plugin plugin;
    private final File folder;
    private final Map<String, Scoreboard> scoreboards = new HashMap<>();

    public ScoreboardRegistry(Plugin plugin) {
        this.plugin = plugin;
        this.folder = new File(plugin.getDataFolder(), "scoreboards");
        ScoreboardSystem.register(this);
    }

    public void register(Scoreboard scoreboard) {
        scoreboards.put(scoreboard.getId(), scoreboard);
        ScoreboardSystem.register(scoreboard);
    }

    public Scoreboard get(String id) {
        return scoreboards.get(id);
    }

    public Collection<Scoreboard> getScoreboards() {
        return scoreboards.values();
    }

    public Scoreboard load(File file) {
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while loading the file \"" + file + "\"", e);
            return null;
        } catch (InvalidConfigurationException e) {
            plugin.getLogger().log(Level.SEVERE, "Invalid configuration in file \"" + file + "\":", e);
            return null;
        }
        String id = file.getName().replaceFirst("[.][^.]+$", "");
        Scoreboard scoreboard;
        try {
            scoreboard = Scoreboard.deserialize(plugin, id, Config.wrap(config));
        } catch (InvalidGuiConfigurationException e) {
            plugin.getLogger().severe(e.getErrorMessage("Invalid configuration in file \"" + file + "\""));
            return null;
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Unknown error thrown while reading config in file \"" + file + "\"", e);
            return null;
        }
        register(scoreboard);
        plugin.getLogger().log(Level.INFO, "Successfully loaded gui " + id);
        return scoreboard;
    }

    public void loadFolder(File folder) {
        plugin.getLogger().info("Attempting to load scoreboards at: \"" + folder.getPath() + "\"");
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

    public void loadFolder() {
        loadFolder(folder);
    }
}
