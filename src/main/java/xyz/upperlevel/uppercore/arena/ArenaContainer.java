package xyz.upperlevel.uppercore.arena;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.message.Message;
import xyz.upperlevel.uppercore.util.WorldUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.util.Locale.ENGLISH;

public class ArenaContainer {
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
    private Arena arena;

    public ArenaContainer(String id) {
        this.id = id.toLowerCase(ENGLISH);
        this.signature = getSignature(id);
        this.world = Bukkit.getWorld(signature);
        if (world == null) {
            throw new IllegalStateException("Arena's world isn't loaded, or doesn't exist: " + signature);
        }
    }

    private ArenaContainer(String id, ConfigurationSection section) {
        this(id);
    }

    private void setArena(Arena arena) {
        this.arena = arena;
        arena.setContainer(this);
    }

    public boolean isReady() {
        return arena.isReady();
    }

    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {{
            put("arena", arena.serialize());
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
     * Spawns the merchants and holograms above those, and generators.
     * Must be called after the arena is set up.
     */
    public void decorate() {
        arena.decorate();
    }

    /**
     * Removes all the merchants and holograms above those, and generators.
     * After it's called, the arena can be saved.
     */
    public void vacate() {
        arena.vacate();
    }

    /**
     * Sets arena enabled or not.
     * When enabled, all needed entities are spawned and the arena is set in LobbyPhase.
     */
    public void setEnabled(boolean enabled, Phase lobbyPhase) {
        if (!isReady()) {
            throw new IllegalStateException("Arena can't be enabled, without it's ready.");
        }
        this.enabled = enabled;
        if (enabled) {
            decorate();
            phaseManager.setPhase(lobbyPhase);
        } else {
            for (Player player : new ArrayList<>(players)) {
                quit(player);
            }
            phaseManager.setPhase(null);
            vacate();
        }
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

    public static ArenaContainer deserialize(String id, ConfigurationSection section) {
        return new ArenaContainer(id, section);
    }

    public static String getSignature(String id) {
        return Uppercore.getPlugin().getName() + "." + id.toLowerCase(ENGLISH);
    }

    public static ArenaContainer create(String id) {
        WorldUtil.createEmptyWorld(getSignature(id));
        return new ArenaContainer(id);
    }

    //================================================================================
    // Game
    //================================================================================

    private final List<Player> players = new ArrayList<>();

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

    public void join(Player player) {
        if (!enabled) {
            throw new IllegalStateException("An arena not enabled can't be joined.");
        }
        players.add(player);
        phaseManager.onPlayerJoin(player);
        player.teleport(arena.getLobby());
    }

    public void quit(Player player) {
        players.remove(player);
        phaseManager.onPlayerQuit(player);
        player.teleport(getFreeWorld().getSpawnLocation()); // TODO temp
    }
}
