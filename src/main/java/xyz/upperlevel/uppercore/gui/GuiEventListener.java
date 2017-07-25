package xyz.upperlevel.uppercore.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.hotbar.HotbarManager;
import xyz.upperlevel.uppercore.hotbar.HotbarView;

import static xyz.upperlevel.uppercore.Uppercore.*;
import static xyz.upperlevel.uppercore.Uppercore.hotbars;

public class GuiEventListener implements Listener {


    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    protected void onPlayerClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        HotbarView h = hotbars().view(player);
        if (e.getAction() == InventoryAction.HOTBAR_SWAP || e.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD) {
            //isIconSlot is really fast but it only works with the main player inventory where the first 9 indexes are
            //for the hotbar. this isn't exactly the main inventory but it works as well
            if (h != null && h.isIconSlot(e.getHotbarButton())) {
                e.setCancelled(true);
                return;
            }
        }

        if (h != null && (h.isIcon(e.getCurrentItem()) || h.isIcon((e.getCursor())))) {//TODO use normal slots
            e.setCancelled(true);
            player.updateInventory();
        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    protected void onPlayerQuit(PlayerQuitEvent e) {
        hotbars().view(e.getPlayer()).clear();
    }


    // TODO
    //@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    //protected void onCustomBookOpen(CustomBookOpenEvent e) {
    //    GuiSystem.closeGui(e.getPlayer());
    //}

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        try {
            if (e.getHand() != EquipmentSlot.HAND)
                return;
        } catch (Error ignored) {
        }
        if (e.getAction() != Action.PHYSICAL)
            hotbars().onClick(e); // this method cancels the event by himself
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        HotbarView h = hotbars().view(e.getPlayer());
        if (h != null && h.isIconSlot(e.getPlayer().getInventory().getHeldItemSlot()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        hotbars().view(e.getPlayer()).print();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        hotbars().view(e.getEntity());
    }
}
