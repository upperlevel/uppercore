package xyz.upperlevel.uppercore.arena;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.arena.events.ArenaQuitEvent.ArenaQuitReason;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.util.WorldUtil;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ArenaManager implements Listener {
    public static final File ARENAS_FOLDER = new File(Uppercore.getPlugin().getDataFolder(), "arenas");
    public static ArenaManager instance = new ArenaManager();

    private final Map<String, Arena> byId = new HashMap<>();

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
        Bukkit.getPluginManager().registerEvents(this, Uppercore.plugin());
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

    public Arena getArena(Sign joinSign) {
        for (Arena arena : byId.values()) {
            if (arena.getJoinSigns().contains(joinSign)) {
                return arena;
            }
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

    //================================================================================
    // Events

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
                Arena arena = getArena((Sign) block.getState());
                // If the sign is a join-sign and the player isn't within any arena, then can join.
                if (arena != null && arena.isEnabled() && get(e.getPlayer()) == null) {
                    arena.join(e.getPlayer());
                }
            }
        }
    }

    public static ArenaManager get() {
        return instance;
    }
}
