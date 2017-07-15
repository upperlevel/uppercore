package xyz.upperlevel.uppercore.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.gui.hotbar.HotbarManager;
import xyz.upperlevel.uppercore.gui.hotbar.HotbarView;

public class GuiEventListener implements Listener {


    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    protected void onPlayerClick(InventoryClickEvent e) {
        HotbarView h = HotbarManager.get((Player) e.getWhoClicked());
        if (e.getAction() == InventoryAction.HOTBAR_SWAP || e.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD) {
            //isIconSlot is really fast but it only works with the main player inventory where the first 9 indexes are
            //for the hotbar. this isn't exactly the main inventory but it works as well
            if (h != null && h.isIconSlot(e.getHotbarButton())) {
                e.setCancelled(true);
                return;
            }
        }

        if (e.getClickedInventory() == e.getInventory())  //getInventory returns always the top inv. so it's like saying that the clicked inventory must be the top one
            GuiManager.onClick(e);

        if (h != null && (h.isIcon(e.getCurrentItem()) || h.isIcon((e.getCursor())))) {//TODO use normal slots
            e.setCancelled(true);
            ((Player) e.getWhoClicked()).updateInventory();
        }
        if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            if (GuiManager.get((Player) e.getWhoClicked()) != null)
                e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    protected void onPlayerQuit(PlayerQuitEvent e) {
        HotbarManager.get(e.getPlayer()).clear();
        GuiManager.close(e.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    protected void onInventoryClose(InventoryCloseEvent e) {
        if (e.getPlayer() instanceof Player && !GuiManager.isCalled()) {
            //Cannot call Inventory actions in an inventory event
            Bukkit.getScheduler().runTaskLater(
                    Uppercore.get(),
                    () -> GuiManager.back((Player) e.getPlayer()),
                    0
            );
        }
    }

    // TODO
    //@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    //protected void onCustomBookOpen(CustomBookOpenEvent e) {
    //    GuiManager.close(e.getPlayer());
    //}

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        try {
            if (e.getHand() != EquipmentSlot.HAND)
                return;
        } catch (Error ignored) {
        }
        if (e.getAction() != Action.PHYSICAL)
            HotbarManager.onClick(e); // this method cancels the event by himself
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        HotbarView h = HotbarManager.get(e.getPlayer());
        if (h != null && h.isIconSlot(e.getPlayer().getInventory().getHeldItemSlot()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        HotbarManager.get(e.getPlayer()).print();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        HotbarManager.get(e.getEntity());
    }
}
