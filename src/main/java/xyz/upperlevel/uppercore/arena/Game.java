package xyz.upperlevel.uppercore.arena;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.util.LocUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * This class contains everything game-related, it listens for signs, join-events.
 * It manages the ArenaManager and the hub.
 */
public class Game implements Listener {
    @Getter
    private final String name;

    @Getter
    private final Plugin plugin;

    @Getter
    @Setter
    private File storageFile;

    @Getter
    @Setter
    private ArenaManager arenaManager;

    @Getter
    @Setter
    private Location hub;

    @Getter
    @Setter
    private Arena defaultArena;

    public Game(
            @NonNull Plugin plugin,
            @NonNull File storageFile,
            @NonNull ArenaManager arenaManager,
            Location hub,
            Arena defaultArena) {
        this.name = plugin.getName().toLowerCase(Locale.ENGLISH).replace(" ", "_");
        this.plugin = plugin;
        this.storageFile = storageFile;

        this.arenaManager = arenaManager;
        this.hub = hub;
        this.defaultArena = defaultArena;
    }

    public Game(
            @NonNull Plugin plugin,
            @NonNull File storageFile,
            @NonNull ArenaManager arenaManager) {
        this(plugin, storageFile, arenaManager, null, null);
    }

    public void start() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void close() throws IOException {
        HandlerList.unregisterAll(this);
        arenaManager.dismiss();

        save();
    }

    /* --------------------------------------------------------------------------------- Event */

    @EventHandler
    protected void onPlayerJoin(PlayerJoinEvent e) {
        if (defaultArena != null) {
            defaultArena.join(e.getPlayer());
        } else if (hub != null) {
            e.getPlayer().teleport(hub);
        }
    }

    @EventHandler
    protected void onPlayerClickSign(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && block != null && block.getState() instanceof Sign) {
            Arena arena = arenaManager.getArenaBySign(block);
            if (arena != null && !arena.getPlayers().contains(e.getPlayer())) {
                arena.join(e.getPlayer());
            }
        }
    }

    /* --------------------------------------------------------------------------------- Storage */

    public void save() throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("arenas", arenaManager.serialize());
        if (hub != null) {
            data.put("hub", LocUtil.serialize(hub));
        }
        if (defaultArena != null) {
            data.put("default-arena", defaultArena.getId());
        }
        storageFile.getParentFile().mkdirs();
        storageFile.createNewFile();
        Yaml yaml = new Yaml();
        yaml.dump(data, new FileWriter(storageFile));
    }

    public static Game load(Plugin plugin, ArenaFactory arenaFactory) {
        File storageFile = new File(plugin.getDataFolder(), "game_data.yml");
        if (storageFile.exists()) {
            Config config = Config.fromYaml(storageFile);
            ArenaManager arenaManager = ArenaManager.load(plugin, config.getMapList("arenas"), arenaFactory);
            return new Game(
                    plugin,
                    storageFile,
                    arenaManager,
                    config.getLocation("hub", null),
                    arenaManager.getArena(config.getString("default-arena", null))
            );
        } else {
            return new Game(plugin, storageFile, new ArenaManager(plugin));
        }
    }
}
