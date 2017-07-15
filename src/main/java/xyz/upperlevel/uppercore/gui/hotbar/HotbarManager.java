package xyz.upperlevel.uppercore.gui.hotbar;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.gui.Icon;
import xyz.upperlevel.uppercore.gui.config.InvalidGuiConfigurationException;
import xyz.upperlevel.uppercore.gui.config.util.Config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class HotbarManager {

    private static final Map<String, Hotbar> hotbars = new HashMap<>();
    private static final Map<Player, HotbarView> views = new HashMap<>();

    private static void joinPlayer(Player player) {
        HotbarView v = new HotbarView(player);
        views.put(player, v);
        for (Hotbar hotbar : hotbars.values())
            if (hotbar.isOnJoin())
                v.addHotbar(hotbar);
    }

    private static void quitPlayer(Player player) {
        HotbarView v = views.remove(player);
        if (v != null) v.clear();
    }

    /**
     * Must be called, initializes the plugin.
     */
    public static void initialize() {
        // registers a hotbar for all players online and registers a listener
        // to register or removeHotbar join and quit players
        Bukkit.getOnlinePlayers().forEach(HotbarManager::joinPlayer);
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent e) {
                joinPlayer(e.getPlayer());
            }

            @EventHandler
            public void onQuit(PlayerQuitEvent e) {
                quitPlayer(e.getPlayer());
            }
        }, Uppercore.get());
    }

    /**
     * Registers the given hotbar with the associated id.
     *
     * @param id     the id of the hotbar to register
     * @param hotbar the hotbar to register
     */
    public static void register(String id, Hotbar hotbar) {
        hotbars.put(id, hotbar);
    }

    /**
     * Registers the given hotbar.
     *
     * @param hotbar the hotbar to register
     */
    public static void register(Hotbar hotbar) {
        hotbars.put(hotbar.getId(), hotbar);
    }

    /**
     * Unregisters the hotbar by its id.
     *
     * @param id the id of the hotbar to removeHotbar
     * @return the hotbar removed
     */
    public static Hotbar unregister(String id) {
        return hotbars.remove(id);
    }

    public static Hotbar unregister(Hotbar hotbar) {
        return hotbars.remove(hotbar.getId());
    }

    /**
     * Gets a hotbar by its id.
     *
     * @param id the id of the hotbar
     * @return the hotbar fetched
     */
    public static Hotbar get(String id) {
        return hotbars.get(id);
    }

    /**
     * Gets a hotbar view by its player.
     *
     * @param player the player holding the hotbar
     * @return the hotbar held
     */
    public static HotbarView get(Player player) {
        return views.computeIfAbsent(player, HotbarView::new);
    }

    /**
     * Loads a new hotbar from the given file.
     *
     * @param file the file where to load the hotbar
     * @return the hotbar loaded
     */
    public static Hotbar load(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        final String id = file.getName().replaceFirst("[.][^.]+$", "");
        Hotbar hotbar;
        try {
            hotbar = Hotbar.deserialize(id, Config.wrap(config));
        } catch (InvalidGuiConfigurationException e) {
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
    public static void loadFolder(File folder) {
        if (folder.exists()) {
            if (folder.isDirectory()) {
                File[] files = folder.listFiles();
                if (files == null) {
                    Uppercore.logger().severe("Error while reading " + folder + " files");
                    return;
                }
                for (File file : files)
                    load(file);
            } else
                Uppercore.logger().severe("\"" + folder.getName() + "\" isn't a folder!");
        } else {
            try {
                folder.mkdirs();
            } catch (Exception e) {
                Uppercore.logger().log(Level.SEVERE, "Error creating the directory " + folder.getName(), e);
            }
        }
    }

    /**
     * Checks if the given player is holding the given hotbar.
     *
     * @param player the player
     * @param hotbar the hotbar
     * @return true if is holding the passed hotbar, otherwise false
     */
    public static boolean isHolding(Player player, Hotbar hotbar) {
        return get(player).isHolding(hotbar);
    }

    /**
     * Removes an hotbar from a player.
     *
     * @param player the player
     */
    public static void remove(Player player) {
        get(player).clear();
    }

    public static boolean onClick(PlayerInteractEvent event) {
        if (onClick(event.getPlayer(), event.getPlayer().getInventory().getHeldItemSlot())) {
            event.setCancelled(true);
            return true;
        }
        return false;
    }

    public static boolean onClick(Player player, int slot) {
        Icon icon = get(player).getIcon(slot);
        if (icon == null || icon.getLink() == null)
            return false;
        icon.getLink().run(player);
        return true;
    }

    public static void clearAll() {
        views.keySet().forEach(HotbarManager::remove);
        views.clear();
    }
}
