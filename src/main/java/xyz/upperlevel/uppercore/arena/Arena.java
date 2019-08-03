package xyz.upperlevel.uppercore.arena;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.arena.events.ArenaJoinEvent;
import xyz.upperlevel.uppercore.arena.events.ArenaQuitEvent;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.message.Message;
import xyz.upperlevel.uppercore.util.LocUtil;
import xyz.upperlevel.uppercore.util.WorldUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static java.util.Locale.ENGLISH;

public class Arena {
    //================================================================================
    // Properties
    //================================================================================

    @Getter
    private final String id;

    @Getter
    private final String signature;

    @Getter
    private final World world;

    @Getter
    private boolean enabled;

    @Getter
    private final PhaseManager phaseManager = new PhaseManager();

    @Getter
    @Setter
    private Location lobby;

    public Arena(String id) {
        this.id = id.toLowerCase(ENGLISH);
        this.signature = getSignature(id);
        this.world = Bukkit.getWorld(signature);
        if (world == null) {
            throw new IllegalStateException("Arena's world isn't loaded, or doesn't exist: " + signature);
        }
    }

    @ConfigConstructor
    public Arena(
            @ConfigProperty("id") String id,
            @ConfigProperty("lobby") Location lobby) {
        this(id);
        this.lobby = lobby;
    }

    public boolean isReady() {
        return lobby != null;
    }

    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {{
            put("id", id);
            put("signature", signature);
            put("lobby", LocUtil.serialize(lobby));
        }};
    }

    /**
     * Saves the arena world and writes data to arena's yml file.
     */
    public void save() throws IOException {
        if (enabled) {
            throw new IllegalStateException("Arena can't be saved while it's enabled.");
        }

        ArenaManager.ARENAS_FOLDER.mkdirs();
        File file = new File(ArenaManager.ARENAS_FOLDER, id + ".yml");
        file.createNewFile();

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        yaml.set(id, serialize());
        yaml.save(file);
    }

    /**
     * This method should spawn all the needed entities within this arena.
     * It's called when the arena is enabled.
     */
    public void decorate() {
    }

    /**
     * This method must remove all the entities within this arena.
     * It's called when the arena is disabled.
     */
    public void vacate() {
    }

    /**
     * Sets arena enabled or not.
     * When enabled, all needed entities are spawned and the arena is set in LobbyPhase.
     */
    public void setEnabled(boolean enabled) {
        if (!isReady()) {
            throw new IllegalStateException("Arena can't be enabled, without it's ready.");
        }
        this.enabled = enabled;
        if (enabled) {
            decorate();
            phaseManager.setPhase(getEntryPhase());
        } else {
            for (Player player : new ArrayList<>(players)) {
                quit(player);
            }
            phaseManager.setPhase(null);
            vacate();
        }
    }

    public Phase getEntryPhase() {
        return null;
    }

    /**
     * Removes all the entities spawned and unloads its world (without saving).
     */
    public void unload() {
        vacate();
        for (Player player : world.getPlayers()) {
            quit(player);
        }
        WorldUtil.unloadWorld(world);
    }

    /**
     * Destroys the arena from the face of earth.
     * This method shouldn't be called directly but through ArenaManager.
     */
    public void destroy() {
        unload();
        WorldUtil.deleteWorld(world);
        new File(ArenaManager.ARENAS_FOLDER, id + ".yml").delete();
    }

    public static String getSignature(String id) {
        return Uppercore.getPlugin().getName() + "." + id.toLowerCase(ENGLISH);
    }

    public static <A extends Arena> A create(Class<A> arenaClass, String id) {
        WorldUtil.createEmptyWorld(getSignature(id));
        try {
            return arenaClass.getConstructor(String.class).newInstance(id);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException("An Arena implementation must always have a constructor like ArenaImpl(String id).");
        }
    }

    //================================================================================
    // Game
    //================================================================================

    private final List<Player> players = new ArrayList<>();

    public boolean hasPlayer(Player player) {
        return players.contains(player);
    }

    public void broadcast(Message message, PlaceholderRegistry placeholders) {
        message.broadcast(players, placeholders);
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    private World getFreeWorld() {
        World free = null;
        for (World world : Bukkit.getWorlds()) {
            if (ArenaManager.get().get(world) == null) {
                // Gets the first free world, prefers normal worlds to nether/end.
                if (free == null || free.getEnvironment() != World.Environment.NORMAL) {
                    free = world;
                }
            }
        }
        return free;
    }

    public boolean join(Player player) {
        if (!enabled) {
            throw new IllegalStateException("An arena not enabled can't be joined.");
        }
        ArenaJoinEvent event = new ArenaJoinEvent(player, this);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        players.add(player);
        return true;
    }

    public void quit(Player player) {
        ArenaQuitEvent event = new ArenaQuitEvent(player, this);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        players.remove(player);
        player.teleport(getFreeWorld().getSpawnLocation()); // TODO temp
    }
}
