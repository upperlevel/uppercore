package xyz.upperlevel.uppercore.hotbar;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import xyz.upperlevel.uppercore.Manager;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.gui.ConfigIcon;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HotbarManager extends Manager<HotbarId> implements Listener {
    private final Map<Player, HotbarView> views = new HashMap<>();
    private final Set<HotbarId> onJoin = new HashSet<>();

    public HotbarManager() {
        Bukkit.getOnlinePlayers().forEach(this::initialize);
        Bukkit.getPluginManager().registerEvents(this, Uppercore.get());
    }

    private void initialize(Player player) {
        HotbarView v = new HotbarView(player);
        views.put(player, v);
        for (HotbarId onJoin : this.onJoin) {
            v.addHotbar(onJoin.get());
        }
    }

    private void destroy(Player player) {
        HotbarView v = views.remove(player);
        if (v != null) v.clear();
    }

    @Override
    public void register(HotbarId entry) {
        super.register(entry);
        onJoin.add(entry);
    }

    @Override
    public HotbarId unregister(String id) {
        HotbarId entry = super.unregister(id);
        if (entry != null)
            onJoin.remove(entry);
        return entry;
    }

    /**
     * Gets a hotbar view by its player.
     *
     * @param player the player holding the hotbar
     * @return the hotbar held
     */
    public HotbarView view(Player player) {
        return views.computeIfAbsent(player, HotbarView::new);
    }

    /**
     * Checks if the given player is holding the given hotbar.
     *
     * @param player the player
     * @param hotbar the hotbar
     * @return true if is holding the passed hotbar, otherwise false
     */
    public boolean isHolding(Player player, Hotbar hotbar) {
        return view(player).isHolding(hotbar);
    }

    /**
     * Removes an hotbar from a player.
     *
     * @param player the player
     */
    public void remove(Player player) {
        view(player).clear();
    }

    public boolean onClick(PlayerInteractEvent event) {
        if (onClick(event.getPlayer(), event.getPlayer().getInventory().getHeldItemSlot())) {
            event.setCancelled(true);
            return true;
        }
        return false;
    }

    public boolean onClick(Player player, int slot) {
        ConfigIcon icon = view(player).getIcon(slot);
        if (icon == null || icon.getLink() == null)
            return false;
        icon.getLink().run(player);
        return true;
    }

    public void clearAll() {
        views.keySet().forEach(this::remove);
        views.clear();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        initialize(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        destroy(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        HotbarView h = view(player);
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

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        try {
            if (e.getHand() != EquipmentSlot.HAND)
                return;
        } catch (Error ignored) {
        }
        if (e.getAction() != Action.PHYSICAL)
            onClick(e); // this method cancels the event by himself
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        HotbarView h = view(e.getPlayer());
        if (h != null && h.isIconSlot(e.getPlayer().getInventory().getHeldItemSlot()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        view(e.getPlayer()).print();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        view(e.getEntity());
    }
}
