package xyz.upperlevel.uppercore.arena;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.arena.event.ArenaQuitEvent.ArenaQuitReason;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.gui.ChestGui;
import xyz.upperlevel.uppercore.gui.ConfigIcon;
import xyz.upperlevel.uppercore.gui.Icon;
import xyz.upperlevel.uppercore.itemstack.UItem;
import xyz.upperlevel.uppercore.util.Dbg;
import xyz.upperlevel.uppercore.util.WorldUtil;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.bukkit.ChatColor.RED;

public class ArenaManager implements Listener {
    public static final File ARENAS_FOLDER = new File(Uppercore.getPlugin().getDataFolder(), "arenas");
    public static ArenaManager instance = new ArenaManager();

    private final Map<String, Arena> byId = new HashMap<>();

    public ArenaManager() {
        Bukkit.getPluginManager().registerEvents(this, Uppercore.plugin());
    }

    //================================================================================
    // Loading

    public <A extends Arena> void load(Class<A> arenaClass) {
        File[] files = ARENAS_FOLDER.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            String id = file.getName().substring(0, file.getName().lastIndexOf('.'));

            // First loads the arena world.
            String world = Arena.getSignature(id);
            WorldUtil.createEmptyWorld(world);

            // Then loads arena's data.
            Config cfg = Config.fromYaml(file);
            A arena = cfg.get(arenaClass);
            register(arena);

            // If the arena is ready, sets to enabled.
            if (arena.isReady()) {
                arena.setEnabled(true);
            }
        }
    }

    //================================================================================
    // Registering

    public void register(Arena arena) {
        byId.put(arena.getId(), arena);
    }

    public Arena get(String id) {
        return byId.get(id);
    }

    public Arena get(World world) {
        for (Arena arena : byId.values()) {
            if (world.equals(arena.getWorld())) {
                return arena;
            }
        }
        return null;
    }

    public Arena get(Player player) {
        for (Arena arena : byId.values()) {
            if (arena.getPlayers().contains(player)) {
                return arena;
            }
        }
        return null;
    }

    public Arena getArenaByJoinSign(Block block) {
        for (Arena arena : byId.values()) {
            if (arena.getJoinSign(block) != null)
                return arena;
        }
        return null;
    }

    public void destroy(Arena arena) {
        byId.remove(arena.getId());
        if (arena.getWorld() != null) {
            arena.destroy();
        }
    }

    public Collection<Arena> getArenas() {
        return byId.values();
    }

    public void unload() {
        for (Arena arena : byId.values()) {
            arena.unload();
        }
    }

    public ChestGui getJoinGui() {
        Config cfg = Uppercore.get().getConfig().getConfig("arenas.join-gui");
        ChestGui.Builder builder = ChestGui.builder((byId.size() / 9 + 1) * 9)
                .title(cfg.getStringRequired("title"));
        int slot = 0;
        for (Arena arena : byId.values()) {
            if (!arena.isEnabled()) // If the arena isn't enabled doesn't show it.
                continue;
            // Otherwise shows the arena in the join-gui inventory, green if playable, red if not.
            UItem item = arena.isPlayable() ? cfg.getUItemRequired("playable-arena") : cfg.getUItemRequired("ingame-arena");
            item.setPlaceholders(arena.getPlaceholders());
            builder.set(slot, ConfigIcon.of(item, player -> {
                Arena old = ArenaManager.get().get(player);
                if (old != null)
                    old.quit(player, ArenaQuitReason.GAME_QUIT);
                arena.join(player);
            }));
            slot++;
        }
        return builder.build();
    }


    //================================================================================
    // Events

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent e) {
        if (Arena.mode.equals("bungee")) {
            Arena arena = get(Arena.mainArenaName);
            if (arena == null) {
                Uppercore.logger().severe("No arena found with name: " + Arena.mainArenaName);
                return;
            }
            Player p = e.getPlayer();
            if (!arena.join(p)) {
                Arena.onQuitHandler.handle(p);
            }
        }
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent e) {
        Arena arena = get(e.getPlayer());
        if (arena != null) {
            arena.quit(e.getPlayer(), ArenaQuitReason.GAME_QUIT);
        }
    }

    @EventHandler
    private void onJoinSignClick(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && block != null) {
            if (block.getState() instanceof Sign) {
                Arena arena = getArenaByJoinSign(block);
                // If the sign is a join-sign and the player isn't within any arena, then can join.
                if (arena != null && arena.isEnabled() && get(e.getPlayer()) == null) {
                    arena.join(e.getPlayer());
                }
            }
        }
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent e) {
        Arena arena = getArenaByJoinSign(e.getBlock());
        if (arena != null) {
            arena.removeJoinSign(e.getBlock());
            e.getPlayer().sendMessage(RED + String.format("You broke a join-sign of the arena '%s'.", arena.getId()));
        }
    }

    public static ArenaManager get() {
        return instance;
    }
}
