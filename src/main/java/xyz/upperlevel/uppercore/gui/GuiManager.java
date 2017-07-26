package xyz.upperlevel.uppercore.gui;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.upperlevel.uppercore.Manager;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.gui.events.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * This manager is the class that manages the player histories in a stack-like system
 * it has multiple operations for interacting with the Gui stack:
 * open: appends the gui to the stack
 * close: clears the stack
 * back: removes the last gui from the stack
 * change: back + open
 * It's suggested NOT to chain operations when not needed, it could cause client flickering
 * <p>
 * This system does not support recursion
 */
@Getter
public class GuiManager extends Manager<GuiId> implements Listener {
    private final Map<Player, LinkedList<Gui>> histories = new HashMap<>();
    private boolean called = false;

    public GuiManager() {
        Bukkit.getPluginManager().registerEvents(this, Uppercore.get());
    }

    /**
     * Opens a Gui to a player, adding it to the stack. If the closeOthers parameter is specified it will removeHotbar the stack first
     *
     * @param player      the player that is opening the api
     * @param gui         the gui to be opened
     * @param closeOthers if give to true the GUI histories would be cleaned
     */
    public void open(Player player, Gui gui, boolean closeOthers) {
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
    public void open(Player player, Gui gui) {
        open(player, gui, false);
    }

    /**
     * Closes *ALL* the player's Guis, clearing his stack histories
     *
     * @param player the player
     */
    public void close(Player player) {
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

    public void closeAll() {
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
    public void back(Player player) {
        if (called) return;
        called = true;
        try {
            histories.computeIfPresent(player, (p, g) -> {
                //computeIfPresent is a method that, if the entry is found permits to the code to replace
                //the value with another one or, to remove it if the replaced value is null
                //In this code is used as a removeIf: if(the history is empty) remove /*return null*/ else don't remove /*return history*

                if (g.isEmpty())//This should never be true
                    return null;//remove the history from the map
                Gui oldGui = g.pop();
                Gui gui = g.peek();

                GuiBackEvent event = new GuiBackEvent(player, gui, oldGui);
                Bukkit.getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    g.push(oldGui);
                    return g;//Don't remove
                }

                oldGui.onClose(player);
                if (!g.isEmpty()) {
                    gui.onOpen(player);
                    gui.show(player);
                    return g;//Don't remove
                } else {
                    player.closeInventory();
                    return null;//remove
                }
            });
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
    public void change(Player player, Gui gui) {
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
    public void onClick(InventoryClickEvent event) {
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

    public void reprint(Player player) {
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
    public LinkedList<Gui> getHistory(Player player) {
        return histories.get(player);
    }

    private LinkedList<Gui> getOrCreate(Player player) {
        return histories.computeIfAbsent(player, (pl) -> new LinkedList<>());
    }

    public Map<Player, LinkedList<Gui>> getHistories() {
        return Collections.unmodifiableMap(histories);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        close(e.getPlayer());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getPlayer() instanceof Player && !called) {
            //Cannot call Inventory actions in an inventory event
            Bukkit.getScheduler().runTaskLater(
                    Uppercore.get(),
                    () -> close((Player) e.getPlayer()),
                    0
            );
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == e.getInventory())
            onClick(e);
        if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            if (getHistory((Player) e.getWhoClicked()) != null)
                e.setCancelled(true);
        }
    }
}
