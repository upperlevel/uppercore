package xyz.upperlevel.uppercore.gui.hotbar;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.command.argument.ArgumentParserSystem;
import xyz.upperlevel.uppercore.gui.Icon;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static xyz.upperlevel.uppercore.util.RegistryUtil.adaptId;
import static xyz.upperlevel.uppercore.util.RegistryUtil.obtainId;

public class HotbarSystem {

    private static final Map<String, Hotbar> hotbars = new HashMap<>();
    private static final Map<Plugin, HotbarRegistry> registries = new HashMap<>();
    private static final Map<Player, HotbarView> views = new HashMap<>();

    private HotbarSystem() {
    }

    public static void initialize() {
        Bukkit.getOnlinePlayers().forEach(HotbarSystem::joinPlayer);
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

        ArgumentParserSystem.register();
    }

    public static void register(Plugin plugin, String id, Hotbar hotbar) {
        hotbars.put(obtainId(plugin, id), hotbar);
    }

    public static void register(Plugin plugin, HotbarRegistry registry) {
        registries.put(plugin, registry);
    }

    public static HotbarRegistry getRegistry(Plugin plugin) {
        return registries.get(plugin);
    }

    /**
     * Gets hotbar of plugin from id.
     *
     * @param plugin the plugin
     * @param id     the id
     * @return the hotbar found
     */
    public static Hotbar getHotbar(Plugin plugin, String id) {
        HotbarRegistry reg = registries.get(plugin);
        if (reg != null)
            return reg.get(id);
        return hotbars.get(id);
    }

    /**
     * Gets hotbar from formatted id {@code ("[plugin]:[id]")}.
     *
     * @param id the formatted id
     * @return the hotbar found
     */
    public static Hotbar getHotbar(String id) {
        return hotbars.get(adaptId(id));
    }

    public static Collection<Hotbar> getHotbars() {
        return hotbars.values();
    }

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
     * Gets a hotbar view by its player.
     *
     * @param player the player holding the hotbar
     * @return the hotbar held
     */
    public static HotbarView view(Player player) {
        return views.computeIfAbsent(player, HotbarView::new);
    }

    /**
     * Checks if the given player is holding the given hotbar.
     *
     * @param player the player
     * @param hotbar the hotbar
     * @return true if is holding the passed hotbar, otherwise false
     */
    public static boolean isHolding(Player player, Hotbar hotbar) {
        return view(player).isHolding(hotbar);
    }

    /**
     * Removes an hotbar from a player.
     *
     * @param player the player
     */
    public static void remove(Player player) {
        view(player).clear();
    }

    public static boolean onClick(PlayerInteractEvent event) {
        if (onClick(event.getPlayer(), event.getPlayer().getInventory().getHeldItemSlot())) {
            event.setCancelled(true);
            return true;
        }
        return false;
    }

    public static boolean onClick(Player player, int slot) {
        Icon icon = view(player).getIcon(slot);
        if (icon == null || icon.getLink() == null)
            return false;
        icon.getLink().run(player);
        return true;
    }

    public static void clearAll() {
        views.keySet().forEach(HotbarSystem::remove);
        views.clear();
    }

    public static HotbarRegistry subscribe(Plugin plugin) {
        return new HotbarRegistry(plugin);
    }
}
