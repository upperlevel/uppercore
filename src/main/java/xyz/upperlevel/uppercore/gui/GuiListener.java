package xyz.upperlevel.uppercore.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.upperlevel.uppercore.Uppercore;

public class GuiListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        GuiSystem.close(e.getPlayer());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getPlayer() instanceof Player && !GuiSystem.isCalled()) {
            //Cannot call Inventory actions in an inventory event
            Bukkit.getScheduler().runTaskLater(
                    Uppercore.get(),
                    () -> GuiSystem.back((Player) e.getPlayer()),
                    0
            );
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == e.getInventory())
            GuiSystem.onClick(e);
        if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            if (GuiSystem.getHistory((Player) e.getWhoClicked()) != null)
                e.setCancelled(true);
        }
    }
}
