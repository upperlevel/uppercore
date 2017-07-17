package xyz.upperlevel.uppercore.gui;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.gui.events.*;

import java.util.*;

import static java.util.Locale.ENGLISH;

/**
 * This manager is the class that manages the player histories in a stack-like system
 * it has multiple operations for interacting with the Gui stack:
 * openGui: appends the gui to the stack
 * closeGui: clears the stack
 * backGui: removes the last gui from the stack
 * changeGui: backGui + openGui
 * It's suggested NOT to chain operations when not needed, it could cause client flickering
 * <p>
 * This system does not support recursion
 */
public final class GuiManager {

    private static final Map<String, Gui> guis = new HashMap<>();
    private static final Map<Plugin, GuiRegistry> registries = new HashMap<>();

    private static final Map<Player, LinkedList<Gui>> histories = new HashMap<>();

    @Getter
    private static boolean called = false;

    private GuiManager() {
    }

    private static String adaptId(String id) {
        return id.toLowerCase(Locale.ENGLISH);
    }

    private static String obtainId(Plugin plugin, String id) {
        return adaptId((plugin.getName() + ":" + id));
    }

    public static void register(Plugin plugin, String id, Gui gui) {
        guis.put(obtainId(plugin, id), gui);
    }

    public static void register(Plugin plugin, GuiRegistry registry) {
        registries.put(plugin, registry);
    }

    public static Gui getGui(String id) {
        return guis.get(adaptId(id));
    }

    public static Gui getGui(Plugin plugin, String id) {
        GuiRegistry reg = registries.get(plugin);
        if (reg != null)
            return reg.getGui(id);
        return null;
    }

    public static GuiRegistry getRegistry(Plugin plugin) {
        return registries.get(plugin);
    }

    public static Collection<Gui> getGuis() {
        return guis.values();
    }

    /**
     * Opens a Gui to a player, adding it to the stack. If the closeOthers parameter is specified it will removeHotbar the stack first
     *
     * @param player      the player that is opening the api
     * @param gui         the gui to be opened
     * @param closeOthers if give to true the GUI histories would be cleaned
     */
    public static void openGui(Player player, Gui gui, boolean closeOthers) {
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
                    histories.remove(player);
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
    public static void openGui(Player player, Gui gui) {
        openGui(player, gui, false);
    }

    /**
     * Closes *ALL* the player's Guis, clearing his stack histories
     *
     * @param player the player
     */
    public static void closeGui(Player player) {
        if (called) return;
        called = true;
        try {
            LinkedList<Gui> g = histories.remove(player);
            if (g == null || g.isEmpty())
                return;

            Gui oldGui = g.peek();

            GuiCloseEvent e = new GuiCloseEvent(player, oldGui);
            Bukkit.getPluginManager().callEvent(e);

            if (e.isCancelled()) {
                histories.put(player, g);
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
            for (Player player : histories.keySet())
                player.closeInventory();
            histories.clear();
        } finally {
            called = false;
        }
    }

    /**
     * Closes only the currently openGui Gui, opening the previous Gui in the stack if present, otherwise it will closeGui the player's inventory
     *
     * @param player the player
     */
    public static void backGui(Player player) {
        if (called) return;
        called = true;
        try {
            LinkedList<Gui> g = histories.get(player);
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
     * Changes the last Gui in the player's stack (if any) with the one specified in the arguments, this could be thought as backGui + openGui
     *
     * @param player the player
     * @param gui    the Gui that will be appended instead of the last one
     */
    public static void changeGui(Player player, Gui gui) {
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
        LinkedList<Gui> g = histories.get(h);
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
            LinkedList<Gui> g = getHistory(player);
            if (g == null)
                return;
            Gui gui = g.peek();
            if (gui != null)
                gui.show(player);
        } finally {
            called = false;
        }
    }

    /**
     * Gets gui history (also called stack) of a player.
     *
     * @param player the player
     * @return the player's Gui histories
     */
    public static LinkedList<Gui> getHistory(Player player) {
        return histories.get(player);
    }

    private static LinkedList<Gui> getOrCreate(Player player) {
        return histories.computeIfAbsent(player, (pl) -> new LinkedList<>());
    }

    public static Map<Player, LinkedList<Gui>> getHistories() {
        return Collections.unmodifiableMap(histories);
    }
}
