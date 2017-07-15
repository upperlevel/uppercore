package xyz.upperlevel.uppercore.gui;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.gui.config.InvalidGuiConfigurationException;
import xyz.upperlevel.uppercore.gui.config.util.Config;
import xyz.upperlevel.uppercore.gui.events.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

/**
 * This manager is the class that manages the player chronology in a stack-like system
 * it has multiple operations for interacting with the Gui stack:
 * open: appends the gui to the stack
 * close: clears the stack
 * back: removes the last gui from the stack
 * change: back + open
 * It's suggested NOT to chain operations when not needed, it could cause client flickering
 * <p>
 * This system does not support recursion
 */
public class GuiManager {

    private static final Map<String, Gui> guis = new HashMap<>();
    private static final Map<Player, LinkedList<Gui>> chronology = new HashMap<>();

    @Getter
    private static boolean called = false;

    /**
     * Loads the given gui file configuration.
     *
     * @param file loads the given file
     */
    public static Gui load(File file) {
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException e) {
            Uppercore.logger().log(Level.SEVERE, "Error while loading the file \"" + file + "\"", e);
            return null;
        } catch (InvalidConfigurationException e) {
            Uppercore.logger().log(Level.SEVERE, "Invalid configuration in file \"" + file + "\":", e);
            return null;
        }
        String id = file.getName().replaceFirst("[.][^.]+$", "");
        CustomGui gui;
        try {
            gui = CustomGui.deserialize(id, Config.wrap(config));
        } catch (InvalidGuiConfigurationException e) {
            Uppercore.logger().severe(e.getErrorMessage("Invalid configuration in file \"" + file + "\""));
            return null;
        } catch (Exception e) {
            Uppercore.logger().log(Level.SEVERE, "Unknown error thrown while reading config in file \"" + file + "\"", e);
            return null;
        }
        register(id, gui);
        Uppercore.logger().log(Level.INFO, "Successfully loaded gui " + id);
        return gui;
    }

    /**
     * Loads a folder that contains gui configurations.
     * This will not delete present guis.
     *
     * @param folder the folder to load
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
            } else {
                Uppercore.logger().severe("\"" + folder.getName() + "\" isn't a folder!");
            }
        } else {
            try {
                folder.mkdirs();
            } catch (Exception e) {
                Uppercore.logger().log(Level.SEVERE, "Error creating the directory " + folder.getName(), e);
            }
        }
    }

    /**
     * Registers the given gui with a custom id.
     *
     * @param id  the id to associate to the gui
     * @param gui the gui to register
     */
    public static void register(String id, Gui gui) {
        guis.put(id, gui);
    }

    /**
     * Registers the given gui.
     *
     * @param gui the gui to register
     */
    public static void register(Gui gui) {
        guis.put(gui.getId(), gui);
    }

    /**
     * Gets a gui by given id.
     *
     * @param id the gui's id
     * @return the gui fetched by id
     */
    public static Gui get(String id) {
        return guis.get(id);
    }

    /**
     * Gets all the registered guis
     *
     * @return a unmodifiable collection representing all the guis
     */
    public static Collection<Gui> getGuis() {
        return Collections.unmodifiableCollection(guis.values());
    }

    /**
     * Unregisters the given gui by id.
     *
     * @param id the id of the gui to unregister
     * @return the gui unregistered
     */
    public static Gui unregister(String id) {
        return guis.remove(id);
    }

    /**
     * Unregisters the given gui.
     *
     * @param gui the gui to unregister
     * @return the gui unregistered
     */
    public static Gui unregister(Gui gui) {
        return guis.remove(gui.getId());
    }

    /**
     * Opens a Gui to a player, adding it to the stack. If the closeOthers parameter is specified it will removeHotbar the stack first
     *
     * @param player      the player that is opening the api
     * @param gui         the gui to be opened
     * @param closeOthers if give to true the GUI history would be cleaned
     */
    public static void open(Player player, Gui gui, boolean closeOthers) {
        if (called) return;
        called = true;
        try {
            LinkedList<Gui> g = getOrCreate(player);
            Gui oldGui = g.peek();
            GuiOpenEvent e = new GuiOpenEvent(player, gui, oldGui);
            e.setCloseOthers(closeOthers);
            Bukkit.getPluginManager().callEvent(e);
            if (e.isCancelled()) {
                if (g.isEmpty())
                    chronology.remove(player);
                return;
            }
            closeOthers = e.isCloseOthers();
            if (oldGui != null)
                oldGui.onClose(player);
            if (closeOthers)
                g.clear();

            gui.onOpen(player);
            gui.show(player);
            g.push(gui);
        } finally {
            called = false;
        }
    }

    /**
     * Opens a Gui to a player, adding it to the stack
     *
     * @param player the player that is opening the api
     * @param gui    the gui to be opened
     */
    public static void open(Player player, Gui gui) {
        open(player, gui, false);
    }

    /**
     * Closes *ALL* the player's Guis, clearing his stack history
     *
     * @param player the player
     */
    public static void close(Player player) {
        if (called) return;
        called = true;
        try {
            LinkedList<Gui> g = chronology.remove(player);
            if (g == null || g.isEmpty())
                return;

            Gui oldGui = g.peek();

            GuiCloseEvent e = new GuiCloseEvent(player, oldGui);
            Bukkit.getPluginManager().callEvent(e);

            if (e.isCancelled()) {
                chronology.put(player, g);
                return;
            }

            oldGui.onClose(player);
            player.closeInventory();
            g.clear();
        } finally {
            called = false;
        }
    }

    public static void closeAll() {
        if (called) return;
        called = true;
        try {
            for (Player player : chronology.keySet())
                player.closeInventory();
            chronology.clear();
        } finally {
            called = false;
        }
    }

    /**
     * Closes only the currently open Gui, opening the previous Gui in the stack if present, otherwise it will close the player's inventory
     *
     * @param player the player
     */
    public static void back(Player player) {
        if (called) return;
        called = true;
        try {
            LinkedList<Gui> g = chronology.get(player);
            if (g == null || g.isEmpty())
                return;

            Gui oldGui = g.pop();
            Gui gui = g.peek();

            GuiBackEvent event = new GuiBackEvent(player, gui, oldGui);
            Bukkit.getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                g.push(oldGui);
                return;
            }

            oldGui.onClose(player);
            if (!g.isEmpty()) {
                gui.onOpen(player);
                gui.show(player);
            } else
                player.closeInventory();
        } finally {
            called = false;
        }
    }

    /**
     * Changes the last Gui in the player's stack (if any) with the one specified in the arguments, this could be thought as back + open
     *
     * @param player the player
     * @param gui    the Gui that will be appended instead of the last one
     */
    public static void change(Player player, Gui gui) {
        if (called) return;
        called = true;
        try {
            LinkedList<Gui> g = getOrCreate(player);
            Gui oldGui;
            if (!g.isEmpty())
                oldGui = g.pop();
            else oldGui = null;

            GuiChangeEvent e = new GuiChangeEvent(player, gui, oldGui);
            Bukkit.getPluginManager().callEvent(e);
            if (e.isCancelled()) {
                if (oldGui != null)
                    g.push(oldGui);
                return;
            }
            if (oldGui != null)
                oldGui.onClose(player);
            g.push(gui);
            gui.onOpen(player);
            gui.show(player);
        } finally {
            called = false;
        }
    }

    /**
     * Called when a player clicks on the inventory, the filters to check if the Player clicked on the GUi's inventory should be made outside of this method
     *
     * @param event the click event
     */
    public static void onClick(InventoryClickEvent event) {
        HumanEntity h = event.getWhoClicked();
        if (!(h instanceof Player))
            return;
        LinkedList<Gui> g = chronology.get(h);
        if (g != null && !g.isEmpty()) {
            Gui gui = g.peek();
            GuiClickEvent e = new GuiClickEvent(event, (Player) h, gui);
            Bukkit.getPluginManager().callEvent(e);
            if (e.isCancelled())
                return;

            //Event cancelled BEFORE the method call to permit the un-cancelling
            event.setCancelled(true);
            gui.onClick(event);
            //Creative idiots could copy the links
            if (event.isShiftClick() && event.getWhoClicked().getGameMode() == GameMode.CREATIVE)
                ((Player) event.getWhoClicked()).updateInventory();
        }
    }

    public static void reprint(Player player) {
        if (called) return;
        called = true;
        try {
            LinkedList<Gui> g = get(player);
            if(g == null)
                return;
            Gui gui = g.peek();
            if(gui != null)
                gui.show(player);
        } finally {
            called = false;
        }
    }

    /**
     * Gets the Gui history (also called stack) of the player. If
     *
     * @param player the player
     * @return the player's Gui history
     */
    public static LinkedList<Gui> get(Player player) {
        return chronology.get(player);
    }

    private static LinkedList<Gui> getOrCreate(Player player) {
        return chronology.computeIfAbsent(player, (pl) -> new LinkedList<>());
    }

    public static Map<Player, LinkedList<Gui>> getChronology() {
        return Collections.unmodifiableMap(chronology);
    }
}
